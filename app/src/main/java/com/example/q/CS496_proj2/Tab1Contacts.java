package com.example.q.CS496_proj2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.BoolRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class Tab1Contacts extends Fragment {

    /* ---------------- */
    /* MACROS & GLOBALS */
    /* ---------------- */

    /* macros */
    public final int ADD_CONTACT_ACTIVITY_CODE = 0;
    public final int EDIT_CONTACT_ACTIVITY_CODE = 1;
    final String server = "http://52.79.200.191:3000";

    /* global variables */
    private int currentIndex = -1;
    ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
    mBaseAdapter baseAdapter;
    View rootView;


    /* -------------- */
    /* MAIN FUNCTIONS */
    /* -------------- */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyAsyncTask newTask = new MyAsyncTask();
        newTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1contacts, container, false);

        ListView listview = (ListView) rootView.findViewById(R.id.contacts_listview);
        baseAdapter = new mBaseAdapter(getActivity(), contactList);
        listview.setAdapter(baseAdapter);
        listview.setOnItemClickListener(listviewListener);

        FloatingActionButton addButton = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        addButton.setOnClickListener(addButtonListener);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        String newName = data.getStringExtra("newName");
        String newNumber = data.getStringExtra("newNumber");
        contactList.add(createContact(newName, newNumber));

        if (requestCode == EDIT_CONTACT_ACTIVITY_CODE) contactList.remove(currentIndex);

        sortList();
        baseAdapter.notifyDataSetChanged();
    }

    public class MyAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        private AlertDialog dialog = new AlertDialog.Builder(getContext()).setMessage("Loading Contacts").create();
        private boolean exists;

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            while (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {}
            exists = validate();
            if (exists) {
                syncFromDB();
            } else {
                getContactsFromPhone();
                syncToDB();
            }
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPostExecute(Integer result) {
            ListView listview = (ListView) rootView.findViewById(R.id.contacts_listview);
            baseAdapter = new mBaseAdapter(getActivity(), contactList);
            listview.setAdapter(baseAdapter);
            listview.setOnItemClickListener(listviewListener);

            dialog.dismiss();
        }
    }

    public class mBaseAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, String>> list;

        public mBaseAdapter(Context c, ArrayList<HashMap<String, String>> array) {
            this.context = c;
            this.list = array;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            }

            ((TextView) v.findViewById(R.id.nameText)).setText(getItem(position).get("name"));
            ((TextView) v.findViewById(R.id.numberText)).setText(getItem(position).get("number"));

            return v;
        }
    }


    /* ---------------- */
    /* HELPER FUNCTIONS */
    /* ---------------- */

    public boolean validate() {
        boolean result = false;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(server + "/validate");
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
            Log.d("validate", Integer.toString(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                result = Boolean.parseBoolean(sb.toString());
            }
        } catch (Exception e) {
            Log.e("OTHER", Log.getStackTraceString(e));
        } finally {
            if (urlConnection == null) urlConnection.disconnect();
        }

        Log.d("result", Boolean.toString(result));

        return result;
    }

    public void syncToDB() {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(server + "/syncTo");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            String id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            JSONObject data = new JSONObject();
            JSONArray contacts = new JSONArray();
            data.put("id", id);

            for (int i = 0; i < contactList.size(); i++) {
                contacts.put(itemToJSON(contactList.get(i).get("name"), contactList.get(i).get("number")));
            }
            data.put("contacts", contacts);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(data));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d("syncToDB", Integer.toString(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
            }
        } catch (Exception e) {
            Log.e("OTHER", Log.getStackTraceString(e));
        } finally {
            if (urlConnection == null) urlConnection.disconnect();
        }
    }

    public JSONObject itemToJSON(String name, String number) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("number", number);

        return result;
    }

    public void syncFromDB() {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(server + "/syncFrom");
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
            Log.d("syncFromDB", Integer.toString(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();

                JSONObject item = new JSONObject(sb.toString());
                JSONArray contacts = (JSONArray) item.get("contacts");

                for (int i = 0; i < contacts.length(); i++) {
                    String name = (String) ((JSONObject) contacts.get(i)).get("name");
                    String number = (String) ((JSONObject) contacts.get(i)).get("number");
                    contactList.add(createContact(name, number));
                }
            }
        } catch (Exception e) {
            Log.e("OTHER", Log.getStackTraceString(e));
        } finally {
            if (urlConnection == null) urlConnection.disconnect();
        }
    }

    public void delete(HashMap pair) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(server + "/delete");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            String id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            JSONObject data = new JSONObject();
            data.put("id", id);
            data.put("name", pair.get("name"));
            data.put("number", pair.get("number"));

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(data));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d("delete", Integer.toString(responseCode));
        } catch (Exception e) {
            Log.e("OTHER", Log.getStackTraceString(e));
        } finally {
            if (urlConnection == null) urlConnection.disconnect();
        }
    }

    /* FUNCTION: reads phone contacts */
    public void getContactsFromPhone() {
        contactList.clear(); // called here since this function is called before getContactsFromJSON
        String phoneNumber = "";
        String contact_id;
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contact_id}, null
                        );
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            break; // only read first phone newNumber
                        }
                        phoneCursor.close();
                    }
                    contactList.add(createContact(name, phoneNumber));
                }

                cursor.close();
            }
        } catch (NullPointerException e) {
            Log.e("getContactsFromPhone", Log.getStackTraceString(e));
        }
    }

    /* FUNCTION: sort contactList */
    private void sortList() {
        Collections.sort(contactList, new Comparator<HashMap<String, String>>() {
            public int compare(HashMap<String, String> one, HashMap<String, String> two) {
                return one.get("name").compareTo(two.get("name"));
            }
        });
    }

    /* FUNCTION: create a contact instance given newName and newNumber */
    private HashMap<String, String> createContact(String name, String number) {
        HashMap<String, String> contactItem = new HashMap<String, String>();
        contactItem.put("name", name);
        contactItem.put("number", number);
        return contactItem;
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

    /* FUNCTION: format newNumber with dashes */
    private String formatNumber(String number) {
        if (number.length() == 11) {
            return number.substring(0, 3) + "-" + number.substring(3, 7) + "-" + number.substring(7, 11);
        } else {
            return number;
        }
    }


    /* --------- */
    /* LISTENERS */
    /* --------- */

    /* LISTENER: clicking listview element invokes SlidingUpPanelLayout */
    AdapterView.OnItemClickListener listviewListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
            currentIndex = position;
            Log.d("index", Integer.toString(contactList.size()));

            CharSequence options[] = new CharSequence[]{"Call Contact", "Edit Contact", "Delete Contact"};

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Options");
            alertDialogBuilder.setItems(options, dialogListener);
            alertDialogBuilder.show();
        }
    };

    AlertDialog.OnClickListener dialogListener = new AlertDialog.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int index) {
            switch (index) {
                case 0:
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    String number = contactList.get(currentIndex).get("newNumber");
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(callIntent);
                    break;
                case 1:
                    Intent intent = new Intent(getContext(), ContactActivity.class);
                    intent.putExtra("mode", EDIT_CONTACT_ACTIVITY_CODE);
                    intent.putExtra("oldName", contactList.get(currentIndex).get("name"));
                    intent.putExtra("oldNumber", contactList.get(currentIndex).get("number"));
                    startActivityForResult(intent, EDIT_CONTACT_ACTIVITY_CODE);
                    break;
                case 2:
                    final HashMap pair = contactList.get(currentIndex);
                    new Thread() {
                        @Override
                        public void run() {
                            delete(pair);
                        }
                    }.start();
                    contactList.remove(currentIndex);
                    baseAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    View.OnClickListener addButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), ContactActivity.class);
            intent.putExtra("mode", ADD_CONTACT_ACTIVITY_CODE);
            startActivityForResult(intent, ADD_CONTACT_ACTIVITY_CODE);
        }
    };
}
