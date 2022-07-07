//package com.example.chatingapp;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Toast;
//
//import com.example.chatingapp.databinding.ActivitySignInWithPhoneBinding;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class SignInWithPhone extends AppCompatActivity {
//    private FirebaseAuth auth;
//    FirebaseDatabase database;
//    ActivitySignInWithPhoneBinding binding;
//    //    Map<String, String> map = new HashMap<String, String>();
//    List<String> contactList = new ArrayList<>();
//    List<String> usernameList = new ArrayList<>();
//    List<String> emailList = new ArrayList<>();
//    List<String> passwordList = new ArrayList<>();
//
//    String phone, email_retrieved, password_retrieved;
//    ProgressDialog progressDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivitySignInWithPhoneBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//
//        progressDialog = new ProgressDialog(this);
//
////        intent from OTPActivity
//        phone = getIntent().getStringExtra("phonenumber");
//        binding.txtcontactPhn.setText(phone);
//
//
//        binding.getdetailBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                database.getReference().child("users")
//                        .addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//
////                            Map<String,Object> map = (Map<String, Object>) snapshot1.getValue();
////                            Object username = map.get("username");
////                            String Contact = (String) map.get("contact");
//                                    String contact = snapshot1.child("contact").getValue().toString();
//                                    String username = snapshot1.child("username").getValue().toString();
//                                    String email = snapshot1.child("mail").getValue().toString();
//                                    String password = snapshot1.child("password").getValue().toString();
//                                    contactList.add(contact);
//                                    usernameList.add(username);
//                                    emailList.add(email);
//                                    passwordList.add(password);
//                                    System.out.println(username + " " + contact + " " + email + " " + password);
//                                }
//
//                                findOtherDetails();
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                            }
//                        });
//            }
//        });
//
//
//        binding.btnSignInPhn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (email_retrieved.isEmpty() && password_retrieved.isEmpty()) {
////                    binding.etEmail.setError("ENTER YOUR EMAIL,PASSWORD AND CONTACT");
//                    Toast.makeText(SignInWithPhone.this, "Please Enter Your Email or Password or Contact", Toast.LENGTH_LONG).show();
//
//                } else {
//                    progressDialog.show();
//
////                    signing in with google
//                    auth.signInWithEmailAndPassword(binding.txtEmailPhn.getText().toString(), binding.txtPasswordPhn.getText().toString())
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    progressDialog.dismiss();
//                                    if (task.isSuccessful()) {
////                                        to add
//                                        Intent intent = new Intent(SignInWithPhone.this, MainActivity.class);
//                                        startActivity(intent);
//
//                                    } else {
//                                        Toast.makeText(SignInWithPhone.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//
//
//                                }
//                            });
//                }
//            }
//        });
//
//
//    }
//
//    private void findOtherDetails() {
//        boolean flag = false;
//        int i = 0;
//        for (i = 0; i < contactList.size(); i++) {
//            if (contactList.get(i).equals(phone)) {
//                System.out.println(contactList.get(i) + " " + usernameList.get(i) + " " + emailList.get(i) + " " + passwordList.get(i));
//                flag = true;
//                break;
//            }
//        }
//
//        if (flag == true) {
//            System.out.println("credentials receieved");
//
//            //         authenticate with google
//            email_retrieved = emailList.get(i);
//            binding.txtEmailPhn.setText(email_retrieved);
//            password_retrieved = passwordList.get(i);
//            binding.txtPasswordPhn.setText(password_retrieved);
//
//
////            convert it into settext to compare  this password
//            if (password_retrieved.equals("123456@default")) {
////                you need to sign in  with google account API
//                Toast.makeText(this, "Please Sign In With Google", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(SignInWithPhone.this, SignUpActivity.class);
//                startActivity(intent);
////               binding.txtPasswordPhn.setHint("Set google password Corresponding to this mail");
//            }
//
//        } else {
//            Intent intent = new Intent(SignInWithPhone.this, SignInActivity.class);
////         show dialog box that no such contact exits
//            Toast.makeText(this, "CONTACT NOT EXISTS", Toast.LENGTH_SHORT).show();
//            startActivity(intent);
//        }
//
//
//    }
//
//}
