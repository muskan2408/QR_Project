package com.qrtool.qrproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
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

public class SignIn extends AppCompatActivity {
Button signin;
TextInputEditText mobile,password;
    Model model;
    TextView signup;
    PrefManager prefManager;
    ProgressDialog progressDialog;
    Spinner userType;
    String  token;
    SessionManagement session;
    private static final String TAG="Login";
    private static Retrofit.Builder builder=new Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    Gson gson = new GsonBuilder().setLenient().create();
    OkHttpClient client = new OkHttpClient();
    public static Retrofit retrofit=builder.build();
    RetrofitInterface retrofitInterface=retrofit.create(RetrofitInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        
        mobile=(TextInputEditText)findViewById(R.id.mobile); 
        password=(TextInputEditText)findViewById(R.id.password);
        signup=(TextView)findViewById(R.id.signuptext);
        session=new SessionManagement(getApplicationContext());

        prefManager=new PrefManager(getApplicationContext());
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SignIn.this,SignUp.class);
                startActivity(i);
            }
        });
        signin=(Button)findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog=new ProgressDialog(SignIn.this);
                progressDialog.setTitle("Log In");
                progressDialog.setMessage("Please wait while we check your credentials");
                progressDialog.setCancelable(false);
                progressDialog.show();
                String mobilenumber=mobile.getEditableText().toString();
                String Spassword=password.getEditableText().toString();
                startLogin(mobilenumber,Spassword);
//                Intent i=new Intent(Login.this,MainActivity.class);
//                startActivity(i);
            }
        });

       
    }

    private void startLogin(final String mobilenumber, String spassword) {
        if(spassword.isEmpty()){
            progressDialog.dismiss();
            password.setError("Mobile is required");
            password.requestFocus();
            return;
        }
        if(spassword.length()<6)
        {
            progressDialog.dismiss();
            password.setError("pin must be of 6 characters");
            password.requestFocus();
            return;
        }
        if(mobilenumber.length()<10)
        {
            progressDialog.dismiss();
            mobile.setError("Enter valid mobile number");
            mobile.requestFocus();
            return;
        }
        Call<Model> call=retrofitInterface.login(mobilenumber,spassword);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, retrofit2.Response<Model> response) {

                progressDialog.dismiss();
                model=response.body();

//             Log.d("check",String.valueOf(model.getSuccess()));
                if(model.getMsg()!=null&& model.getMsg().equals("wrong password"))
                {
                    progressDialog.cancel();
                    Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                    password.setError("Incorrect Pin");
                    password.requestFocus();
                    return ;
                }

                if(model.getMsg()!=null&&model.getMsg().equals("user with contact number does not exist"))
                {
                    progressDialog.cancel();
                    mobile.setError("Invalid Username");
                    mobile.requestFocus();
                    return;
                }
                boolean check=false;
                check= model.getSuccess();
                Log.d("check",String.valueOf(check));



                if(check){
                    progressDialog.cancel();
                    Toast.makeText(SignIn.this, "Signin Successful", Toast.LENGTH_SHORT).show();
                    String userType;
                    userType=model.getUserType();
                    session.createLoginSession(mobile.toString(), password.toString(),model.getToken(),userType);

                  token=model.getToken();


                    Log.e(TAG,token);
                    Intent i=new Intent(SignIn.this,MainActivity.class);
                    // i.putExtra("allvalues", model);
                    i.putExtra("token",token);
                    i.putExtra("userType",userType);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(SignIn.this, "Some Error occured, Please try Again After Some Time! ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {

                Toast.makeText(SignIn.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("TAG", "error: " + t);
                progressDialog.cancel();
            }
        });

    }
}
