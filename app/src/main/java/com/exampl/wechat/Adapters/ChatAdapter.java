package com.exampl.wechat.Adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.exampl.wechat.Models.MessagesModel;
import com.exampl.wechat.ViewImage;
import com.exampl.weChat.R;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//first create a viewholder
//since there are two Layouts SENDER AND RECEIVER so we will make 2 VIEWHOLDERS here

//here since we have two viewHodlder so we cannot pass that into the RecyclerView.Adapter<>
//so for that we will make one method
public class ChatAdapter extends RecyclerView.Adapter {
    ArrayList<MessagesModel> messagesModels;
    Context context;
    String recid;
    String senderRoom, receiverRoom;
    //   TWO VARIABLES HAVE BEEN CREATED TO IDENTIFY THE VIEW
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context, String recid, String senderRoom, String receiverRoom) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.recid = recid;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
//        if the id of the person that have login to the app matches with the sender then its the sender
//        else its a receiver
        if (messagesModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


//        position will tell where to place the model
        MessagesModel messagesModel = messagesModels.get(position);
//        to delete a message we will use this and we will use setOnLongClickLostener
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override

            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialTheme);
                View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_warning,
                        (ConstraintLayout) v.findViewById(R.id.layoutDialogContainer)); //id of the main Constraint layout in custom dialog box xml file

                builder.setView(view);
                ((TextView) view.findViewById(R.id.texttitle)).setText("Delete Chat");
                ((TextView) view.findViewById(R.id.textmessage)).setText("Are you sure you want to Delete Chat");
                ((Button) view.findViewById(R.id.buttonAction)).setText("Delete");
                ((ImageView) view.findViewById(R.id.ImgIcon)).setImageResource(R.drawable.ic_security);
                final AlertDialog alertDialog = builder.create();

                view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String senderRoom = FirebaseAuth.getInstance().getUid() + recid;
                        database.getReference().child("chats").child(senderRoom).child(messagesModel.getMessageId()).setValue(null);
                        Toast.makeText(context, "CHAT DELETED", Toast.LENGTH_SHORT).show();

                        alertDialog.dismiss();

                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();

                return false;
            }
        });


//        to set click listener on image button
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messagesModel.getMessage().equals("Photo")) {
                    Toast.makeText(context, "Opening Image", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    String senderRoom = FirebaseAuth.getInstance().getUid() + recid;
                    database.getReference().child("chats").child(senderRoom).child(messagesModel.getMessageId())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                               to get the specific child of the node use this single line statement
                                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
//                                    System.out.println("image url is " + imageUrl);
//                                     Adapter cannot pass the intent bcoz it donot extend AppCompactActivity
//                                    so pass teh value using interface
//                                     or use context with it and make sure the Constructor for adapter should exist
//                                    Intent intent = new Intent(ChatAdapter.this, ViewImage.class);
                                    Intent intent = new Intent(context, ViewImage.class);
                                    intent.putExtra("url_image", imageUrl);
                                    context.startActivity(intent);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }


            }
        });

        // adding reactions to chat

        int reactions[] = new int[]{
                R.drawable.reaction1,
                R.drawable.reaction2,
                R.drawable.reaction3,
                R.drawable.reaction4,
                R.drawable.reaction5,
                R.drawable.reaction6
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (pos >= 0){

                if (holder.getClass() == SenderViewHolder.class) {
                    ((SenderViewHolder) holder).feeling.setImageResource(reactions[pos]);
                    ((SenderViewHolder) holder).feeling.setVisibility(View.VISIBLE);
                } else {
                    ((RecieverViewHolder) holder).feeling_rec.setImageResource(reactions[pos]);
                    ((RecieverViewHolder) holder).feeling_rec.setVisibility(View.VISIBLE);

                }
            messagesModel.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child(messagesModel.getMessageId()).setValue(messagesModel);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child(messagesModel.getMessageId()).setValue(messagesModel);
        }
            return true;   // true is closing popup, false is requesting a new selection
        });


