package com.qrtool.qrproject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.qrtool.qrproject.Retrofit.RetrofitInterface;
import com.qrtool.qrproject.util.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QrGenerate extends AppCompatActivity {
ImageView image;
Model model;
Button save;
ProgressDialog progressDialog;
    Gson gson = new GsonBuilder().setLenient().create();
    OkHttpClient client = new OkHttpClient();
    private static Retrofit.Builder builder=new Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static Retrofit retrofit=builder.build();
    RetrofitInterface retrofitInterface=retrofit.create(RetrofitInterface.class);
    public final static int QRcodeWidth = 500 ;
    private static final String IMAGE_DIRECTORY = "/QRcodeDemonuts";
    Bitmap bitmap ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generate);
        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
        Intent i=getIntent();
        String productdesc=i.getStringExtra("ProductDescription");
        String raddress=i.getStringExtra("ReceiverAddress");
        String rmobile=i.getStringExtra("ReceiverMobile");
        String rname=i.getStringExtra("ReceiverName");
        Log.d("dfgersfgggggggg",productdesc);
        image=(ImageView)findViewById(R.id.iv);
        try {
            progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Generating QR");
            progressDialog.setCancelable(false);
            BitMatrix bitMatrix=multiFormatWriter.encode(productdesc+ " "+raddress+ " "+rmobile+" "+rname,
                    BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            bitmap=barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);


            //  bitmap = TextToImageEncode(productdesc+ " "+raddress+ " "+rmobile+" "+rname);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        //  image.setImageBitmap(bitmap);
        progressDialog.cancel();

        save=(Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PdfDocument pdfDocument= new PdfDocument();
                PdfDocument.PageInfo pi;
                pi = new  PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(),1).create();
                PdfDocument.Page page=pdfDocument.startPage(pi);
                Canvas canvas=page.getCanvas();
                Paint paint =new Paint();
                paint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawPaint(paint);

                bitmap =Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(),true);
                paint.setColor(Color.BLACK);
                canvas.drawBitmap(bitmap,0,0,null);

                pdfDocument.finishPage(page);
                String root1 = Environment.getExternalStorageDirectory().toString();

                if (isStoragePermissionGranted()) { // check or ask permission
                    File myDir = new File(root1, "/saved_images");
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }

                //saving the pdf
               // File root = new File(Environment.getExternalStorageDirectory().toString(),"PDF Folder 12");
//                if (!root.exists())
//                {
//                    root.mkdir();
//                }
                File file=new File(myDir,"qr.pdf");

                try{
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    pdfDocument.writeTo(fileOutputStream);
                    Toast.makeText(QrGenerate.this,"PDF downloaded",Toast.LENGTH_SHORT).show();
                  //  Downloader.DownloadFile("http://www.nmu.ac.in/ejournals/aspx/courselist.pdf", file);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                pdfDocument.close();

            }}

        });

    }


    private Bitmap TextToImageEncode(String Value) throws WriterException {



        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);

        return bitmap;
    }



    private boolean isStoragePermissionGranted() {

        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
    }
