//package com.example.chatingapp.Adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import Users;
//import com.example.chatingapp.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.ArrayList;
//
//public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
//    ArrayList<Users> list;
//    Context context;
//
//    TextView dialog_username, dialog_contact;
//    ImageView dialog_phncall, dialog_fav, dialog_chat;
//
//    public FavouriteAdapter(ArrayList<Users> list, Context context) {
//        this.list = list;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_users, parent, false);
//
//        return new FavouriteAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView image;
//        TextView userName, lastmessage, lastmsgtime;
//        ImageView favorite_btn ;
//        DatabaseReference favouriteref;
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            image = itemView.findViewById(R.id.profile_image);
//            userName = itemView.findViewById(R.id.txtusername);
//            lastmessage = itemView.findViewById(R.id.txtlastmessage);
//            lastmsgtime = itemView.findViewById(R.id.txtlastTime);
//            favorite_btn=itemView.findViewById(R.id.add_favourite);
//
//
//        }
//
//
//    }
//}
