package com.example.q.CS496_proj2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import static android.app.Activity.RESULT_OK;
import static com.example.q.CS496_proj2.R.id.galleryGridView;

public class Tab2Gallery extends Fragment {

    final String server = "http://52.79.200.191:3000";

    View rootView;
    GridView gv;
    GalleryGridAdapter gAdapter;
    LinearLayout XImg;
    FloatingActionButton FABAddImg;
    SeekBar seekBar;
    TextView seekText;

    ArrayList<HashMap<String, Object>> bitmaps = new ArrayList<HashMap<String, Object>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncAsyncTask newTask = new syncAsyncTask();
        newTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab2gallery, container, false);

        XImg = (LinearLayout) rootView.findViewById(R.id.XImg);

        FABAddImg = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        gv = (GridView) rootView.findViewById(galleryGridView);
        gAdapter = new GalleryGridAdapter(getContext());
        gv.setAdapter(gAdapter);
        seekBar = (SeekBar) rootView.findViewById(R.id.gall_seekbar);
        seekText = (TextView) rootView.findViewById(R.id.gall_seekcnt);


        FABAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Integer seek_cnt = seekBar.getProgress() + 1;
                String seek_text = String.valueOf(seek_cnt) + " in a row";
                seekText.setText(seek_text);
                gv.setNumColumns(seek_cnt);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            Toast.makeText(getContext(), "Added Image to Gallery", Toast.LENGTH_SHORT).show();

            Uri uri = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(uri, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            cursor.close();

            final Bitmap tmp_image = BitmapFactory.decodeFile(imagePath);
            final String photo_id = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
            final Bitmap image = Bitmap.createScaledBitmap(tmp_image, 300, 300, false);
            bitmaps.add(createObject(image, photo_id));
            gAdapter.notifyDataSetChanged();

            if (bitmaps.size() == 0) {
                XImg.setVisibility(View.VISIBLE);
            } else {
                XImg.setVisibility(View.GONE);
            }

            new AsyncTask<Integer, String, String>() {
                @Override
                protected String doInBackground(Integer... integers) {
                    add(image, photo_id);
                    return "";
                }

//                @Override
//                protected void onPostExecute(String result) {
//                    ImageView test = getActivity().findViewById(R.id.testImageView);
//                    test.setImageBitmap(StringToBitMap(result));
//                }
            }.execute();

        }
    }

    public HashMap<String, Object> createObject (Bitmap bitmap, String photo_id) {
        HashMap<String, Object> object = new HashMap<>();
        object.put("bitmap", bitmap);
        object.put("photo_id", photo_id);
        return object;
    }

    public class syncAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        private android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(getContext()).setMessage("Loading Contacts").create();

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            sync();
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPostExecute(Integer result) {
            gv = (GridView) rootView.findViewById(galleryGridView);
            gAdapter = new GalleryGridAdapter(getContext());
            gv.setAdapter(gAdapter);

            if (bitmaps.size() == 0) {
                XImg.setVisibility(View.VISIBLE);
            } else {
                XImg.setVisibility(View.GONE);
            }

            dialog.dismiss();
        }
    }

    public void sync() {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(server + "/syncGallery");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            String id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            JSONObject data = new JSONObject();
            data.put("id", id);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(data));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d("sync photo", Integer.toString(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                Log.d("asdf", sb.toString());
                JSONArray photos = new JSONArray(sb.toString());

                for (int i = 0; i < photos.length(); i++) {
                    Bitmap bitmap = StringToBitMap((String) ((JSONObject) photos.get(i)).get("bitmap"));
                    String photo_id = (String) ((JSONObject) photos.get(i)).get("photo_id");
                    bitmaps.add(createObject(bitmap, photo_id));
                }
            }
        } catch (Exception e) {
            Log.e("OTHER", Log.getStackTraceString(e));
        } finally {
            if (urlConnection == null) urlConnection.disconnect();
        }
    }

    public void add(Bitmap bitmap, String photo_id) {
        Log.d("starting add", "starting add");
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(server + "/addGallery");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            String id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            JSONObject data = new JSONObject();
            data.put("id", id);
            data.put("photo_id", photo_id);
            data.put("bitmap", BitMapToString(bitmap));

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(data));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d("add photo", Integer.toString(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                Log.d("asdf", sb.toString());
            }
        } catch (Exception e) {
            Log.e("OTHER", Log.getStackTraceString(e));
        } finally {
            if (urlConnection == null) urlConnection.disconnect();
        }
    }

    public void delete(Bitmap bitmap, String photo_id) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(server + "/deleteGallery");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            String id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            JSONObject data = new JSONObject();
            data.put("id", id);
            data.put("bitmap", BitMapToString(bitmap));
            data.put("photo_id", photo_id);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(data));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d("delete photo", Integer.toString(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                Log.d("asdf", sb.toString());
            }
        } catch (Exception e) {
            Log.e("OTHER", Log.getStackTraceString(e));
        } finally {
            if (urlConnection == null) urlConnection.disconnect();
        }
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

    public String getPostDataString(JSONObject params) throws Exception {
        Iterator<String> itr = params.keys();
        StringBuilder result = new StringBuilder();
        boolean first = true;

        while (itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }

        return result.toString();
    }

    //delete
    private String getInternalPath() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getContext().getApplicationContext().getPackageName()
                + "/Files/tabB");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file newName
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        String mImageName = "ChanRong_" + timeStamp + ".jpg";
        return mediaStorageDir.getPath() + File.separator + mImageName;
    }
    //delete
    /* Create a File for saving an image or video */
    private File getOutputMediaFile(String path) {
        if (path == null) {
            return null;
        }
        File mediaFile;
        mediaFile = new File(path);
        return mediaFile;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public String BitMapToShort(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    public class GalleryGridAdapter extends BaseAdapter {
        Context context;

        public GalleryGridAdapter(Context c) {
            context = c;
        }

        public int getCount() {
            return bitmaps.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LinearLayout linear = new LinearLayout(context);
            linear.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 500));
            linear.setPadding(10, 10, 10, 10);

            ImageView imageview = new ImageView(context);
            imageview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final Bitmap bitmap = (Bitmap) bitmaps.get(position).get("bitmap");

            imageview.setImageBitmap(bitmap);

            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String[] actions = new String[]{"Show Image", "Delete Image"};
                    AlertDialog.Builder selectAct = new AlertDialog.Builder(getContext());
                    selectAct.setTitle("Select Action");
                    selectAct.setNegativeButton("Cancel", null);
                    selectAct.setItems(actions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                Intent showIntent = new Intent(getContext(), DisplayActivity.class);
                                showIntent.putExtra("bitmap", BitMapToString((Bitmap) bitmaps.get(position).get("bitmap")));
                                startActivity(showIntent);
                            } else {
                                final Bitmap bm = (Bitmap) bitmaps.get(position).get("bitmap");
                                final String s = (String) bitmaps.get(position).get("photo_id");
                                bitmaps.remove(position);
                                if (bitmaps.size() == 0) {
                                    XImg.setVisibility(View.VISIBLE);
                                } else {
                                    XImg.setVisibility(View.GONE);
                                }

                                Toast.makeText(getContext(), "Deleted Image From Gallery", Toast.LENGTH_SHORT).show();
                                gAdapter.notifyDataSetChanged();
                                new AsyncTask<Integer, String, String>() {
                                    @Override
                                    protected String doInBackground(Integer... integers) {
                                        delete(bm, s);
                                        return "";
                                    }
                                }.execute();
                            }
                        }
                    });
                    selectAct.show();
                }
            });

            linear.addView(imageview);
            return linear;
        }
    }
}
