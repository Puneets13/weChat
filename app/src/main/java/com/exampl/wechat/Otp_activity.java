package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.exampl.weChat.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


//KIRAN ACTIVITY
public class Otp_activity extends AppCompatActivity {
    ActivityOtpBinding binding;
    FirebaseAuth auth;// for authenticationS
    String backendotp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth= FirebaseAuth.getInstance();
        String phoneNum= getIntent().getStringExtra("phoneNumber");
        binding.phoneLabel.setText(phoneNum);
        backendotp=getIntent().getStringExtra("backendOTP");



//        PhoneAuthProvider.verifyPhoneNumber(options);//options ko follow karde code sent on phonr number

        binding.SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.otpView1.getText().toString().trim().isEmpty()&&!binding.otpView2.getText().toString().trim().isEmpty()&&!binding.otpView3.getText().toString().trim().isEmpty()&&!binding.otpView4.getText().toString().trim().isEmpty()&&!binding.otpView5.getText().toString().trim().isEmpty()&&!binding.otpView6.getText().toString().trim().isEmpty()){
//                    Toast.makeText(Otp_activity.this, "otp verify!", Toast.LENGTH_SHORT).show();
                    String entercodeotp=binding.otpView1.getText().toString()+
                            binding.otpView2.getText().toString()+
                            binding.otpView3.getText().toString()+
                            binding.otpView4.getText().toString()+
                            binding.otpView5.getText().toString()+
                            binding.otpView6.getText().toString();
                    if(backendotp!=null){
                        binding.progressbarSendingOTP.setVisibility(View.VISIBLE);
                        binding.SubmitButton.setVisibility(View.INVISIBLE);
                        PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(backendotp,entercodeotp);
//                        using sigininwith PHONE method
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        binding.progressbarSendingOTP.setVisibility(View.GONE);
                                        binding.SubmitButton.setVisibility(View.VISIBLE);

                                        if(task.isSuccessful()){
                                            Toast.makeText(Otp_activity.this, "SIGN IN WITH PHONE", Toast.LENGTH_SHORT).show();
                                            Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        }
                                        else{
                                            Toast.makeText(Otp_activity.this, "Make correct otp", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else{
                        Toast.makeText(Otp_activity.this, "check internet", Toast.LENGTH_SHORT).show();
                    }


                }else{

                    Toast.makeText(Otp_activity.this, "Please enter otp", Toast.LENGTH_SHORT).show();
                }
            }
        });
        numberotpMove();

        binding.resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + phoneNum,
                        60,
                        TimeUnit.SECONDS,
                        Otp_activity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(Otp_activity.this, "Error! Check Internet Connection", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String new_botp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                backendotp=new_botp;
                                Toast.makeText(Otp_activity.this, "otp send successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
    }
    private void numberotpMove(){
        binding.otpView1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.otpView2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.otpView2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.otpView3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.otpView3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.otpView4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.otpView4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.otpView5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.otpView5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.otpView6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}