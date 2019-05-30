package com.qrtool.qrproject;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qrtool.qrproject.Retrofit.RetrofitInterface;
import com.qrtool.qrproject.adapters.QrHistoryAdapter;
import com.qrtool.qrproject.util.History;
import com.qrtool.qrproject.util.Model;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QrHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    SessionManagement sessionManagement;
    ProgressDialog progressDialog;
    Model model;

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    Gson gson = new GsonBuilder().setLenient().create();
    OkHttpClient client = new OkHttpClient();
    public static Retrofit retrofit = builder.build();
    RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_history);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_history);
        sessionManagement=new SessionManagement(this);
        String token;
        HashMap<String, String> user = sessionManagement.getUserDetails();
        progressDialog = new ProgressDialog(QrHistory.this);
        progressDialog.setTitle("Loading History");
        progressDialog.setMessage("Please wait while we load your generation history");
        progressDialog.setCancelable(false);
        progressDialog.show();
        token = user.get(SessionManagement.KEY_TOKEN);


        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);

        Call<Model> call=retrofitInterface.qrhistory(token);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                progressDialog.dismiss();

                model=response.body();

                ArrayList<History> histories=model.getData();

                if(histories.size()==0)
                {
                    progressDialog.dismiss();
                }

            //    Log.e("Rname.................",histories.get(0).getRname().toString());
                QrHistoryAdapter adapter=new QrHistoryAdapter(QrHistory.this,histories);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {

            }
        });


       //


    }
}
