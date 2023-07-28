package com.example.sellbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sellbooks.databinding.ActivityRegisterPhoneBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterPhoneActivity extends AppCompatActivity {

    ActivityRegisterPhoneBinding binding;
    FirebaseAuth auth;
    PhoneAuthCredential phoneAuthCredential;
    PhoneAuthProvider.ForceResendingToken token;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String verificationID;

    Boolean otpValid ;
    String phone;
    String sentPhone;

    Boolean verify = false;
    Boolean send = true;

    public static final String SHARED_PREFS = "sharedPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        otpValid = false;

        Intent data = getIntent();
        sentPhone = data.getStringExtra("phone");

        binding.editTextText.setText(sentPhone);

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationID = s;
                token = forceResendingToken;
                binding.Resend.setVisibility(View.GONE);

                binding.verify.setBackgroundResource(R.drawable.disabled_btn);
                binding.send.setBackgroundResource(R.drawable.round_btn);

                send = false;
                verify = true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                binding.Resend.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                verifyAuthentication(phoneAuthCredential);
                binding.Resend.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(RegisterPhoneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };


        if(verify == true) {

            binding.verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sendOtp();
                    Toast.makeText(RegisterPhoneActivity.this, "OTP sent!", Toast.LENGTH_SHORT).show();
                    otpValid = true;
                }
            });
        }
        else {

            Toast.makeText(RegisterPhoneActivity.this, "Click resend in some time.", Toast.LENGTH_SHORT).show();
        }


        binding.Resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOtp();
                Toast.makeText(RegisterPhoneActivity.this, "OTP sent!", Toast.LENGTH_SHORT).show();
            }
        });



        if(send == true) {
            binding.send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (binding.editTextText.getText() != null) {

                        String otp = binding.editTextTextPassword.getText().toString();

                        phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID, otp);

                        verifyAuthentication(phoneAuthCredential);
                    }
                }
            });
        }
        else{

            Toast.makeText(RegisterPhoneActivity.this, "First, send the OTP.", Toast.LENGTH_SHORT).show();

        }

    }

    private void verifyAuthentication(PhoneAuthCredential credential) {

        auth.getCurrentUser().linkWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("id", "true");
                        editor.apply();

                        Toast.makeText(RegisterPhoneActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterPhoneActivity.this, HomeActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(RegisterPhoneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void sendOtp(){

        phone = binding.editTextText.getText().toString();
        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phone).setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(callbacks).build());
    }

    private void resendOtp(){

        phone = binding.editTextText.getText().toString();
        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phone).setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(callbacks).setForceResendingToken(token).build());
    }


}