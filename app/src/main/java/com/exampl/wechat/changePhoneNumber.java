package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.exampl.weChat.databinding.ActivityChangePhoneNumberBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class changePhoneNumber extends AppCompatActivity {
    ActivityChangePhoneNumberBinding binding;
    String oldPhn, newPhn;
FirebaseAuth auth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        newPhn = binding.etPhnNew.getText().toString();

    binding.submitbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!binding.etPhnNew.getText().toString().isEmpty()) {
                if (binding.etPhnNew.getText().toString().trim().length() == 10) {
//                    to add the loading feature we use this progress bar  and added this in frame layout
                    binding.progressbarSendingOTP.setVisibility(View.VISIBLE);
//                    to make thhe button invisible on clicking we use thiss
                    binding.submitbtn.setVisibility(View.INVISIBLE);

//                    to verify with firebase auth
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + binding.etPhnNew.getText().toString(),
                            60,
                            TimeUnit.SECONDS,
                            changePhoneNumber.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                            on completing the verification the button should be visible and progress bar invisible
                                    binding.progressbarSendingOTP.setVisibility(View.GONE);
//                            to make the button visible on clicking we use this
                                    binding.submitbtn.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
//                                    on completing the verification the button should be visivble and progress bar invisible
                                    binding.progressbarSendingOTP.setVisibility(View.GONE);
//                            to make the button visible on clicking we use this
                                    binding.submitbtn.setVisibility(View.VISIBLE);
//                                    to locate the error in the Toast use e.getmessage
                                    Toast.makeText(changePhoneNumber.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String backendOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                  To move the control to new screen
                                    binding.progressbarSendingOTP.setVisibility(View.GONE);
                                    binding.submitbtn.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(changePhoneNumber.this, changePhnOTP.class);
                                    intent.putExtra("phoneNumber", binding.etPhnNew.getText().toString());
//                              we will move the backendOTp that is being passed to new screen also
                                    intent.putExtra("backendOTP", backendOTP);
                                    startActivity(intent);
                                }
                            }
                    );


                } else {
                    Toast.makeText(changePhoneNumber.this, "Please Enter Correct Number", Toast.LENGTH_SHORT).show();
                }
            } else {
                binding.etPhnNew.setError("Field cannot be empty");
                Toast.makeText(changePhoneNumber.this, "Enter Your phone Number", Toast.LENGTH_SHORT).show();

            }

        }
    });



    }
}