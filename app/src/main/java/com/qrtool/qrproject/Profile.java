package com.qrtool.qrproject;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qrtool.qrproject.Retrofit.RetrofitInterface;
import com.qrtool.qrproject.util.Model;
import com.qrtool.qrproject.util.User;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Profile extends AppCompatActivity {
       FloatingActionButton edit;
       SessionManagement session;
       TextView email,mobile,name;
    User u;
       Model model;
    Gson gson = new GsonBuilder().setLenient().create();
    OkHttpClient client = new OkHttpClient();
    Retrofit.Builder builder=new Retrofit.Builder().baseUrl(Constants.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create(gson));
    Retrofit retrofit=builder.build();
    RetrofitInterface retrofitInterface=retrofit.create(RetrofitInterface.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        email=(TextView)findViewById(R.id.emailid);
        mobile=(TextView)findViewById(R.id.mobile);
        name=(TextView)findViewById(R.id.name);
        edit=(FloatingActionButton)findViewById(R.id.floatingActionButton);
        session=new SessionManagement(this);
         String token=getIntent().getStringExtra("token");
        Call<Model> call=retrofitInterface.show_profile(token);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                model=response.body();
                if(model.getSuccess())
                {
                    u=model.getUser();
                    name.setText(u.getName().toUpperCase());
                    email.setText(u.getEmail());
                    mobile.setText("Mobile Number : "+u.getContact());

                }
                Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                Log.e("TAG", "response 33: " + t.getMessage());

            }


        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Profile.this,EditProfile.class);
                i.putExtra("name",u.getName());
                i.putExtra("email",u.getEmail());
                startActivity(i);

            }
        });
    }
}
