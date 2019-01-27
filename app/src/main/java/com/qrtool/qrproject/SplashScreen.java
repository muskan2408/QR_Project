package com.qrtool.qrproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.qrtool.qrproject.Constants;
import com.qrtool.qrproject.Retrofit.RetrofitInterface;
import com.qrtool.qrproject.util.Model;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=4000;
    SessionManagement session;
    Gson gson = new GsonBuilder().setLenient().create();
    Model model;
    OkHttpClient client = new OkHttpClient();
    Retrofit.Builder builder=new Retrofit.Builder().baseUrl(Constants.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create(gson));
    Retrofit retrofit=builder.build();
    RetrofitInterface retrofitInterface=retrofit.create(RetrofitInterface.class);
   // PermissionManager permissionManager;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS=1;
    private static final String TAG="SplashActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
//        permissionManager=new PermissionManager() {};
//           permissionManager.checkAndRequestPermissions(this);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
//            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
//            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                    // TODO Auto-generated method stub
//                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivity(myIntent);
//                    //get gps
//                }
//            });
//            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                    // TODO Auto-generated method stub
//
//                }
//            });
//            dialog.show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions

                perms.put(FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (
                            perms.get(FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            ) {
                        Log.d(TAG, "location services permission granted");
                        // process the normal flow
                        Intent i = new Intent(SplashScreen.this, Options.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, FINE_LOCATION)) {
                            showDialogOK("Service Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
    private void explain(String msg){
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.exampledemo.parsaniahardik.marshmallowpermission")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!checkInternet()) {
            new AlertDialog.Builder(SplashScreen.this)
                    .setTitle("No Internet Connection")
                    .setMessage("Your device is not connected to Internet")
                    .setCancelable(false)
                    .setPositiveButton("Go To Settings", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                        }

                    })
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .show();


        } else   if(!checkLocation()){
            new AlertDialog.Builder(SplashScreen.this)
                    .setTitle("Unable to Access Location")
                    .setMessage("Turn on your location to move further")
                    .setCancelable(false)
                    .setPositiveButton("Go To Settings", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);

                        }

                    })
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .show();


        }else
            if(!checkCamera()){
                String permission = Manifest.permission.CAMERA;
   int res = getApplicationContext().checkCallingOrSelfPermission(permission);
                new AlertDialog.Builder(SplashScreen.this)
                        .setTitle("Camera Permission Required")
                        .setMessage("Allow permission in app settings")
                        .setPositiveButton("Go To App Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                System.exit(0);
                            }
                        })
                        .show();
            }

            else if(checkLocation()&& checkInternet()){
            Splash();
        }
    }

    private boolean checkCamera() {

      if(  ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
          return true;
      }
      return false;
    }


    private boolean checkLocation() {
        boolean gps_enabled = false;
//        if(context!=null) {

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
//                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
//                dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        // TODO Auto-generated method stub
//                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivity(myIntent);
//                        //get gps
//                    }
//                });
//                dialog.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        // TODO Auto-generated method stub
//                        finish();
//                        System.exit(0);
//
//
//                    }
//                });
//                dialog.show();
        }
        Log.e("Check gps_enabled", String.valueOf(gps_enabled));

//        }
        return gps_enabled;
    }
    private void Splash() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                session = new SessionManagement(getApplicationContext());
                // Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

                if(!SplashScreen.this.session.isLoggedIn())
                {
                    Log.e("TAG", "execterd");
                    Intent i=new Intent(SplashScreen.this,Options.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();


                }
                else{   HashMap<String, String> user = session.getUserDetails();

                    String token=user.get(SessionManagement.KEY_TOKEN);
                    Call<Model> call=retrofitInterface.session_manage(token);

                    call.enqueue(new Callback<Model>() {
                        @Override
                        public void onResponse(Call<Model> call, retrofit2.Response<Model> response) {

                            model=  response.body();// have your all data
                            boolean check=false;
                            if(model!=null)  {check=model.getSuccess();}

                            if((!check)||model.getSuccess()==null)
                            {
                                new AlertDialog.Builder(SplashScreen.this)
                                        .setTitle("Can't Connect to Server")
                                        .setCancelable(false)
                                        .setMessage("Can't Connect to server Please Check after some time")
                                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                                System.exit(0);
                                            }
                                        })
                                        .show();
                            }
                            else {
                                Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                                Log.e("TAG", "response 33: " + response.body());
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("Model", model);
                                startActivity(intent);
                                finish();
                            }
                        }
                        @Override
                        public void onFailure(Call<Model> call, Throwable t) {

                            Log.e("TAG", "response 33: " +t.getMessage());
                            if(t.getMessage().contains("connect"))
                            {
                                new AlertDialog.Builder(SplashScreen.this)
                                        .setMessage("Can't Connect to server Please Check after some time")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                                System.exit(0);
                                            }
                                        }).show();
                            }

                            Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();

                        }

                    });}

            }
        }, SPLASH_TIME_OUT);

    }


    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
        super.onBackPressed();
    }

    public boolean checkInternet() {
        boolean mobileNwInfo;
        ConnectivityManager conxMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        try {
            mobileNwInfo = conxMgr.getActiveNetworkInfo().isConnected();
        } catch (NullPointerException e) {
            mobileNwInfo = false;
        }
        return mobileNwInfo;
    }


}
