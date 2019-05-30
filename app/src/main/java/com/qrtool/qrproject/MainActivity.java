package com.qrtool.qrproject;

import android.Manifest;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.qrtool.qrproject.util.Model;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class
MainActivity extends AppCompatActivity {
     CardView qrscanner,qrgenerator,track,viewprofile;
    public static final int REQUEST_CODE_QR_SCAN = 101;
    Button logout;
    SessionManagement session;
    double longitude;
    double latitude;
    Button qrHistory;
    String date;
    String time;

    private final String LOGTAG = "QRCScanner-MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i=getIntent();
        final String token=i.getStringExtra("model");
        session=new SessionManagement(this);
        String userType=getIntent().getStringExtra("userType");

        HashMap<String, String> user = session.getUserDetails();

        String userTypeSession=user.get(SessionManagement.KEY_TYPE);
        qrgenerator=(CardView)findViewById(R.id.qrgeneratorcard);
        logout=(Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                session.logoutUser();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });
        viewprofile=(CardView)findViewById(R.id.profilecard);
        qrgenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,GenerateQR.class);
                i.putExtra("model",token);
                startActivity(i);
            }
        });
        viewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Profile.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        track=(CardView)findViewById(R.id.trackcard);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Maps.class);
                startActivity(i);
            }
        });
        qrscanner=(CardView)findViewById(R.id.scannercard);
        qrscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String permission = Manifest.permission.CAMERA;
                int res = getApplicationContext().checkCallingOrSelfPermission(permission);
                if (res != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(MainActivity.this)
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
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }else{
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Image of parcel required")
                            .setMessage("Take a picture first")
                            .setPositiveButton("OK",    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(i, 0);



                                }

                            })
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                  dialogInterface.dismiss();
                                }
                            })
                            .show();
            }}
        });

        qrHistory=(Button)findViewById(R.id.report);
        qrHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(MainActivity.this,QrHistory.class);
                startActivity(i);

            }
        });

        if(userTypeSession.equals("Courier") )
        {
            qrHistory.setVisibility(View.GONE);
            qrgenerator.setVisibility(View.GONE);
            qrscanner.setVisibility(View.VISIBLE);
        }

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            super.onActivityResult(requestCode, resultCode, data);
            if(data!=null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();


                DateFormat df = new SimpleDateFormat("d MMM yyyy");
                date = df.format(Calendar.getInstance().getTime());

                DateFormat dft = new SimpleDateFormat("HH:mm a");
                time = df.format(Calendar.getInstance().getTime());

                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
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

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);


                Intent intent = new Intent(MainActivity.this, ScanQr.class);
                intent.putExtra("imageByte", byteArray);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                startActivity(intent);
            }
        if(resultCode != Activity.RESULT_OK)
        {
            Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
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
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
    }
}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
        finish();
    }
}
