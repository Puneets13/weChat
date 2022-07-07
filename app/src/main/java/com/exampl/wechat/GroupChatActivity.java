package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.exampl.weChat.databinding.ActivityGroupChatBinding;
import com.exampl.wechat.Models.MessagesModel;
import com.exampl.wechat.Adapters.ChatAdapter;
import com.exampl.weChat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener  {

    ActivityGroupChatBinding binding;
    FirebaseDatabase database ;
    FirebaseAuth auth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
//        to hide the toolbar
        getSupportActionBar().hide();
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final ArrayList<MessagesModel> messagesModels = new ArrayList<>();

        final String SenderId = FirebaseAuth.getInstance().getUid();
//we will give a new name to this gropu by using a new activty
        binding.username.setText("CHAT ROOM");
        final ChatAdapter adapter = new ChatAdapter(messagesModels, this);
        binding.chatrecyclerview.setAdapter(adapter);

        binding.profileImage.setImageResource(R.drawable.ic_launcher_foreground);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatrecyclerview.setLayoutManager(layoutManager);
//        for displaying activity over the RecyclerView
        database.getReference().child("groupChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesModels.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessagesModel model = dataSnapshot.getValue(MessagesModel.class);
                    messagesModels.add(model);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = binding.etMessage.getText().toString();
                if (message.isEmpty()) {
                    binding.etMessage.setError("ENTER YOUR MESSAGE");
                    return;
                }
                final MessagesModel model = new MessagesModel(SenderId, message);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String timeresult = sdf.format(new Date());
                model.setTimestamp((timeresult));
//                model.setTimestamp(new Date().getTime());
                binding.etMessage.setText("");

                database.getReference().child("groupChat").push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

            }
        });


        binding.dotsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopup(v);
            }
        });

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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //to perofrmon some action when the item is being clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.All_chat:
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.clear_grpChat:
//clearing chat code

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this, R.style.AlertDialTheme);
                View view = LayoutInflater.from(GroupChatActivity.this).inflate(R.layout.custom_dialog_warning,
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
                        database.getReference().child("groupChat").setValue(null);
//
                        Toast.makeText(GroupChatActivity.this, "CHAT DELETED", Toast.LENGTH_SHORT).show();

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
        popup.inflate(R.menu.group_menu);
        popup.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.All_chat:
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.clear_grpChat:
//clearing chat code

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this, R.style.AlertDialTheme);
                View view = LayoutInflater.from(GroupChatActivity.this).inflate(R.layout.custom_dialog_warning,
                        (ConstraintLayout) findViewById(R.id.layoutDialogContainer)); //id of the main Constraint layout in custom dialog box xml file

                builder.setView(view);
                ((TextView) view.findViewById(R.id.texttitle)).setText("Clear Group Chat");
                ((TextView) view.findViewById(R.id.textmessage)).setText("Are you sure you want to Clear Group Chat");
                ((Button) view.findViewById(R.id.buttonAction)).setText("Clear");
                ((ImageView) view.findViewById(R.id.ImgIcon)).setImageResource(R.drawable.ic_security);
                final AlertDialog alertDialog = builder.create();

                alertDialog.setCancelable(true);
                view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        database.getReference().child("groupChat").setValue(null);
//
                        Toast.makeText(GroupChatActivity.this, "CHAT DELETED", Toast.LENGTH_SHORT).show();

                        alertDialog.dismiss();


                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();

                return true;
            default:
                return false;

        }

    }
}