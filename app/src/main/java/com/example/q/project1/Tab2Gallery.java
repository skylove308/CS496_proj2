package com.example.q.project1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Field;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.q.project1.R.id.galleryGridView;

public class Tab2Gallery extends Fragment {

    Button btnLoadImg, btnAddImg, btnDelImg;
    ImageView tempView;
    SeekBar seekBar;
    TextView seekText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2gallery, container, false);

        btnLoadImg = rootView.findViewById(R.id.btnLoadImg);
        btnAddImg = rootView.findViewById(R.id.btnAddImg);
        btnDelImg = rootView.findViewById(R.id.btnDelImg);
        final GridView gv = (GridView) rootView.findViewById(galleryGridView);
        GalleryGridAdapter gAdapter = new GalleryGridAdapter(getContext());
        gv.setAdapter(gAdapter);
        tempView = rootView.findViewById(R.id.gall_img_temp_view);
        seekBar = rootView.findViewById(R.id.gall_seekbar);
        seekText = rootView.findViewById(R.id.gall_seekcnt);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);


        btnLoadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

        btnDelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempView.setImageBitmap(null);
                btnAddImg.setVisibility(View.GONE);
                btnDelImg.setVisibility(View.GONE);
                tempView.setVisibility(View.GONE);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Integer seek_cnt = seekBar.getProgress();
                String seek_text = String.valueOf(seek_cnt) + " in a row";
                seekText.setText(seek_text);
                gv.setNumColumns(seek_cnt);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            cursor.close();
            tempView.setImageBitmap(bitmap);
            btnAddImg.setVisibility(View.VISIBLE);
            btnDelImg.setVisibility(View.VISIBLE);
            tempView.setVisibility(View.VISIBLE);
        }
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


}
