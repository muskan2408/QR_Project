package com.qrtool.qrproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qrtool.qrproject.Retrofit.RetrofitInterface;
import com.qrtool.qrproject.util.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GenerateQR extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView takeImage;
    ImageView takePicture;
    ImageView packagePicture;
    String  token;
    private PlaceAutocompleteAdapter mplaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;

    private static final LatLngBounds LAT_LNG_BOUNDS=new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));

    TextInputEditText productdesc, receivermob, receivername,packageName;
    private AutoCompleteTextView receiverAdd;

    Button submit;
    ProgressDialog progressDialog;
    Model model;
    Bitmap imageBitmap;
    SessionManagement session;
    Location location;

    double longitudeVal;
    double latitudeVal;
    String date;
    //String token;
    String time;
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    Gson gson = new GsonBuilder().setLenient().create();
    OkHttpClient client = new OkHttpClient();
    public static Retrofit retrofit = builder.build();
    RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generatorqr);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,  this)
                .build();

        packageName=(TextInputEditText)findViewById(R.id.packageName);

        takeImage = (TextView) findViewById(R.id.captureImageAtGeneration);
        takePicture = (ImageView) findViewById(R.id.takePicture);
        packagePicture = (ImageView) findViewById(R.id.packagepicture);

        packagePicture.setVisibility(View.GONE);

        productdesc = (TextInputEditText) findViewById(R.id.productdesc);
        receiverAdd=(AutoCompleteTextView) findViewById(R.id.input_search);
        receivermob = (TextInputEditText) findViewById(R.id.rmobile);
        receivername = (TextInputEditText) findViewById(R.id.rname);
        submit = (Button) findViewById(R.id.submit);

        session=new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();
        token=user.get(SessionManagement.KEY_TOKEN);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 0);
            }


        });

        receiverAdd.setOnItemClickListener(mAutoCompleteListener);
        mplaceAutocompleteAdapter =new PlaceAutocompleteAdapter(this,
                Places.getGeoDataClient(this,null),LAT_LNG_BOUNDS,null);
        receiverAdd.setAdapter(mplaceAutocompleteAdapter);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(GenerateQR.this);
                progressDialog.setTitle("Submitting QR");
                progressDialog.setMessage("Please wait while we submit your details");
                progressDialog.setCancelable(false);
                progressDialog.show();
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(GenerateQR.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GenerateQR.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(location!= null) {
                    longitudeVal = location.getLongitude();
                    latitudeVal = location.getLatitude();
                }


                final LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        longitudeVal = location.getLongitude();
                        latitudeVal = location.getLatitude();
                    }


                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                if (ActivityCompat.checkSelfPermission(GenerateQR.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GenerateQR.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
                //   token=getIntent().getStringExtra("model");


                startSubmit(packageName.getEditableText().toString(),productdesc.getEditableText().toString(),
                        receiverAdd.getEditableText().toString(),
                        receivermob.getEditableText().toString(),
                        receivername.getEditableText().toString(),
                        imageBitmap,
                        longitudeVal,
                       latitudeVal, date, time);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageBitmap = (Bitmap) data.getExtras().get("data");
        DateFormat df = new SimpleDateFormat("d MMM yyyy");
        date = df.format(Calendar.getInstance().getTime());

        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);
        time = formattedDate;

        if(requestCode==0)
        {
            packagePicture.setImageBitmap(imageBitmap);
            packagePicture.setVisibility(View.VISIBLE);

        }


    }

    private void startSubmit(final String packageName,final String productdescription, final String raddress, final String rmobile, final String rname, final Bitmap imageBitmap, final double longitude, final double latitude, final String date, final String time) {


        if(packageName.isEmpty())
        {
            progressDialog.dismiss();
            productdesc.setError("Package Name is required");
            productdesc.requestFocus();
            return;
        }

        if (productdescription.isEmpty()) {
            progressDialog.dismiss();
            productdesc.setError("Product Description is required");
            productdesc.requestFocus();
            return;
        }
        if (raddress.isEmpty()) {
            progressDialog.dismiss();
            receiverAdd.setError("Receiver's Address is required");
            receiverAdd.requestFocus();
            return;
        }
        if (rmobile.isEmpty()) {
            progressDialog.dismiss();
            receivermob.setError("Receiver's Mobile is required");
            receivermob.requestFocus();
            return;
        }
        if (rname.isEmpty()) {
            progressDialog.dismiss();
            receivername.setError("Receiver's Name is required");
            receivername.requestFocus();
            return;
        }
        if (rmobile.length() < 10) {
            progressDialog.dismiss();
            receivermob.setError("Enter valid mobile number");
            receivermob.requestFocus();
            return;
        }
        if (imageBitmap == null) {
            progressDialog.dismiss();
            return;
        }


        Call<Model> call = retrofitInterface.generateqr(rname, raddress, rmobile, productdescription, imageBitmap, longitude, latitude, date, time,token,packageName);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, retrofit2.Response<Model> response) {

                progressDialog.dismiss();
                model = response.body();

//             Log.d("check",String.valueOf(model.getSuccess()));
                if (model.getSuccess() && model.getMsg().equals("qrdata successfully stored in database")) {

                    String qrId = model.getQrId();

                    progressDialog.cancel();
                    Toast.makeText(GenerateQR.this,"Data Stored Successfully",Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                    Intent i=new Intent(GenerateQR.this,QrGenerate.class);
                    i.putExtra("ProductDescription",productdescription);
                    i.putExtra("ReceiverAddress",raddress);
                    i.putExtra("ReceiverMobile",rmobile);
                    i.putExtra("ReceiverName",rname);
                    i.putExtra("ImageCaptured",imageBitmap);
                    i.putExtra("longitude",longitude);
                    i.putExtra("latitude",latitude);
                    i.putExtra("date",date);
                    i.putExtra("time",time);
                    i.putExtra("qrid",qrId);
                    i.putExtra("packageName",packageName);

                    // i.putExtra("allvalues", model);
//                    i.putExtra("token",token);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    return ;
                }


            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {

                Toast.makeText(GenerateQR.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("TAG", "error: " + t);
                progressDialog.cancel();
            }
        });

    }
    private ResultCallback<? super PlaceBuffer> mUpdatePlaceDetailsCallback=new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess())
            {
              //  Log.d(TAG,"places query did not complete successfully "+places.getStatus().toString());
                places.release();
                return;
            }
            final Place place=places.get(0);
            try{
//                mPlace=new PlaceInfo();
//                mPlace.setName(place.getName().toString());
//                mPlace.setAddress(place.getAddress().toString());
//                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
//                //   mPlace.setAttributions(place.getName().toString());
//                mPlace.setId(place.getId().toString());
//                mPlace.setLatLng(place.getLatLng());
//                mPlace.setWebsiteUri(place.getWebsiteUri());
//                mPlace.setRating(place.getRating());
//                Log.d(TAG,"OnResult: place details"+mPlace.toString());

            }catch(NullPointerException e)
            {
              //  Log.e(TAG,"OnResult: NullPointerException"+e.getMessage());
            }
//            moveCamera(new LatLng(place.getViewport().getCenter().latitude,place.getViewport().getCenter().longitude),
//                    DEFAULT_ZOOM,mPlace);
//            Log.d(TAG,"OnResult:place details: "+place.getAttributions());
//            Log.d(TAG,"OnResult:place details: "+place.getViewport());
//            Log.d(TAG,"OnResult:place details: "+place.getAddress());
//            Log.d(TAG,"OnResult:place details: "+place.getPhoneNumber());
//            Log.d(TAG,"OnResult:place details: "+place.getLatLng());
//            Log.d(TAG,"OnResult:place details: "+place.getId());
//            Log.d(TAG,"OnResult:place details: "+place.getWebsiteUri());
//            Log.d(TAG,"OnResult:place details: "+place.getRating());
            places.release();
        }
    };

    private AdapterView.OnItemClickListener mAutoCompleteListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            // hideSoftKeyboard();
            final AutocompletePrediction item=mplaceAutocompleteAdapter.getItem(i);
            final String placeId=item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult=Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
