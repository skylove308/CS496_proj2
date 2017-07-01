package com.example.q.project1;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Field;

import static android.app.Activity.RESULT_OK;

public class Tab2Gallery extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2gallery, container, false);

        Button btnNewImg = rootView.findViewById(R.id.btnNewImg);
        Button btnDelImg = rootView.findViewById(R.id.btnDelImg);
        final GridView gv = (GridView) rootView.findViewById(R.id.galleryGridView);
        GalleryGridAdapter gAdapter = new GalleryGridAdapter(getContext());
        gv.setAdapter(gAdapter);

        btnNewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
            }
        });

        btnDelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer imgNum = 0;
                Field[] fields = R.drawable.class.getFields();
                for (Field field : fields) {
                    imgNum++;
                }

                Toast.makeText(getActivity(), String.valueOf(imgNum), Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    public class GalleryGridAdapter extends BaseAdapter {
        Context context;

        public GalleryGridAdapter(Context c) {
            context = c;
        }

        public int getCount() {
            return pictureID.length;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        Integer[] pictureID = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5, R.drawable.img6, R.drawable.img7, R.drawable.img8, R.drawable.img9, R.drawable.img10};

//        public View getView(int position, View convertView, ViewGroup parent) {
//            ImageView imageview = new ImageView(context);
//            imageview.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 500));
//            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageview.setPadding(10, 10, 10, 10);
//
//            imageview.setImageResource(pictureID[position]);
//            return imageview;
//        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linear = new LinearLayout(context);
            linear.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 500));
            linear.setPadding(10, 10, 10, 10);

            ImageView imageview = new ImageView(context);
            imageview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageview.setImageResource(pictureID[position]);

            linear.addView(imageview);
            return linear;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(getContext(), "ByeByeBye", Toast.LENGTH_LONG).show();
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
//                currImageURI = data.getData();
                Toast.makeText(getContext(), data.getData().toString(), Toast.LENGTH_LONG).show();
            }

        }
    }
}
