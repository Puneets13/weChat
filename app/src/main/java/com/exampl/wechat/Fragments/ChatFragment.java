package com.exampl.wechat.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.SearchView;
//import android.widget.SearchView;

import com.exampl.weChat.R;
import com.exampl.weChat.databinding.FragmentChatBinding;

import com.exampl.wechat.Adapters.UsersAdapter;
import com.exampl.wechat.Models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    UsersAdapter adapter1;
    LinearLayoutManager layoutManager, layoutManager1;

    //    for adding search View in toolbar
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

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


//for adding search view to the chat fragment
//        binding.searchUsers.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                searchUsers(s.toString());
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//
//
//        });


        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (binding.searchUsers.getText().toString().equals(""))
                if (searchView != null) {
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
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }

    //    to search the user in the list
    private void searchUsers(String s) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("username")
                .startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mlist.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Users user = snapshot1.getValue(Users.class);

                    assert user != null;
                    assert fuser != null;
                    if (!user.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                        mlist.add(user);
                    } else {
                        sender_contact = user.getContact();
                    }
                }


                adapter1 = new UsersAdapter(mlist, getContext(), sender_contact);
                binding.chatrecyclerview.setAdapter(adapter1);
                layoutManager1 = new LinearLayoutManager(getContext());
                binding.chatrecyclerview.setLayoutManager(layoutManager1);
                adapter1.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
//    to add search view in toolbar override the onCreateOptionMenu


    //    to add in fragment we need to first override onCreate method then we can override other two methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setQueryHint("Search User...");
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
//                   the method created ( userDefined )
                    searchUsers(newText.toString());

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
//                dont write here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }
}
