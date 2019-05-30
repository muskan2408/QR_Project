package com.qrtool.qrproject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.client.android.Intents;
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
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.qrtool.qrproject.MainActivity.REQUEST_CODE_QR_SCAN;

public class ScanQr extends AppCompatActivity {

    Bitmap imageBitmap;
    ImageView image;
    Button proceed;
    double longitude;
    double latitude;
    String dateVal;
    String time;
    String token;
    Location location;
    ProgressDialog progressDialog;
    SessionManagement sessionManagement;
    Model model;

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    Gson gson = new GsonBuilder().setLenient().create();
    OkHttpClient client = new OkHttpClient();
    public static Retrofit retrofit = builder.build();
    RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);


    private final String LOGTAG = "QRCScanner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

//        longitude = getIntent().getDoubleExtra("longitude",longitude);
//        latitude = getIntent().getDoubleExtra("latitude",latitude);
        sessionManagement = new SessionManagement(this);

        HashMap<String, String> user = sessionManagement.getUserDetails();

        token = user.get(SessionManagement.KEY_TOKEN);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(ScanQr.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ScanQr.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }


        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
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
        if (ActivityCompat.checkSelfPermission(ScanQr.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ScanQr.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        DateFormat df = new SimpleDateFormat("d MMM yyyy");
        dateVal = df.format(Calendar.getInstance().getTime());

        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);
        time = formattedDate;

        byte[] byteArray = getIntent().getByteArrayExtra("imageByte");
        imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        image = (ImageView) findViewById(R.id.imageAtScan);
        image.setImageBitmap(imageBitmap);

        proceed = (Button) findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScanQr.this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);



                progressDialog=new ProgressDialog(ScanQr.this);
                progressDialog.setTitle("Storing Image");
                progressDialog.setMessage("Please wait while we submit your parcel's image");
                progressDialog.setCancelable(false);
                progressDialog.show();

                startSubmit(imageBitmap, longitude,latitude,dateVal,time);

            }


        });


    }

    private void startSubmit(final Bitmap imageBitmap, final double longitude,final double latitude,final String date,final String time) {


        Call<Model> call=retrofitInterface.scanqr(imageBitmap,longitude,latitude,date,time,token);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, retrofit2.Response<Model> response) {

                progressDialog.dismiss();
                model=response.body();

//             Log.d("check",String.valueOf(model.getSuccess()));
                if(model.getSuccess() && model.getMsg().equals("scanned image successfully stored in database"))
                {
                    progressDialog.cancel();
                    Toast.makeText(ScanQr.this,"Image stored",Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));

                    return ;
                }


            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {

                Toast.makeText(ScanQr.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("TAG", "error: " + t);
                progressDialog.cancel();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
        {
            Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(ScanQr.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent i=new Intent(ScanQr.this,MainActivity.class);
                                startActivity(i);
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG,"Have scan result in your app activity :"+ result);
            AlertDialog alertDialog = new AlertDialog.Builder(ScanQr.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent i=new Intent(ScanQr.this,MainActivity.class);
                            startActivity(i);
                        }
                    });
            alertDialog.show();

            Call<Model> call=retrofitInterface.scanresult(result,token);
            call.enqueue(new Callback<Model>()
            {
                @Override
                public void onResponse(Call<Model> call, Response<Model> response) {
                    if(model.getSuccess())
                    {
                        progressDialog.cancel();
                        Toast.makeText(ScanQr.this,"scan Result Stored",Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                        return ;
                    }

                }

                @Override
                public void onFailure(Call<Model> call, Throwable t) {


                }
            });
//            Intent i=new Intent(ScanQr.this,MainActivity.class);
//            startActivity(i);
        }
//
//        Intent i=new Intent(ScanQr.this,MainActivity.class);
//        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}
