package com.qrtool.qrproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qrtool.qrproject.Retrofit.RetrofitInterface;
import com.qrtool.qrproject.util.Model;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity {
     TextInputEditText name,passwrod,confirmPassword,email,mobile,setmobile;
     Button signup;
    ProgressDialog progressDialog;
    TextView signintext;
    Spinner userType;
    String type;


    private static Retrofit.Builder builder=new Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static Retrofit retrofit=builder.build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name=(TextInputEditText)findViewById(R.id.name);
        email=(TextInputEditText)findViewById(R.id.email);
        passwrod=(TextInputEditText)findViewById(R.id.password);
        confirmPassword=(TextInputEditText)findViewById(R.id.confirmpassword);
        mobile=(TextInputEditText)findViewById(R.id.mobile);
        userType =(Spinner)findViewById(R.id.spinner);
        signintext=(TextView)findViewById(R.id.signintext);
        setmobile=mobile;
        signintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SignUp.this,SignIn.class);
                startActivity(i);
                finish();
            }
        });
        signup=(Button)findViewById(R.id.edit);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog=new ProgressDialog(SignUp.this);
                progressDialog.setTitle("Creating Account");
                progressDialog.setMessage("Please wait while we creating your account");
                progressDialog.setCancelable(false);
                progressDialog.show();
                type=userType.getSelectedItem().toString();
                startRegister(name.getEditableText().toString(),
                        email.getEditableText().toString(),
                        passwrod.getEditableText().toString(),
                        confirmPassword.getEditableText().toString(),
                        mobile.getEditableText().toString(),type);

            }
        });


    }

    private void startRegister(String name, String email, String password, String confirmPassword, final String mobile,String type) {



        if(name.isEmpty()){
          progressDialog.dismiss();
            this.name.setError("Name is required");
            this.name.requestFocus();
            return;
        }
        if(email.isEmpty()){
       progressDialog.dismiss();
            this.email.setError("Email is required");
            this.email.requestFocus();
            return;
        }
        if(password.isEmpty()){
         progressDialog.dismiss();
            this.passwrod.setError("password is required");
            this.passwrod.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            this.email.setError("Please enter a valid email");
            this.email.requestFocus();
             progressDialog.cancel();
         progressDialog.dismiss();
            return;
        }
        if(confirmPassword.isEmpty()){
         progressDialog.dismiss();
            this. confirmPassword.setError("confirmPassword is required");
            this.confirmPassword.requestFocus();
            return;
        }
        if(password.length()<6)
        {
         progressDialog.dismiss();
            this.passwrod.setError("password must be of 6 characters");
            this.passwrod.requestFocus();
            return;
        }
        if(!confirmPassword.equals(password))
        {

           progressDialog.dismiss();
            this.confirmPassword.setError("password does not match");
            this.confirmPassword.requestFocus();
            return;
        }
        if(mobile.length()!=10)
        {
           progressDialog.dismiss();
            this.mobile.setError("Enter valid mobile number");
            this.mobile.requestFocus();
            return;
        }


        RetrofitInterface retrofitInterface=retrofit.create(RetrofitInterface.class);

        Call<Model> call=retrofitInterface.register(mobile,password,email,name,type);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
              progressDialog.dismiss();
                Model model=response.body();
                if(model.getMsg().equals("mobile number already exist")){
                    progressDialog.dismiss();
                    setmobile.setError("mobile number already exist");
                    setmobile.requestFocus();

                }
                if(model.getMsg().equals("user created")){
                  //  progressDialog.cancel();
                    Intent i=new Intent(SignUp.this,VerifyMobile.class);
                    i.putExtra("Mobile",mobile);
                    startActivity(i);
//                    new AlertDialog.Builder(SignUp.this).setTitle("User Created Successfully")
//                            .setMessage("Please Login to Continue")
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    Intent intent=new Intent(SignUp.this,SignIn.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            }).show();
                }
                else
                {
                   // progressDialog.dismiss();
                    Toast.makeText(SignUp.this, "Some Error occured, Please try Again After Some Time! ", Toast.LENGTH_SHORT).show();
                }}

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
               progressDialog.dismiss();
                Toast.makeText(SignUp.this, "Error !!!!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
