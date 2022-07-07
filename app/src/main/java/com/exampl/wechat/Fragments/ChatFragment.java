package com.exampl.wechat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exampl.weChat.databinding.FragmentChatBinding;
import com.exampl.wechat.Adapters.UsersAdapter;
import com.exampl.wechat.Models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    public ChatFragment() {
    }

    FragmentChatBinding binding;
    ArrayList<Users> list = new ArrayList<>();
    ArrayList<Users> mlist = new ArrayList<>();

    FirebaseDatabase database;

    FirebaseAuth auth;
    String sender_contact;
    UsersAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        UsersAdapter adapter = new UsersAdapter(list, getContext(), sender_contact);
         layoutManager = new LinearLayoutManager(getContext());
        binding.chatrecyclerview.setLayoutManager(layoutManager);
        binding.chatrecyclerview.setAdapter(adapter);

//        binding.searchUsers.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                searchUsers(s.toString());
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

//        adapter1.setClickListener(this);
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                if (binding.searchUsers.getText().toString().equals("")) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Users users = dataSnapshot.getValue(Users.class);
                        users.setUserId(dataSnapshot.getKey());
//                 we need to apply the condition that the user that has been logged in to the watsp should not be displayed
                        if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                            list.add(users);
                        } else {
                            sender_contact = users.getContact();
                        }

                    }
//                adapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();

                }
//            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();

    }

//    private void searchUsers(String s) {
//        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
//        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("username")
//                .startAt(s).endAt(s + "\uf8ff");
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mlist.clear();
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    Users user = snapshot1.getValue(Users.class);
//
//                    assert user != null;
//                    assert fuser != null;
//                    try {
//                        if (!user.getUserId().equals(fuser.getUid())) {
//                            mlist.add(user);
//                            adapter.notifyDataSetChanged();
//
//                        }
//                    } catch (NullPointerException e) {
//
//                    }
//                }
//
//
//                adapter = new UsersAdapter(mlist, getContext(), sender_contact);
//                binding.chatrecyclerview.setAdapter(adapter);
//                layoutManager = new LinearLayoutManager(getContext());
//                binding.chatrecyclerview.setLayoutManager(layoutManager);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//    }

}