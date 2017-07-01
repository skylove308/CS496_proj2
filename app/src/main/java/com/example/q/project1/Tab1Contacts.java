package com.example.q.project1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Tab1Contacts extends Fragment {

    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private int currentIndex = -1;
    private SlidingUpPanelLayout mLayout;
    List<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1contacts, container, false);

        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        mLayout.setTouchEnabled(false);

        askForContactPermission();
        initList();
        sortList();
        ListView listview = (ListView) rootView.findViewById(R.id.contacts_listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                int height = mLayout.getPanelHeight();
                if (currentIndex != position || height == 0) {
                    mLayout.setPanelHeight(130);
                    currentIndex = position;
                } else {
                    mLayout.setPanelHeight(0);
                }
            }
        });

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getActivity(), contactList, android.R.layout.simple_list_item_2,
                new String[] {"name", "number"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );
        listview.setAdapter(simpleAdapter);

//        FloatingActionButton fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
//        fab_add.setOnClickListener(add_listener);

//        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        AddContactActivity fr = new AddContactActivity();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("contacts", cs);
//        fr.setArguments(bundle);
//        ft.replace(android.R.id.content, fr);
//        ft.addToBackStack(null);
//        ft.commit();
        return rootView;
    }

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS
                );
            } else {
                getContacts();
            }
        } else{
            getContacts();
        }
    }

    public void getContacts() {
        /* Called here instead of initList because this comes first */
        contactList.clear();

        String phoneNumber = null;
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null,null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex( ContactsContract.Contacts._ID ));
                String name = cursor.getString(cursor.getColumnIndex( ContactsContract.Contacts.DISPLAY_NAME ));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER )));
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { contact_id }, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        break;
                    }
                    phoneCursor.close();
                }
                contactList.add(createContact(name, phoneNumber));
            }
        }

        cursor.close();
    }


    View.OnClickListener add_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent ni = new Intent(getContext(), AddContactActivity.class);
            startActivity(ni);
        }
    };

    private void initList() {
        try {
            JSONObject jsonResponse = new JSONObject(loadJSONFromAsset("contacts.json"));
            JSONArray jsonMainNode = jsonResponse.optJSONArray("contacts");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String name = jsonChildNode.optString("name");
                String number = jsonChildNode.optString("number");
                contactList.add(createContact(name, number));
            }
        } catch (JSONException e) {
            // TODO
        }
    }

    private void sortList() {
        Collections.sort(contactList, new Comparator<HashMap<String, String>>() {
            public int compare(HashMap<String, String> one, HashMap<String, String> two) {
                return one.get("name").compareTo(two.get("name"));
            }
        });
    }

    public String loadJSONFromAsset(String source) {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open(source);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private HashMap<String, String> createContact(String name, String number) {
        HashMap<String, String> contactItem = new HashMap<String, String>();
        String formattedName = convertName(name);
        String formattedNumber = convertNumber(number);
        contactItem.put("name", formattedName);
        contactItem.put("number", formattedNumber);
        return contactItem;
    }

    private String convertName(String name) {
        String[] tokens = name.split("[ ]+");
        String result = tokens[1] + ", " + tokens[0];
        return result;
    }

    private String convertNumber(String number) {
        if (number.length() == 11) {
            String result = number.substring(0, 3) + "-" + number.substring(3, 7) + "-" + number.substring(7, 11);
            return result;
        } else {
            return number;
        }
    }
}
