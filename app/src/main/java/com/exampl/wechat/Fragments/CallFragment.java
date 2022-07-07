package com.exampl.wechat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exampl.weChat.databinding.FragmentCallBinding;
import com.exampl.wechat.Adapters.CallAdapter;
import com.exampl.wechat.Models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CallFragment extends Fragment {
    FragmentCallBinding binding;
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;
    String sender_contact1;
    FirebaseAuth auth;

    public CallFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCallBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        CallAdapter adapter = new CallAdapter(list, getContext(), sender_contact1);
        binding.callRecyclerview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.callRecyclerview.setLayoutManager(layoutManager);
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());
//                 we need to apply the condition that the user that has been logged in to the watsp should not be displayed
                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                        list.add(users);
                    } else {
                        sender_contact1 = users.getContact();
                    }

                }
//                adapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    //    for showing current status to others
//    to show online when mainActivity Opens
    @Override
    public void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("online");


    }

    //when the activity closes then the offline status should be displayed for that use onPausemethodd
    // use it in if condition ...that if it is not null then show offline

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