//since here we have two different holders so we use getclass() method to know which class message is to be displayed
        if (holder.getClass() == SenderViewHolder.class) {
//            SenderViewHolder viewHolder = (SenderViewHolder)holder ;
            ((SenderViewHolder) holder).sendermsg.setText(messagesModel.getMessage());
            ((SenderViewHolder) holder).senderTime.setText(messagesModel.getTimestamp());
            ((SenderViewHolder) holder).senderImage.setVisibility(View.GONE);
            ((SenderViewHolder) holder).sendermsg.setVisibility(View.VISIBLE);

            if(messagesModel.getFeeling()>=0){
//                messagesModel.setFeeling(reactions[(int)messagesModel.getFeeling()]);
                ((SenderViewHolder) holder).feeling.setImageResource(reactions[messagesModel.getFeeling()]);
                ((SenderViewHolder) holder).feeling.setVisibility(View.VISIBLE);

            }else{
                ((SenderViewHolder) holder).feeling.setVisibility(View.GONE);

            }

//for adding reactions to message
//adding touch listener on the background layout for reactions
//            if we add this on the text then it would override with the delete message
            ((SenderViewHolder) holder).sendermsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);

                    return false;
                }
            });


//       for sendnig image over chat we need to dd this visibilty bcoz we hide that while making layout
            if (messagesModel.getMessage().equals("Photo")) {
                ((SenderViewHolder) holder).senderImage.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).sendermsg.setVisibility(View.GONE);
//                Picasso.get().load(messagesModel.getImageUrl()).placeholder(R.drawable.image_placeholder1).into(viewHolder.senderImage);
                Glide.with(context).load(messagesModel.getImageUrl()).placeholder(R.drawable.image_placeholder1).into(((SenderViewHolder) holder).senderImage);
            }

        } else {
//            RecieverViewHolder viewHolder = (RecieverViewHolder) holder ;
            ((RecieverViewHolder) holder).receivermsg.setText(messagesModel.getMessage());
            ((RecieverViewHolder) holder).receiverTime.setText(messagesModel.getTimestamp());
            String ReceiverImage = messagesModel.getReceiverImage();
            Picasso.get().load(ReceiverImage).placeholder(R.drawable.user).into(((RecieverViewHolder) holder).ReceiverImage);
            ((RecieverViewHolder) holder).receiverImage.setVisibility(View.GONE);
            ((RecieverViewHolder) holder).receivermsg.setVisibility(View.VISIBLE);

            if(messagesModel.getFeeling()>=0){

                ((RecieverViewHolder) holder).feeling_rec.setImageResource(reactions[messagesModel.getFeeling()]);
                ((RecieverViewHolder) holder).feeling_rec.setVisibility(View.VISIBLE);

            }else{
                ((RecieverViewHolder) holder).feeling_rec.setVisibility(View.GONE);

            }

//          for ading feelings recation for receiver
            ((RecieverViewHolder) holder).receivermsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);

                    return false;
                }
            });


            //for receiving image over chat we need to dd this visibilty bcoz we hide that while making layout
            if (messagesModel.getMessage().equals("Photo")) {


                ((RecieverViewHolder) holder).receiverImage.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).receivermsg.setVisibility(View.GONE);
//                Picasso.get().load(messagesModel.getImageUrl()).placeholder(R.drawable.image_placeholder1).into(viewHolder.receiverImage);
                Glide.with(context).load(messagesModel.getImageUrl()).placeholder(R.drawable.image_placeholder1).into(((RecieverViewHolder) holder).receiverImage);

            }


        }

    }

    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder {
        TextView receivermsg, receiverTime;
        ImageView receiverImage, feeling_rec,ReceiverImage;
        ConstraintLayout receiver_msg_bg;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            receivermsg = itemView.findViewById(R.id.receivertext);
            receiverTime = itemView.findViewById(R.id.receivertime);
            receiverImage = itemView.findViewById(R.id.image_receiver);
            receiver_msg_bg = itemView.findViewById(R.id.receiver_msg_background);
            feeling_rec = itemView.findViewById(R.id.feeling_rec);
            ReceiverImage = itemView.findViewById(R.id.userMsgImage);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView sendermsg, senderTime;
        ImageView senderImage, feeling;
        ConstraintLayout sender_msg_bg;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sendermsg = itemView.findViewById(R.id.sendertxt);
            senderTime = itemView.findViewById(R.id.sendertime);
            senderImage = itemView.findViewById(R.id.image_Sender);
            sender_msg_bg = itemView.findViewById(R.id.sender_msg_background);
            feeling = itemView.findViewById(R.id.feeling);
        }
    }

//public interface getImageUrl_interface{
//        void getUrl_method(String url);
//}

}