package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.exampl.weChat.R;
import com.exampl.weChat.databinding.ActivityChatDetailBinding;

import com.exampl.wechat.Adapters.ChatAdapter;
import com.exampl.wechat.Models.MessagesModel;
import com.exampl.wechat.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatDetailActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String contact, receiverId, senderId, userName, profilepic, status, message , userNotificationName;
    String sender_contact;
    final ArrayList<MessagesModel> messagesModels = new ArrayList<>();
    FirebaseStorage storage;
    String senderRoom = "";
    String ReceiverRoom = "";
    Uri uri;
    ProgressDialog progressDialog;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 13;
    String status_chatdetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Image");
        progressDialog.setCancelable(false);

//        auth is being used to get the userId from he databse
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
//       the one user who is signed in is the sender
//        all other s users are the receiver
//        Sender and userid are requiqred to chat with one and another user
//  sender id can be easily get usgin the firebase
//        by making the sender id final we can use it gloabally

//        intent from userAdapter
        senderId = auth.getUid();
        receiverId = getIntent().getStringExtra("userid");
        userName = getIntent().getStringExtra("userName");

        profilepic = getIntent().getStringExtra("profilepic");
        contact = getIntent().getStringExtra("contact");
        status = getIntent().getStringExtra("status");
        sender_contact = getIntent().getStringExtra("Sender_contact");
        String token = getIntent().getStringExtra("token");

        senderRoom = senderId + receiverId;
        ReceiverRoom = receiverId + senderId;


        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        try {
                            userNotificationName = users.getUsername();

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        binding.username.setText(userName);
        Picasso.get().load(profilepic).placeholder(R.drawable.user).into(binding.profileImage);
//for send button
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });

//        for making phn call and registering the number
        binding.PhnCallChatDetail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sender_contact.equals("")) {
                    Toast.makeText(ChatDetailActivity.this, "Register Yourself", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatDetailActivity.this, R.style.AlertDialTheme);
                    View view = LayoutInflater.from(ChatDetailActivity.this).inflate(R.layout.custom_dialog_warning,
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
                    Toast.makeText(ChatDetailActivity.this, userName+ " Contact not Uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatDetailActivity.this, "Calling.." + contact, Toast.LENGTH_SHORT).show();
                    phn_call_method();
                }


            }
        });


//        to set the CHatAdapter here in list view to share the data
        final ChatAdapter chatAdapter = new ChatAdapter(messagesModels, this, receiverId, senderRoom, ReceiverRoom);

        binding.chatrecyclerview.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatrecyclerview.setLayoutManager(layoutManager);

//        for making chatrecycler view smaller when keyboard opens
        layoutManager.setStackFromEnd(true);
        // for chat node in DATABASE ..creating these strings
//        final variable is used to create a global variable
//
//    GETTING DATA FROM DATABASE USING FIREBASE
//        WHENEVER WE NEED TO TAKe DATA FROM DATABASE WE USE getRefference() ; method


        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if we dont use this then each message will be displayed number of times ..to avoid this we use this
                        messagesModels.clear();
//                             for each loop will be used to get all the messages
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessagesModel model = snapshot1.getValue(MessagesModel.class);
                            model.setMessageId(snapshot1.getKey());
                            messagesModels.add(model);
                        }
//                        Collections.sort(messagesModels);
//to update the recycler view at the moment send button is clicked
                        chatAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


//        to check the receiver status that he's online or not ONLINE/OFFLINE
        database.getReference().child("presence").child(receiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            status_chatdetail = snapshot.getValue(String.class);
                            if (!status_chatdetail.isEmpty()) {

                                if (status_chatdetail.equals("offline")) {
                                    binding.statusChatdetail.setVisibility(View.GONE);
                                } else {
                                    binding.statusChatdetail.setText(status_chatdetail);
                                    binding.statusChatdetail.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


//        to show TYPING ..... STATUS
//        textWatcher is used to view that the text has been changed

//        create new handler
        final Handler handler = new Handler();

        binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderId).setValue("typing...");
                //                to remove the typing status when keyboard is stoped
                handler.removeCallbacksAndMessages(null);
//                to change the status online after typing has been stopped ..we use handler to delay it for 1 sec
                handler.postDelayed(userStopTyping, 1000);
            }

            Runnable userStopTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderId).setValue("Online");

                }
            };

        });


//  setting onClick listener on SEND button
        binding.sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etMessage.getText().toString().isEmpty()) {
                    binding.etMessage.setError("ENTER YOUR MESSAGE");
                    return;
                }
//                Toast.makeText(ChatDetailActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
//                to get the message typed by the person from message box on pressing send button
                message = binding.etMessage.getText().toString();
