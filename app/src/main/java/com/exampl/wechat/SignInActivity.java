package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

//import com.exampl.weChat.databinding.ActivitySignInBinding;
import com.exampl.weChat.databinding.ActivitySignInBinding;
import com.exampl.wechat.Models.Users;
import com.exampl.weChat.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;
    public ProgressDialog loadingBar;
    //get getting contacts
    List<String> contactList_google = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        to remove the actionBar on the top of app
        getSupportActionBar().hide();

        loadingBar = new ProgressDialog(SignInActivity.this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("LogIn");
        progressDialog.setMessage("LogIn to your account");

        GoogleSignInOptions gso = (new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
//                String contact = binding.txtPhone.getText().toString();

                if (mail.isEmpty() && password.isEmpty()) {
                    binding.etEmail.setError("ENTER YOUR EMAIL,PASSWORD");
                    Toast.makeText(SignInActivity.this, "Please Enter Your Email or Password", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    progressDialog.show();

//                    signing in with email and password
                    auth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
//for getting notification if user signed in through other device
                                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String token) {
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("token", token);
                                                database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                                                        .updateChildren(map);

//                Toast.makeText(MainActivity.this, token , Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                }
            }
        });


        binding.txtViewSignupwithPhnone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                kiran Activity
                Intent intent = new Intent(SignInActivity.this, SignInPhone.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });

        binding.txtClickSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        binding.ForegetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });


        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();

            }
        });


//        now if the usrer once signin then he must remin in the main activity
//        every time the user donot need to enter his password
//        for that we will write one condition over here


        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }


    }

    EditText emailet;
    String email;

    //    method for forget password
//    #############################################################
//    Dialog box with userdefiened design in the dialog itself
    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
// creating an edittext inside the dialog box
        emailet = new EditText(this);
        // write the email using which you registered
        emailet.setHint("Your Email");
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                email = emailet.getText().toString().trim();

//            new method for showing progress bar
                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    //    to recover the password using firebase
    private void beginRecovery(String email) {
        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Sending Email....\nPlease check your SPAM Folder");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // calling sendPasswordResetEmail
        // open your email and write the new
        // password and then you can login
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if (task.isSuccessful()) {
                    // if isSuccessful then done message will be shown
                    // and you can change the password
                    Toast.makeText(SignInActivity.this, "Mail sent to " + email, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignInActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(SignInActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
            }
        });

    }


    int RC_SIGN_IN = 65;

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
//we are using intent in signIn mthod so the intent passes info from one page to another
//    here onActivityResult method is being used to get the information from the google abt the signin

    //    the information is taken by the firebaseAuthWithgoogle() method
//    this method takes the information if the RC_SIGN CODE match with the request code
//    the paramter is passed as  getIdToken for firebaseAuthWithgoogle() method
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "Got ID token." + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("TAG", "google sign in failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Log.d("TAG", "signInWithCredential : Success");
                    FirebaseUser user = auth.getCurrentUser();
                    Users users = new Users();
                    users.setUserId(user.getUid());
                    users.setUsername(user.getDisplayName());
                    users.setProfilepicture(user.getPhotoUrl().toString());
//
                    //we can get the mail through google by this method
                    users.setMail(task.getResult().getUser().getEmail());
                    //it is important to set " " contact as this bcoz google donot provide contact and empty contact will not allow to seearch in the list
//                    so add the blank contact and ovveride it further after signing in
//                    this will allow to give some time for google process and signing in
                    users.setContact(" ");

//                    to set the google contact if not exist and if exist then
                    database.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild("users/" + user.getUid())) {
                                return;
                            } else {
                                database.getReference().child("users").child(user.getUid()).setValue(users);

                                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String token) {
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("token", token);
                                        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                                                .updateChildren(map);

//                Toast.makeText(MainActivity.this, token , Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

//                    database.getReference().child("users").child(user.getUid()).setValue(users);

                    Toast.makeText(SignInActivity.this, user.getEmail(), Toast.LENGTH_LONG).show();

//                    to store the data in the database we are using this databse.getreference()......
//                    the "users" used in child is the users that we have created in the FIREBASE real time database so give this appropriately
//                    database.getReference().child("users").child(user.getUid()).setValue(users);

//for showing the registration contact option
//                    showDialog_box();
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

//                    updateUI(user);
                } else {
                    Log.w("TAG", "signInWithCredential : failure", task.getException());

//            Snackbar.make()

                }
            }
        });

    }

}
