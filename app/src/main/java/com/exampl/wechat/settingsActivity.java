package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.exampl.weChat.databinding.ActivitySettingsBinding;
import com.exampl.wechat.Models.Users;
import com.exampl.weChat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class settingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String changedContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        to update the contact

//        intent from changePhoneOTP
        changedContact=getIntent().getStringExtra("newPhnNumber");
        binding.etContact.setText(changedContact);


        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        try {
                            if (users.getContact().equals("")) {
                                binding.etContact.setText("Contact Not Registered");
                            } else {
                                binding.etContact.setText(users.getContact());
                            }
                        }catch (NullPointerException e){

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



//for backArrow
        binding.backArrowsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(settingsActivity.this, MainActivity.class);
                startActivity(intent2);
            }
        });

//        to save the username and About on pressing SAVE button
        binding.saveBtnSettings.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                String status = binding.etStatus.getText().toString();
                String userName = binding.etUser.getText().toString();
//                contact = binding.etContact.getText().toString();
//                final String contact_copied = contact;
//              if the contact uploaded by the user is empty then
//                if the user erase the contact himself then the empty space should be replaced by null to further update in future
                if (userName.isEmpty() && status.isEmpty()) {
                    Toast.makeText(settingsActivity.this, "Please enter the Details Correctly", Toast.LENGTH_SHORT).show();

                }
                     else {
                                        // to show the dialog box that your account is being creating

                                        HashMap<String, Object> obj = new HashMap<>();
                                        obj.put("username", userName);
                                        obj.put("status", status);
//first value should be same as the node key in the firebase otherwise a new node would be created
//     this line of code is used to update the data in Firebase
                                        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                                                .updateChildren(obj);
                                        Toast.makeText(settingsActivity.this, "PROFILE UPDATED", Toast.LENGTH_SHORT).show();
                                    }
            }
        });

//        to set the image as a profile pic in settings we use this
//        when we upload the image from settings then it got update ti the recycler view and will be removed from settings icon
//        to get that again  we ue this code
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        Picasso.get().load(users.getProfilepicture())
                                .placeholder(R.drawable.user)
                                .into(binding.userImage);

                        //after SAVING the data will get disappear from their
// to keep that data in settings also we will use this
                        binding.etStatus.setText(users.getStatus());
                        binding.etUser.setText(users.getUsername());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//        for adding profile image we need to use firebase auth ..firebase database and
//        set Onclick listener on plus button
        binding.plusSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                to move from settings activity screen to Gallery we will use Intent
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
//to get all type of activity from phon use --->   */*
//                this will give gallery..mp3 .. media everything

                startActivityForResult(intent, 33);
            }
        });

    binding.updatContact.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           Intent intent= new Intent(settingsActivity.this , changePhoneNumber.class);
           startActivity(intent);
        }
    });
    }




    //now we will use onActivityResult further to get the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        getData() methods contain the file path that has been selected by the user
        if (data.getData() != null) {
            Uri sFile = data.getData();
            binding.userImage.setImageURI(sFile);
//             give a unique id to the profile image so use the userId only as the profile image
            final StorageReference reference = storage.getReference().child("profilepicture")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//toget the link from the storage
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profilepicture").setValue(uri.toString());
                            Toast.makeText(settingsActivity.this, "PROFILE UPLOADED", Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            });


        }


    }
}