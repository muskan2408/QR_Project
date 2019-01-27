package com.qrtool.qrproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qrtool.qrproject.util.Model;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyMobile extends AppCompatActivity {
    private TextInputLayout codeET;
    private ProgressBar codeProgress;
    private ConstraintLayout codeLayout;

    private Button sendCodeBT;
    private TextView errorText;
    Model model;
    String phoneNumber;
    private FirebaseAuth mAuth;

    private int btnType = 0;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    protected PhoneAuthProvider.ForceResendingToken mResendToken;
    Gson gson = new GsonBuilder().setLenient().create();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        codeLayout  = findViewById(R.id.codeLayout);
        codeET = findViewById(R.id.codeET);
        codeProgress = findViewById(R.id.codeProgress);
        sendCodeBT = findViewById(R.id.sendCodeBT);
        errorText = findViewById(R.id.errorText);
        FirebaseApp.initializeApp(this);

       Intent i=getIntent();
       phoneNumber= "+91"+i.getStringExtra("Mobile");


        mAuth = FirebaseAuth.getInstance();


        sendCodeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  sendCodeBT.setError(null);

            if (btnType==0) {
//
                     if(phoneNumber.length()==13){
//
//                        phoneProgress.setVisibility(View.VIS8IBLE);
//                        phoneET.setEnabled(false);
//                        //sendCodeBT.setVisibility(View.GONE);
//                        Call<Model> call=retrofitInterface.checkMobile(phoneNumber);
//
//                        call.enqueue(new Callback<Model>() {
//                            @Override
//                            public void onResponse(Call<Model> call, Response<Model> response) {
//                                model=response.body();
//                                if(model.getMsg().equals("mobile number already exist")){
//                                    phoneET.setError("Mobile number already exist, enter different mobile number");
//                                    phoneET.setEnabled(true);
//
//                                }
//                                else{
//                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                                            phoneNumber,
//                                            30,
//                                            TimeUnit.SECONDS,
//                                            MobileAuthActivity.this,
//                                            mCallbacks);
//                                }
//
//                            }
//
                         PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                 phoneNumber,
                                 30,
                                 TimeUnit.SECONDS,
                                 VerifyMobile.this,
                                 mCallbacks);
//                            @Override
//                            public void onFailure(Call<Model> call, Throwable t) {
//                                phoneET.setError("Mobile number already exist...");
//                            }
//                        });
//
                    }
//                    else {
////                        phoneET.setError("Please enter a valid number...");
//                    }
//
//                }else {


                    String code = codeET.getEditText().getText().toString();

                    if(code.length()!=6){

                        codeET.setError("Please enter a valid code");

                    }else {

                        sendCodeBT.setEnabled(false);
                        codeProgress.setVisibility(View.VISIBLE);

                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId,code);

                        signInWithPhoneAuthCredential(phoneAuthCredential);

                    }

                }}
           // }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    errorText.setText(e.getMessage());
                    Log.d("error",e.getLocalizedMessage());
                } else if (e instanceof FirebaseAuthInvalidUserException) {
                    errorText.setText("Invalid mobile number");
                    Log.d("error",e.getLocalizedMessage());
                } else {
                    errorText.setText(e.getMessage());
                    Log.d("error",e.getLocalizedMessage());
                }

                errorText.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                mVerificationId = verificationId;
                mResendToken = forceResendingToken;

               // phoneProgress.setVisibility(View.INVISIBLE);

                codeLayout.setVisibility(View.VISIBLE);

                btnType = 1;
                sendCodeBT.setEnabled(true);
                sendCodeBT.setText("Verify Code");
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();
                            String contactNo = user.getPhoneNumber();

//       Toast.makeText(getApplicationContext(),"valid",Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MobileAuthActivity.this,"Code sent", Toast.LENGTH_SHORT).show();
//                            Intent intent=new Intent(VerifyMobile.this,Register.class);
//                            intent.putExtra("mobileno",contactNo);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();

                            new AlertDialog.Builder(VerifyMobile.this).setTitle("User Created Successfully")
                            .setMessage("Please Login to Continue")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent=new Intent(VerifyMobile.this,SignIn.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();


                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                codeProgress.setVisibility(View.INVISIBLE);

                                errorText.setText("The sms verification code is invalid. Please check the code");
                                Log.d("VerifyMobile",task.getException().getLocalizedMessage());

                                errorText.setVisibility(View.VISIBLE);
                                sendCodeBT.setEnabled(true);


                            }
                        }
                    }
                });
    }
}
