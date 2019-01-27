package com.qrtool.qrproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qrtool.qrproject.Retrofit.RetrofitInterface;
import com.qrtool.qrproject.util.Model;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GenerateQR extends AppCompatActivity {
     TextInputEditText productdesc,receiverAdd,receivermob,receivername;
     Button submit;
     ProgressDialog progressDialog;
     Model model;
    private static Retrofit.Builder builder=new Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    Gson gson = new GsonBuilder().setLenient().create();
    OkHttpClient client = new OkHttpClient();
    public static Retrofit retrofit=builder.build();
    RetrofitInterface retrofitInterface=retrofit.create(RetrofitInterface.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generatorqr);


        productdesc=(TextInputEditText)findViewById(R.id.productdesc);
        receiverAdd=(TextInputEditText)findViewById(R.id.raddress);
        receivermob=(TextInputEditText)findViewById(R.id.rmobile);
        receivername=(TextInputEditText)findViewById(R.id.rname);
        submit=(Button)findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog=new ProgressDialog(GenerateQR.this);
                progressDialog.setTitle("Submitting QR");
                progressDialog.setMessage("Please wait while we submit your details");
                progressDialog.setCancelable(false);
                progressDialog.show();
                startSubmit(productdesc.getEditableText().toString(),
                        receiverAdd.getEditableText().toString(),
                        receivermob.getEditableText().toString(),
                        receivername.getEditableText().toString());

            }
        });

    }

    private void startSubmit(final String productdescription, final String raddress, final String rmobile, final String rname) {


        if(productdescription.isEmpty()){
            progressDialog.dismiss();
            productdesc.setError("Product Description is required");
            productdesc.requestFocus();
            return;
        }
        if(raddress.isEmpty())
        {
            progressDialog.dismiss();
            receiverAdd.setError("Receiver's Address is required");
            receiverAdd.requestFocus();
            return;
        }
        if(rmobile.isEmpty())
        {
            progressDialog.dismiss();
            receivermob.setError("Receiver's Mobile is required");
            receivermob.requestFocus();
            return;
        }
        if(rname.isEmpty())
        {
            progressDialog.dismiss();
            receivername.setError("Receiver's Name is required");
            receivername.requestFocus();
            return;
        }
        if(rmobile.length()<10)
        {
            progressDialog.dismiss();
            receivermob.setError("Enter valid mobile number");
            receivermob.requestFocus();
            return;
        }

        Call<Model> call=retrofitInterface.generateqr(rname,raddress,rmobile,productdescription);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, retrofit2.Response<Model> response) {

                progressDialog.dismiss();
                model=response.body();

//             Log.d("check",String.valueOf(model.getSuccess()));
                if(model.getSuccess() && model.getMsg().equals("qrdata successfully stored in database"))
                {
                    progressDialog.cancel();
                    Toast.makeText(GenerateQR.this,"Data Stored Successfully",Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                    Intent i=new Intent(GenerateQR.this,QrGenerate.class);
                    i.putExtra("ProductDescription",productdescription);
                    i.putExtra("ReceiverAddress",raddress);
                    i.putExtra("ReceiverMobile",rmobile);
                    i.putExtra("ReceiverName",rname);

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
}
