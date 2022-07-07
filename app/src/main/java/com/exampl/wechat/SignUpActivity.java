package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.exampl.weChat.databinding.ActivitySignUpBinding;
import com.exampl.wechat.Models.Users;
import com.exampl.weChat.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class SignUpActivity extends AppCompatActivity {

    //    instead of using findViewById we will use ViewBinding Concept
    ActivitySignUpBinding binding;

    //    for makking connection with Firebase and Database
    private FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    //    for adding Loading(Buffering ) when user clicks on signin/signup option
    ProgressDialog progressDialog;
    //for checeking the contact and maintiaing contact list

    //    for checking that contact is correct or not
    boolean checkContact = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        to remove the toolbar
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");


        GoogleSignInOptions gso = (new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


//        for rechecking the signin button i used do while loop

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                checkContact = true;

                String mail = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
//                String phoneNumber = binding.etUserContact.getText().toString();
                if (mail.isEmpty() && password.isEmpty()) {
                    binding.etEmail.setError("ENTER YOUR EMAIL OR PASSWORD");
                    Toast.makeText(SignUpActivity.this, "Please Enter Your Email or Password", Toast.LENGTH_LONG).show();

                } else {
                    // to show the dialog box that your account is being creating
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
//                             to close the dialog box that your account has been created
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Users user = new Users(binding.etusername.getText().toString(), binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
                                        String id = task.getResult().getUser().getUid();
                                        database.getReference().child("users").child(id).setValue(user);
//user.setUserId(id);
                                        Toast.makeText(SignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK|intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }

        });


//if user alredy have an account then
        binding.txtAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });


        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                we need to add delay for sometime inorder to add the user contact then only we can register with google
                signUp();

            }
        });


//        now if the usrer once signin then he must remin in the main activity
//        every time the user donot need to enter his password
//        for that we will write one condition over here


        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    int RC_SIGN_IN = 65;

    public void signUp() {
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
                    Log.d("TAG", "signUpWithCredential : Success");
                    FirebaseUser user = auth.getCurrentUser();
                    Users users = new Users();
                    users.setUserId(user.getUid());
                    users.setUsername(task.getResult().getUser().getDisplayName().toString());
//                    it is important to set " " contact as this bcoz google donot provide contact and empty contact will not allow to seearch in the list
//                    so add the blank contact and ovveride it further after signing in
//                    this will allow to give some time for google process and signing in
//                    users.setContact(" ");
                    users.setProfilepicture(task.getResult().getUser().getPhotoUrl().toString());
                    //we can get the mail through google by this method
                    users.setMail(task.getResult().getUser().getEmail());
//                    users.setPassword("123456@default");


//                    to set the google contact if not exist and if exist then
//                    if the user already exists then donot make the new user ..get the info from the databse only

//                    database.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if(snapshot.hasChild("users/"+user.getUid())){
//                                return;
//                            }
//                            else{
//                                database.getReference().child("users").child(user.getUid()).setValue(users);
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });

                                database.getReference().child("users").child(user.getUid()).setValue(users);

                    Toast.makeText(SignUpActivity.this, user.getEmail(), Toast.LENGTH_LONG).show();
//                    to store the data in the database we are using this databse.getreference()......
//                    the "users" used in child is the users that we have created in the FIREBASE real time database so give this appropriately
//                    database.getReference().child("users").child(user.getUid()).setValue(users);

//                    showDialog_box();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.w("TAG", "signUpWithCredential : failure", task.getException());

                }
            }
        });


    }

}