// since we need to send the message and senderId to the database so we use this in our model
                final MessagesModel model = new MessagesModel(senderId, message);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String timeresult = sdf.format(new Date());
                model.setTimestamp((timeresult));
                model.setReceiverImage(profilepic);
//                model.setImageUrl(null);

//to empty the message box after send button clicked
                binding.etMessage.setText("");


//                for making same ey for sneder and reciever we will use
//               for adding feelings we will use same id for that we use randomKey
                String randomKey = database.getReference().push().getKey();

//to create the node of the chat user in the database to store each message of sender and receiver seprately
//                we will do this for SENDER MESSAGE STORING
                database.getReference().child("chats").child(senderRoom)//senderroom bcz on pressing send button the sender message will be stored in node
                        .child(randomKey)  //push() is used to convert the timming of the message sent in database to String
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//FOR RECEIVER MESSAGE STORING WE WILL DO THIS
                                database.getReference().child("chats")
                                        .child(ReceiverRoom).child(randomKey)
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sendNotification(userNotificationName, message, token);
                                            }
                                        });
                            }
                        });


            }
        });

//        for setting popup menu on chatS DETAIL activity on dots...
//        i created one showpop up menu button and and a layout also in menu folader and implemnted a method above with class as onmenuPopUp
//        it is important to select popup menu only then only it will work
        binding.dotsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopup(v);
            }
        });




//        for attachement sharing file
        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
//to get all type of activity from phon use --->   */*
//                this will give gallery..mp3 .. media everything

                startActivityForResult(intent, 25);

            }
        });


//        for viewing profile on taping
        binding.viewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailActivity.this, viewProfileActivity.class);
                intent.putExtra("username", userName);
                intent.putExtra("profilepic", profilepic);
                intent.putExtra("contact", contact);
                intent.putExtra("status", status);

                startActivity(intent);

            }
        });
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailActivity.this, viewProfileActivity.class);
                intent.putExtra("username", userName);
                intent.putExtra("profilepic", profilepic);
                intent.putExtra("contact", contact);
                intent.putExtra("status", status);

                startActivity(intent);
                finish();
            }
        });

        binding.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailActivity.this, viewProfileActivity.class);
                intent.putExtra("username", userName);
                intent.putExtra("profilepic", profilepic);
                intent.putExtra("contact", contact);
                intent.putExtra("status", status);

                startActivity(intent);
                finish();
            }
        });
//        for making chatrecycler view smaller when keyboard opens
        binding.chatrecyclerview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom <= oldBottom) {
                    binding.chatrecyclerview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.chatrecyclerview.smoothScrollToPosition(bottom);
                        }
                    }, 100);
                }
            }
        });

    }


//    for  generating gNotifications

    void sendNotification(String name, String message, String Token) {
        try {

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";
//    the data is sent to the API in the Form of JSON format
            JSONObject data = new JSONObject();
            data.put("title", name);
            data.put("body", message);
            JSONObject notificationData = new JSONObject();
            notificationData.put("notification", data);
            notificationData.put("to", Token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
//                            Toast.makeText(ChatDetailActivity.this, "notification gyi", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(ChatDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                //           too send the secure authentication we use getHeaders to send the API keys
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    String key = "key=AAAADnSKxR0:APA91bHmfirBZducd87-NH0JZwsqDJSy5C0_tr2AZNEzvFSSocI7PwP1Lvjd_3-17RWTwiGbbXPW-GhNKSAuQqsXKYTf66qcRWzPdDHFFt0m4pBZW-EjzoEnV_TAR2EIhQ8k3tjNt4de";
                    map.put("Content-Type", "application/json");
                    map.put("Authorization", key);

                    return map;
                }
            };
            queue.add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //    for getting image form gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        getData() methods contain the file path that has been selected by the user
        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectedImage = data.getData();
//                    for creating unique id for storing image we are using Calender time bcoz its unique
                    Calendar calendar = Calendar.getInstance();
                    final StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
//                    setting id as the sender id itself so that we can retrieve when required
//                    final StorageReference reference =   storage.getReference().child("chats").child(FirebaseAuth.getInstance().getUid() );

//now to upload the image to storage
//                   to show the dialog box
                    progressDialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                           if task is successfull then we will take the url of the image
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String Filepath = uri.toString();

//                                        Toast.makeText(ChatDetailActivity.this, "Media sent", Toast.LENGTH_SHORT).show();
//                to get the message typed by the person from message box on pressing send button
// since we need to send the message and senderId to the database so we use this in our model

//                                        message needs to be present in message otherwise exception would be thrown

//                                        String message = binding.etMessage.getText().toString();
                                        final MessagesModel model = new MessagesModel(senderId, message);
                                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                                        String timeresult = sdf.format(new Date());
                                        model.setTimestamp((timeresult));
                                        model.setMessage("Photo");
                                        model.setImageUrl(Filepath);
//                                        binding.etMessage.setText("");

//to empty the message box after send button clicked
//to create the node of the chat user in the database to store each message of sender and receiver seprately
//                we will do this for SENDER MESSAGE STORING
                                        database.getReference().child("chats").child(senderRoom)//senderroom bcz on pressing send button the sender message will be stored in node
                                                .push()    //push() is used to convert the timming of the message sent in database to String
                                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//FOR RECEIVER MESSAGE STORING WE WILL DO THIS
                                                        database.getReference().child("chats")
                                                                .child(ReceiverRoom).push()
                                                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                    }
                                                                });
                                                    }
                                                });


                                    }
