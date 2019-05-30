package com.qrtool.qrproject.Retrofit;

import android.graphics.Bitmap;

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
                         @Field(value = "name") String name,
                         @Field(value = "usertype") String usertype);



    @FormUrlEncoded
    @POST("/login")
    Call<Model> login(@Field(value = "contact") String contact,@Field(value = "password") String pin);

    @FormUrlEncoded
    @POST("/qrgenerator")
    Call<Model> generateqr(@Field(value = "rname") String name, @Field(value = "raddress") String address, @Field(value = "rmobile") String mobile, @Field(value = "proDescription") String productdesc, @Field(value="imageBitmap") Bitmap imageBitmap, @Field(value="longitude") double longitude, @Field(value
            ="latitude") double latitude, @Field(value = "date") String date, @Field(value = "time") String time,@Header("authorization") String authotoken
    ,@Field(value = "packagename") String packageName);
    @FormUrlEncoded
    @POST("/scanqr")
    Call<Model> scanqr(@Field(value="imageBitmap") Bitmap imageBitmap,@Field(value="longitude") double longitude,@Field(value ="latitude") double latitude ,@Field(value = "date") String date,@Field(value = "time") String time,@Header("authorization") String authotoken);
    @GET("/")
    Call<Model>session_manage(@Header("authorization")String authtoken);
    @GET("/profile")
    Call<Model>show_profile(@Header("authorization")String authtoken);

    @GET("/report")
    Call<Model>show_report(@Header("authorization") String authotoken);
    @FormUrlEncoded
    @POST("/scanhistory")
    Call<Model>scanHistory(@Header("authorization") String authotoken,@Field(value="id") int id);

    @FormUrlEncoded
    @POST("/scanresult")
    Call<Model> scanresult(@Field(value = "data")String data,@Header("authorization") String authotoken);

    @GET("/qrhistory")
    Call<Model> qrhistory(@Header("authorization") String authotoken);

}
