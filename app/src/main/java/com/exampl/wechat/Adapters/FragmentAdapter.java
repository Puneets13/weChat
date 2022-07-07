package com.exampl.wechat.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.exampl.wechat.Fragments.CallFragment;
import com.exampl.wechat.Fragments.ChatFragment;
import com.exampl.wechat.Fragments.StatusFragment;

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
//  TO GET THE ITEM ACCORDING TO THERI POSITION SO USING SWITCH CASE
    switch (position){
        case 0: return new ChatFragment();
        case 1:return new StatusFragment();
        case 2:return new CallFragment();
        default: return new ChatFragment();

    }
    }

    @Override
    public int getCount() {
        return 3;
    }

//TO GET THE TITLE TO THE TABS ACCORDING TO THERE POSITION
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null ;
        if(position==0){
            title="CHATS";
        }
        if(position==1){
            title="STATUS";
        } if(position==2){
            title="CALLS";
        }
        return title;
    }
}
