package com.exampl.wechat;

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

import com.exampl.weChat.databinding.ActivityProfileViewBinding;
import com.exampl.weChat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class profile_viewActivity extends AppCompatActivity {
    String contact, userName, profilepic, status, sender_contact;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 13;

    ActivityProfileViewBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        userName = getIntent().getStringExtra("userName");
        profilepic = getIntent().getStringExtra("profilepic");
        contact = getIntent().getStringExtra("contact");
        status = getIntent().getStringExtra("status");
        sender_contact = getIntent().getStringExtra("Sender_contact");
        binding.dialogUsername.setText(userName);
        binding.dialogContact.setText(contact);
        Picasso.get().load(profilepic).placeholder(R.drawable.user).into(binding.dialogUserimg);

        binding.dialogPhncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sender_contact.equals("")) {
                    Toast.makeText(profile_viewActivity.this, "Register Yourself", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(profile_viewActivity.this, R.style.AlertDialTheme);
                    View view = LayoutInflater.from(profile_viewActivity.this).inflate(R.layout.custom_dialog_warning,
                            (ConstraintLayout) v.findViewById(R.id.layoutDialogContainer)); //id of the main Constraint layout in custom dialog box xml file

                    builder.setView(view);
                    ((TextView) view.findViewById(R.id.texttitle)).setText("Registration Required");
                    ((TextView) view.findViewById(R.id.textmessage)).setText("You need to register your contact");
                    ((Button) view.findViewById(R.id.buttonAction)).setText("OK");
                    ((ImageView) view.findViewById(R.id.ImgIcon)).setImageResource(R.drawable.ic_security);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
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


                } else if (getIntent().getStringExtra("contact").equals("")) {
                    Toast.makeText(profile_viewActivity.this, userName+ " Contact not Uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(profile_viewActivity.this, "Calling.." + contact, Toast.LENGTH_SHORT).show();
                    phoneCall();
                }


            }
        });

//        to dismiss the popup activity use setonclick listner on the root layout by giving it id and then set finish() there...
        binding.constraintbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.dialogChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile_viewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    public void phoneCall() {
        String number = contact;
        if (number.length() < 10) {
            Toast.makeText(profile_viewActivity.this, "PLEASE ENTER VALID NUMBER", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(profile_viewActivity.this, settingsActivity.class);
            startActivity(intent);
        } else {
            String s = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(s));
//            to check if the permission was granted previously or not
            if (ContextCompat.checkSelfPermission(profile_viewActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(profile_viewActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);

                // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                //You already have permission
                try {
                    Toast.makeText(this, "Calling.." + contact, Toast.LENGTH_LONG).show();
                    startActivity(intent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //    for showing current status to others
//    to show online when mainActivity Opens
    @Override
    public void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("online");


    }

    //    when the activity closes then the offline status should be displayed for that use onPausemethodd
//    use it in if condition ...that if it is not null then show offline

    //it says that when mainActivity is onPause then do this
    @Override
    public void onPause() {


            String currentId = FirebaseAuth.getInstance().getUid();
            database.getReference().child("presence").child(currentId).setValue("offline");
        super.onPause();

    }



}