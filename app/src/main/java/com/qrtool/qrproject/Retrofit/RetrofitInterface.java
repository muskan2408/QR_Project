package com.qrtool.qrproject.Retrofit;

import com.qrtool.qrproject.util.Model;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitInterface {


    @FormUrlEncoded
    @POST("/register")
    Call<Model> register(@Field(value = "contact") String contact,
                         @Field(value = "password" ) String password,
                         @Field(value = "email") String email,
                         @Field(value = "name") String name);



    @FormUrlEncoded
    @POST("/login")
    Call<Model> login(@Field(value = "contact") String contact,@Field(value = "password") String pin);

    @FormUrlEncoded
    @POST("/qrgenerator")
    Call<Model> generateqr(@Field(value = "rname") String name,@Field(value = "raddress") String address,@Field(value = "rmobile") String mobile,@Field(value = "proDescription") String productdesc);
    @GET("/")
    Call<Model>session_manage(@Header("authorization")String authtoken);
    @GET("/profile")
    Call<Model>show_profile(@Header("authorization")String authtoken);
}
