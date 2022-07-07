package com.exampl.wechat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exampl.wechat.ChatDetailActivity;
import com.exampl.wechat.profile_viewActivity;
import com.exampl.wechat.Models.Users;
import com.exampl.weChat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    ArrayList<Users> list;
    Context context;
    String Sender_Contact;

    ArrayList<Users> mlist ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public UsersAdapter(ArrayList<Users> list, Context context, String contact) {
        this.list = list;
        this.context = context;
        this.Sender_Contact = contact;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_users, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position);
//to get the image from the internet source example from GOOGLE SIGN IN we need to use PECASSO
        Picasso.get().load(users.getProfilepicture()).placeholder(R.drawable.user).into(holder.image);
        holder.userName.setText(users.getUsername());
//to place the image from the storage to the Users data



        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        try {
                            Sender_Contact = users.getContact();

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        this is done to update the lastmessage on the chat
//        in firebase we will go to chat node and then get their id and we will orderby the chat according to their timeStamp
//        that is in decreasing order of their time
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getUid() + users.getUserId())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                holder.lastmessage.setText(snapshot1.child("message").getValue().toString());
                                holder.lastmsgtime.setText(snapshot1.child("timestamp").getValue().toString());

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//to set on click listner on the recycler view event
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userid", users.getUserId());
                intent.putExtra("profilepic", users.getProfilepicture());
                intent.putExtra("userName", users.getUsername());
                intent.putExtra("contact", users.getContact());
                intent.putExtra("status", users.getStatus());
                intent.putExtra("Sender_contact", Sender_Contact);
intent.putExtra("token",users.getToken());
                context.startActivity(intent);
            }
        });

//        to set the onClick listener on the profile image on chat section
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, profile_viewActivity.class);
                intent1.putExtra("userid", users.getUserId());
                intent1.putExtra("profilepic", users.getProfilepicture());
                intent1.putExtra("userName", users.getUsername());
                intent1.putExtra("contact", users.getContact());
                intent1.putExtra("status", users.getStatus());
                intent1.putExtra("Sender_contact", Sender_Contact);

                context.startActivity(intent1);
                Toast.makeText(context, users.getUsername() + " PROFILE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView userName, lastmessage, lastmsgtime;
        ImageView favorite_btn;
//        DatabaseReference favouriteref;
//        FirebaseDatabase database = FirebaseDatabase.getInstance();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.txtusername);
            lastmessage = itemView.findViewById(R.id.txtlastmessage);
            lastmsgtime = itemView.findViewById(R.id.txtlastTime);


        }
    }




}
