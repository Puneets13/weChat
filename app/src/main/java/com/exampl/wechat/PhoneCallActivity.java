package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exampl.weChat.databinding.ActivityPhoneCallBinding;
import com.exampl.weChat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.ncorti.slidetoact.SlideToActView;
import com.squareup.picasso.Picasso;

public class PhoneCallActivity extends AppCompatActivity {
    ActivityPhoneCallBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String sender_contact, userName, contact, profilepic;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 13;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

//to set the user name and image there
//        phn_Number = getIntent().getStringExtra("contact");
//        String profilepic = getIntent().getStringExtra("profilepic");

//        intent coming from callAdapter
        userName = getIntent().getStringExtra("userName");
        profilepic = getIntent().getStringExtra("profilepic");
        contact = getIntent().getStringExtra("contact");
        sender_contact = getIntent().getStringExtra("Sender_contact");


        binding.username.setText(userName);
        binding.PhnNumber.setText(contact);
        Picasso.get().load(profilepic).placeholder(R.drawable.user).into(binding.userCallImg);

        binding.backArrowPhn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for coming back to the activity and to finsih the activity use finish() instead of Intent
//                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });


//        using slideonClick Listener
        binding.makecall.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {
                if (sender_contact.equals("")) {
//                    check_uniqueness();
//                    Toast.makeText(PhoneCallActivity.this, "Register Yourself", Toast.LENGTH_SHORT).show();
//                    dialog box

                    AlertDialog.Builder builder = new AlertDialog.Builder(PhoneCallActivity.this, R.style.AlertDialTheme);
                    View view = LayoutInflater.from(PhoneCallActivity.this).inflate(R.layout.custom_dialog_warning,
                            (ConstraintLayout) findViewById(R.id.layoutDialogContainer)); //id of the main Constraint layout in custom dialog box xml file

                    builder.setView(view);
                    ((TextView) view.findViewById(R.id.texttitle)).setText("REGISTER CONTACT");
                    ((TextView) view.findViewById(R.id.textmessage)).setText("Do You want to Register");
                    ((Button) view.findViewById(R.id.buttonAction)).setText("REGISTER");
                    ((ImageView) view.findViewById(R.id.ImgIcon)).setImageResource(R.drawable.ic_security);
                    final AlertDialog alertDialog = builder.create();

                    alertDialog.setCancelable(true);

//                alertDialog.dismiss();
                    view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(PhoneCallActivity.this, "REGISTERED", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
//                            binding.makecall.resetSlider();
                            Intent intent = new Intent(PhoneCallActivity.this, PhoneNumberActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                    if (alertDialog.getWindow() != null) {
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                    alertDialog.show();

                } else if (getIntent().getStringExtra("contact").equals("")) {

//                    Toast.makeText(PhoneCallActivity.this, "Reciever Contact not Uploaded", Toast.LENGTH_SHORT).show();
//                    dialog box
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhoneCallActivity.this, R.style.AlertDialTheme);
                    View view = LayoutInflater.from(PhoneCallActivity.this).inflate(R.layout.custom_dialog_warning,
                            (ConstraintLayout) findViewById(R.id.layoutDialogContainer)); //id of the main Constraint layout in custom dialog box xml file

                    builder.setView(view);
                    ((TextView) view.findViewById(R.id.texttitle)).setText("CAN'T MAKE CALL ");
                    ((TextView) view.findViewById(R.id.textmessage)).setText(userName+" haven't Registered");
                    ((Button) view.findViewById(R.id.buttonAction)).setText("OK");
                    ((ImageView) view.findViewById(R.id.ImgIcon)).setImageResource(R.drawable.ic_security);
                    final AlertDialog alertDialog = builder.create();

                    alertDialog.setCancelable(true);

                    view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    if (alertDialog.getWindow() != null) {
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                    alertDialog.show();



                } else {
//                    Toast.makeText(PhoneCallActivity.this, "Calling.." + contact, Toast.LENGTH_SHORT).show();
//                    check_uniqueness();
                    phoneCall();

                }

                binding.makecall.resetSlider();

            }
        });

//        binding.makecall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (sender_contact == null) {
////                    check_uniqueness();
//                    Toast.makeText(PhoneCallActivity.this, "Register Yourself", Toast.LENGTH_SHORT).show();
////once the data  got update in the database ..then we have to use onDataChange method so that tge data got updated everywhere in the Strings Also
//                    database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    Users users = snapshot.getValue(Users.class);
//                                    try {
//                                        users.setContact(sender_contact);
//
//                                    } catch (NullPointerException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                } else if (getIntent().getStringExtra("contact") == null) {
//                    Toast.makeText(PhoneCallActivity.this, "Reciever Contact not Uploaded", Toast.LENGTH_SHORT).show();
//                } else {
////                    Toast.makeText(PhoneCallActivity.this, "Calling.." + contact, Toast.LENGTH_SHORT).show();
////                    check_uniqueness();
//                    phoneCall();
//                }
//
//            }
//        });
//
    }

//
////to retrive the data from the database we use this snapshot method and i have printed this statement in Run
//    public void check_uniqueness(){
//        database.getReference().child("users")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                            String contactList = snapshot1.child("contact").getValue().toString();
//                            System.out.println(contactList);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }


    public void phoneCall() {
        String number = binding.PhnNumber.getText().toString();
        if (getIntent().getStringExtra("contact") == null) {
            Toast.makeText(PhoneCallActivity.this, "PLEASE ENTER VALID NUMBER", Toast.LENGTH_SHORT).show();
            //to reset the slider after making call
            binding.makecall.resetSlider();
        } else {
            String s = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(s));
//            to check if the permission was granted previously or not
            if (ContextCompat.checkSelfPermission(PhoneCallActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(PhoneCallActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);

//                to reset the slider after making call
                binding.makecall.resetSlider();

                // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                //You already have permission
                try {
                    Toast.makeText(this, "Calling.." + number, Toast.LENGTH_LONG).show();
                    binding.makecall.resetSlider();
                    startActivity(intent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                //                to reset the slider after making call
            }

        }
    }

    //    for showing current status to others
//    to show online when mainActivity Opens
    @Override
    protected void onResume() {
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("online");

        super.onResume();

    }

    //when person presss home button from chatdetail then it should show offline there also
//    for that run it in chatDetail also ..that it show go to offline also
    @Override
    protected void onPause() {

        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("offline");
        super.onPause();

    }


}


