package com.exampl.wechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.exampl.weChat.databinding.ActivityViewImageBinding;
import com.exampl.weChat.R;

public class ViewImage extends AppCompatActivity {
    ActivityViewImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        getting intent from ChatAdapter to get the ImageUrl and loading it to image
        String ImageUrlReceived = getIntent().getStringExtra("url_image");
//        Picasso.get().load(ImageUrlReceived).placeholder(R.drawable.image_placeholder1).into(binding.viewImage);
        Glide.with(ViewImage.this).load(ImageUrlReceived).placeholder(R.drawable.image_placeholder1).into(binding.viewImage);

        //    to dismiss the image after clicking
//    use setonCLick Listener on the root Layout and set Finish();
        binding.contraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}