package com.exampl.wechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.exampl.weChat.databinding.ActivityViewProfileBinding;
import com.exampl.weChat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class viewProfileActivity extends AppCompatActivity {
    ActivityViewProfileBinding binding;

    String contact, username, profilepic, status;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        binding = ActivityViewProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.backArrowsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent2 = new Intent(viewProfileActivity.this, MainActivity.class);
//                startActivity(intent2);
                finish();
            }
        });

        username = getIntent().getStringExtra("username");
        profilepic = getIntent().getStringExtra("profilepic");
        contact = getIntent().getStringExtra("contact");
        status = getIntent().getStringExtra("status");

        binding.username.setText(username);
        binding.contact.setText(contact);
        Picasso.get().load(profilepic).placeholder(R.drawable.user).into(binding.userImage);
        binding.userStatus.setText(status);
    } //    for showing current status to others

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

        if (auth.getCurrentUser() != null) {

            String currentId = FirebaseAuth.getInstance().getUid();
            database.getReference().child("presence").child(currentId).setValue("offline");
        }
        super.onPause();

    }


}