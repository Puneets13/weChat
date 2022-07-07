package com.exampl.wechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.exampl.weChat.R;
import com.exampl.weChat.databinding.ActivityMainBinding;
import com.exampl.wechat.Adapters.FragmentAdapter;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
FragmentAdapter fragmentAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(fragmentAdapter);
//        binding.viewpager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tablayout.setupWithViewPager(binding.viewpager);


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

    //    for showing current status to others
//    to show online when mainActivity Opens
    @Override
    protected void onResume() {

        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("online");
        super.onResume();
    }

    //    when the activity closes then the offline status should be displayed for that use onPausemethodd
//    use it in if condition ...that if it is not null then show offline

    //it says that when mainActivity is onPause then do this
    @Override
    protected void onPause() {
        String currentId = FirebaseAuth.getInstance().getUid();
        if(auth.getCurrentUser()!= null) {
            database.getReference().child("presence").child(currentId).setValue("offline");
        }
        super.onPause();

    }


    //    to add menu to the mainActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

//        MenuItem menuItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setQueryHint("Type here to Search");
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
////           fragmentAdapter.getfilter.filter(newText);
//
//                return false;
//            }
//        });

        return super.onCreateOptionsMenu(menu);
    }

    //to perofrmon some action when the item is being clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, settingsActivity.class);
                startActivity(intent);
                break;

            case R.id.logout:
                database.getReference().child("presence").child(FirebaseAuth.getInstance().getUid()).setValue("offline");
                auth.signOut();
//                after logging out where do the user moves for that make intent
                Intent intent2 = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent2);
                break;
            case R.id.chatRoom:
                Intent intent1 = new Intent(MainActivity.this, GroupChatActivity.class);
                startActivity(intent1);
                break;

        }


        return super.onOptionsItemSelected(item);
    }



}