//                                    Toast.makeText(ChatDetailActivity.this, Filepath, Toast.LENGTH_SHORT).show();

                                });
                            }
                        }
                    });

                }

            }


        }
    }


    //    we need to request the permission for making call at the RUN TIME
//    else it will shows the error
    public void phn_call_method() {
        String number = contact;
        if (getIntent().getStringExtra("contact").equals("")) {
            Toast.makeText(ChatDetailActivity.this, "PLEASE ENTER VALID NUMBER", Toast.LENGTH_SHORT).show();
        } else {
            String s = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(s));
//            to check if the permission was granted previously or not
            if (ContextCompat.checkSelfPermission(ChatDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(ChatDetailActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);

                // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                //You already have permission
                try {
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


    //    to add menu to the mainActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //to perofrmon some action when the item is being clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.All_chat:
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.ViewProfile:

//                after logging out where do the user moves for that make intent
                Intent intent1 = new Intent(ChatDetailActivity.this, viewProfileActivity.class);
                intent1.putExtra("username", userName);
                intent1.putExtra("profilepic", profilepic);
                intent1.putExtra("contact", contact);
                intent1.putExtra("status", status);

//                startActivity(intent1);
                finish();
                break;
            case R.id.Clearchat:
//clearing chat code

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatDetailActivity.this, R.style.AlertDialTheme);
                View view = LayoutInflater.from(ChatDetailActivity.this).inflate(R.layout.custom_dialog_warning,
                        (ConstraintLayout) findViewById(R.id.layoutDialogContainer)); //id of the main Constraint layout in custom dialog box xml file

                builder.setView(view);
                ((TextView) view.findViewById(R.id.texttitle)).setText("Clear Chat");
                ((TextView) view.findViewById(R.id.textmessage)).setText("Are you sure you want to Clear Chat");
                ((Button) view.findViewById(R.id.buttonAction)).setText("Clear");
                ((ImageView) view.findViewById(R.id.ImgIcon)).setImageResource(R.drawable.ic_security);
                final AlertDialog alertDialog = builder.create();

//                alertDialog.dismiss();
                view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                        database.getReference().child("chats").child(senderRoom).setValue(null);
//
                        Toast.makeText(ChatDetailActivity.this, "CHAT DELETED", Toast.LENGTH_SHORT).show();

                        alertDialog.dismiss();


                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();

                break;

        }


        return super.onOptionsItemSelected(item);
    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.chat_menu);
        popup.show();
    }

    //    for popupMenu
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.All_chat:
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.ViewProfile:

//                after logging out where do the user moves for that make intent
                Intent intent1 = new Intent(ChatDetailActivity.this, viewProfileActivity.class);
                intent1.putExtra("username", userName);
                intent1.putExtra("profilepic", profilepic);
                intent1.putExtra("contact", contact);
                intent1.putExtra("status", status);

                startActivity(intent1);
                return true;

//                break;
            case R.id.Clearchat:
//clearing chat code

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatDetailActivity.this, R.style.AlertDialTheme);
                View view = LayoutInflater.from(ChatDetailActivity.this).inflate(R.layout.custom_dialog_warning,
                        (ConstraintLayout) findViewById(R.id.layoutDialogContainer)); //id of the main Constraint layout in custom dialog box xml file

                builder.setView(view);
                ((TextView) view.findViewById(R.id.texttitle)).setText("Clear Chat");
                ((TextView) view.findViewById(R.id.textmessage)).setText("Are you sure you want to Clear Chat");
                ((Button) view.findViewById(R.id.buttonAction)).setText("Clear");
                ((ImageView) view.findViewById(R.id.ImgIcon)).setImageResource(R.drawable.ic_security);
                final AlertDialog alertDialog = builder.create();

                view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                        database.getReference().child("chats").child(senderRoom).setValue(null);
//
                        Toast.makeText(ChatDetailActivity.this, "CHAT CLEARED", Toast.LENGTH_SHORT).show();

                        alertDialog.dismiss();


                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();


//                break;
                return true;
            default:
                return false;

        }

    }

}



