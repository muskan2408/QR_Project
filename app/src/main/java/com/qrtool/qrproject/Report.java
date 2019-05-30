package com.qrtool.qrproject;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qrtool.qrproject.Retrofit.RetrofitInterface;
import com.qrtool.qrproject.adapters.StatusRecyclerAdapter;
import com.qrtool.qrproject.util.Model;
import com.qrtool.qrproject.util.Status;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Report extends AppCompatActivity {

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    SessionManagement sessionManagement;
    ProgressDialog progressDialog;
    Model model;
    TextView nostatus;
    Button download;

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    Gson gson = new GsonBuilder().setLenient().create();
    OkHttpClient client = new OkHttpClient();
    public static Retrofit retrofit = builder.build();
    RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_status);
        sessionManagement=new SessionManagement(this);
        String token;
        HashMap<String, String> user = sessionManagement.getUserDetails();
        progressDialog = new ProgressDialog(Report.this);
        progressDialog.setTitle("Loading Status");
        progressDialog.setMessage("Please wait while we load status");
        progressDialog.setCancelable(false);
        progressDialog.show();
        nostatus =(TextView)findViewById(R.id.nostatus);
        nostatus.setVisibility(View.GONE);
        token = user.get(SessionManagement.KEY_TOKEN);
        download=(Button)findViewById(R.id.button);


        int id=getIntent().getIntExtra("id",0);

        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);

        Call<Model> call=retrofitInterface.scanHistory(token,id);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
             progressDialog.dismiss();

             model=response.body();
                ArrayList<Status> status=model.getStatus();

                if(status.size()==0)
                {
                    progressDialog.dismiss();
                    nostatus.setVisibility(View.VISIBLE);
                    download.setVisibility(View.GONE);

                }
//                Log.e("Status Print..........", String.valueOf(status.get(0).getName()+status.get(1).getName()+status.get(2).getDate()));
                StatusRecyclerAdapter statusRecyclerAdapter=new StatusRecyclerAdapter(Report.this,status);
              recyclerView.setAdapter(statusRecyclerAdapter);


            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {

            }
        });


//




    }
}
