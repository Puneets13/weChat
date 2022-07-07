package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exampl.weChat.R;
import com.exampl.weChat.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignInPhone extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;

    FirebaseDatabase database;
    List<String> contactList = new ArrayList<>();
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
//request focus will help the cursor to move to the next box after one otp box is filled
//binding.phoneBox.requestFocus();


//            checking contact exists or not

        database.getReference().child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        phoneNumber = binding.phoneBox.getText().toString();

                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            contactList.add(snapshot1.child("contact").getValue().toString());
                        }
                        System.out.println("the contacts are : "+ contactList);
//                                    checking if the string is beinging contained in the list then we cant set that contact
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.continueButton.setOnClickListener(v -> {

            if (contactList.contains(binding.phoneBox.getText().toString())) {
                System.out.println(phoneNumber);

                //if phone number is contained in contactList
                if (!binding.phoneBox.getText().toString().isEmpty()) {
//                    to add the loading feature we use this progress bar  and added this in frame layout
                        binding.progressbarSendingOTP.setVisibility(View.VISIBLE);
//                    to make thhe button invisible on clicking we use thiss
                        binding.continueButton.setVisibility(View.INVISIBLE);

//                    to verify with firebase auth
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + binding.phoneBox.getText().toString(),
                                60,
                                TimeUnit.SECONDS,
                                SignInPhone.this,
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
                                        Toast.makeText(SignInPhone.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String backendOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                  To move the control to new screen
                                        binding.progressbarSendingOTP.setVisibility(View.GONE);
                                        binding.continueButton.setVisibility(View.VISIBLE);
                                        Intent intent = new Intent(SignInPhone.this, Otp_activity.class);
                                        intent.putExtra("phoneNumber", binding.phoneBox.getText().toString());
//                              we will move the backendOTp that is being passed to new screen also
                                        intent.putExtra("backendOTP", backendOTP);
                                        startActivity(intent);
                                    }
                                }
                        );
                } else {
                    binding.phoneBox.setError("Field cannot be empty");
                    Toast.makeText(this, "Enter Your phone Number", Toast.LENGTH_SHORT).show();

                }

            }else{

                System.out.println(phoneNumber);
                //inflated custom dialog here custom_dialog_warning
//                                        also in style.xml file added the theme for alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SignInPhone.this, R.style.AlertDialTheme);
                View view = LayoutInflater.from(SignInPhone.this).inflate(R.layout.custom_dialog_warning,
                        (ConstraintLayout) findViewById(R.id.layoutDialogContainer)); //id of the main Constraint layout in custom dialog box xml file

                builder.setView(view);
                ((TextView) view.findViewById(R.id.texttitle)).setText("USER NOT REGISTERED");
                ((TextView) view.findViewById(R.id.textmessage)).setText("Contact not registered\nPlease Register with EMAIL First");
                ((Button) view.findViewById(R.id.buttonAction)).setText("Ok");
                ((ImageView) view.findViewById(R.id.ImgIcon)).setImageResource(R.drawable.ic_security);
                final AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        binding.phoneBox.setText("");
                        Intent intent = new Intent(SignInPhone.this,SignUpActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();



            }



        });
    }
}