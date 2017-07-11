package com.example.q.CS496_proj2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        Bitmap bitmap = StringToBitMap(intent.getStringExtra("bitmap"));

        ImageView bitmapImageView = (ImageView) findViewById(R.id.bitmapImageView);
        PhotoViewAttacher photoAttacher = new PhotoViewAttacher(bitmapImageView);
        photoAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);

        bitmapImageView.setImageBitmap(bitmap);
    }


    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }


}
