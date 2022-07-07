package com.exampl.wechat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exampl.wechat.Models.Users;
import com.exampl.wechat.PhoneCallActivity;
import com.exampl.weChat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder>{
    ArrayList<Users> list;
    Context context;
    String Sender_number_calldetail;

    String senderRoom,receiverRoom ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public CallAdapter(ArrayList<Users> list, Context context,String Sender_number_calldetail ) {
        this.list = list;
        this.context = context;
        this.Sender_number_calldetail=Sender_number_calldetail;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_phn_call, parent, false);

        return new CallAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//for updating the String Sender_number_chatdetial when updated from user end from phone call activity
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        try {
                            Sender_number_calldetail = users.getContact();

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        Users users = list.get(position);
        Picasso.get().load(users.getProfilepicture()).placeholder(R.drawable.user).into(holder.image);

        holder.userName.setText(users.getUsername());
        holder.phn_num.setText(users.getContact());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhoneCallActivity.class);
                intent.putExtra("userid", users.getUserId());
                intent.putExtra("profilepic", users.getProfilepicture());
                intent.putExtra("userName", users.getUsername());
                intent.putExtra("contact",users.getContact());
                intent.putExtra("Sender_contact",Sender_number_calldetail);
                context.startActivity(intent);
            }
        });

//        holder.phn_num.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "calling...", Toast.LENGTH_SHORT).show();
//            }
//        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView userName, phn_num;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.userCallImg);
            userName = itemView.findViewById(R.id.username);
            phn_num = itemView.findViewById(R.id.PhnNumber);

        }

    }




}
