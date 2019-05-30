//
package com.qrtool.qrproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Maps extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    Location currentLocation;
    private static final float DEFAULT_ZOOM = 15f;
  //  private AutoCompleteTextView mSearchText;
    GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private static final int PLACE_PICKER_REQUEST = 1;
    private Marker mMarker;
    RelativeLayout searchbar;
    private PlaceAutocompleteAdapter mplaceAutocompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

     //   mSearchText = (AutoCompleteTextView)findViewById(R.id.input_search);

        getLocationPermission();

//        searchbar.setVisibility(View.GONE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST );
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();


        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       // ((Maps.this)getSupportActionBar().show();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    private void init() {

        Log.d(TAG,"initializing");
    //    mSearchText.setOnItemClickListener(mAutoCompleteListener);
        mplaceAutocompleteAdapter =new PlaceAutocompleteAdapter(this,
                Places.getGeoDataClient(this,null),LAT_LNG_BOUNDS,null);
    //    mSearchText.setAdapter(mplaceAutocompleteAdapter);

     //
    }

    private void geoLocate() {
        Log.d(TAG,"geolocating");

      //  String searchString=mSearchText.getText().toString();
        Geocoder geocoder=new Geocoder(this);
        List<Address> list=new ArrayList<>();
        try{
       //     list=geocoder.getFromLocationName(searchString,1);
        }catch(Exception e)
        {
            Log.e(TAG,"geoLocate: IOException"+e.getMessage());
        }

        if(list.size()>0)
        {
            Address address=list.get(0);
            Log.d(TAG,address.toString());
            //  Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "move the camera to: lat:" + latLng.latitude + " long:" + latLng.longitude);
//        getLocationPermission();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
        else if(title.equals("My Location")){
            {
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(title);
                mMap.addMarker(options);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
        }
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG,"move the camera to: lat:"+latLng.latitude+" long:"+latLng.longitude);
//        getLocationPermission();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        mMap.clear();

        if(placeInfo!=null)
        {
            try{
                String snippet="Address"+placeInfo.getAddress()+"\n" +
                        "Phone Number"+placeInfo.getPhoneNumber()+"\n" +
                        "Website"+placeInfo.getWebsiteUri()+"\n" ;
                String title="My Location";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(title);
                mMap.addMarker(options);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                erasePolylines();
//                getRouteMarker(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),placeInfo.getLatLng());


            }catch(NullPointerException e)
            {
                Log.e(TAG,"moveCamera: NullPointerException"+e.getMessage());
            }

        }
        else{

            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideSoftKeyboard();

    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);


        // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void getDeviceLocation() {

        Log.d(TAG, "getting current device location");

        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {

                com.google.android.gms.tasks.Task location = mfusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "found location!");
                            currentLocation = task.getResult();
                            Log.d("current location",String.valueOf(currentLocation.getLatitude()+currentLocation.getLongitude()));

                            getLocationPermission();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");


                          //  start=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());


                        } else {
                            Log.d(TAG, "current location is null");
                            Toast.makeText(Maps.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        MarkerOptions options1 = new MarkerOptions()
//                .position(latLng)
//                .title(placeInfo.getName())
//                .snippet(snippet);
//        mMarker = mMap.addMarker(options1);
            }
        } catch (SecurityException e) {

            Log.d(TAG, "GETDEVICELOCATION : securityException" + e.getMessage());
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "get Location Permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {

                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);

            }

        } else {

            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);

        }

    }

    private void initMap() {
        Log.d(TAG, "initialize a map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //this is one of the way if we didnt want to implement onMapCallBack interface
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                mMap=googleMap;
//            }
//        });
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Toast.makeText(getActivity(), "map is ready", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "map is ready");
                mMap = googleMap;

                if (mLocationPermissionGranted) {
                    getDeviceLocation();
                    if (ActivityCompat.checkSelfPermission(Maps.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Maps.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //getLocationPermission();
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    init();


                }

            }
        });
    }

    private AdapterView.OnItemClickListener mAutoCompleteListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //hideSoftKeyboard();
            final AutocompletePrediction item = mplaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
    private ResultCallback<? super PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {

                Log.d(TAG, "places query did not complete successfrully " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            try {

                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                //   mPlace.setAttributions(place.getName().toString());
                mPlace.setId(place.getId().toString());
                mPlace.setLatLng(place.getLatLng());
              //  mPlace.setWebsiteUri(place.getWebsiteUri());
                mPlace.setRating(place.getRating());

                Log.d(TAG, "OnResult: place details" + mPlace.toString());

            } catch (NullPointerException e) {
                Log.e(TAG, "OnResult: NullPointerException" + e.getMessage());

            }
            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude),
                    DEFAULT_ZOOM, mPlace);

            places.release();

        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onBackPressed() {
        Intent i=new Intent(Maps.this,MainActivity.class);
        startActivity(i);
    }

}
