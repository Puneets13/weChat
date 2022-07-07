package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.exampl.weChat.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//request focus will help the cursor to move to the next box after one otp box is filled
//        binding.phoneBox.requestFocus();

        binding.continueButton.setOnClickListener(v -> {
            if (!binding.phoneBox.getText().toString().isEmpty()) {
                if (binding.phoneBox.getText().toString().trim().length() == 10) {
//                    to add the loading feature we use this progress bar  and added this in frame layout
                    binding.progressbarSendingOTP.setVisibility(View.VISIBLE);
//                    to make thhe button invisible on clicking we use thiss
                    binding.continueButton.setVisibility(View.INVISIBLE);

//                    to verify with firebase auth
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + binding.phoneBox.getText().toString(),
                            60,
                            TimeUnit.SECONDS,
                            PhoneNumberActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                            on completing the verification the button should be visible and progress bar invisible
                                    binding.progressbarSendingOTP.setVisibility(View.GONE);
//                            to make the button visible on clicking we use this
                                    binding.continueButton.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
//                                    on completing the verification the button should be visivble and progress bar invisible
                                    binding.progressbarSendingOTP.setVisibility(View.GONE);
//                            to make the button visible on clicking we use this
                                    binding.continueButton.setVisibility(View.VISIBLE);
//                                    to locate the error in the Toast use e.getmessage
                                    Toast.makeText(PhoneNumberActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String backendOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                  To move the control to new screen
                                    binding.progressbarSendingOTP.setVisibility(View.GONE);
                                    binding.continueButton.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
                                    intent.putExtra("phoneNumber", binding.phoneBox.getText().toString());
//                              we will move the backendOTp that is being passed to new screen also
                                    intent.putExtra("backendOTP", backendOTP);
                                    startActivity(intent);
                                }
                            }
                    );


                } else {
                    Toast.makeText(this, "Please Enter Correct Number", Toast.LENGTH_SHORT).show();
                }
            } else {
                binding.phoneBox.setError("Field cannot be empty");
                Toast.makeText(this, "Enter Your phone Number", Toast.LENGTH_SHORT).show();

            }
        });
    }
}