package com.example.q.project1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

    /* ---------------- */
    /* MACROS & GLOBALS */
    /* ---------------- */

    /* macros */
    public final int EDIT_CONTACT_ACTIVITY_CODE = 1;

    /* global variables */
    private int currentIndex = -1;
    List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
    SimpleAdapter simpleAdapter;


    /* -------------- */
    /* MAIN FUNCTIONS */
    /* -------------- */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1contacts, container, false);

        /* initialize contactList from contacts.json and from phone contacts */
        getContactsFromPhone();
        // getContactsFromJSON();

        /* sort list now that contactList is done */
        //sortList();

        /* listener for ListView - invokes SlidingUpPanelLayout */
        ListView listview = (ListView) rootView.findViewById(R.id.contacts_listview);
        listview.setOnItemClickListener(listviewListener);

        /* adapter for ListView */
        simpleAdapter = new SimpleAdapter(
                getActivity(), contactList, android.R.layout.simple_list_item_2,
                new String[]{"name", "number"}, new int[]{android.R.id.text1, android.R.id.text2}
        );
        listview.setAdapter(simpleAdapter);

        FloatingActionButton addButton = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        addButton.setOnClickListener(addButtonListener);

//        /* listener for call button */
//        Button callButton = (Button) rootView.findViewById(R.id.callButton);
//        callButton.setOnClickListener(callButtonListener);
//
//        /* listener for edit button */
//        Button editButton = (Button) rootView.findViewById(R.id.editButton);
//        editButton.setOnClickListener(editButtonListener);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* runs after returning from EditContactActivity */
        if (requestCode == EDIT_CONTACT_ACTIVITY_CODE) {
            getContactsFromPhone();
            simpleAdapter.notifyDataSetChanged();
        }
    }


    /* ---------------- */
    /* HELPER FUNCTIONS */
    /* ---------------- */

    /* FUNCTION: reads phone contacts */
    public void getContactsFromPhone() {
        contactList.clear(); // called here since this function is called before getContactsFromJSON
        String phoneNumber = null;
        String contact_id;
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

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
                        break; // only read first phone number
                    }
                    phoneCursor.close();
                }
                contactList.add(createContact(contact_id, name, phoneNumber));
            }
        }
        cursor.close();
    }

//    /* FUNCTION: Read contacts from contacts.json */
//    private void getContactsFromJSON() {
//        try {
//            JSONObject jsonResponse = new JSONObject(loadJSONFromAsset("contacts.json"));
//            JSONArray jsonMainNode = jsonResponse.optJSONArray("contacts");
//
//            for (int i = 0; i < jsonMainNode.length(); i++) {
//                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
//                String name = jsonChildNode.optString("name");
//                String number = jsonChildNode.optString("number");
//                contactList.add(createContact(name, number));
//            }
//        } catch (JSONException e) {
//        }
//    }

    private void writeContact() {
        String id = contactList.get(currentIndex).get("id");
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
        if (cursor.moveToFirst()) {
            long idContact = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            Intent intent = new Intent(Intent.ACTION_EDIT, ContactsContract.Contacts.CONTENT_URI);
            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idContact);
            intent.setData(contactUri);
            startActivityForResult(intent, EDIT_CONTACT_ACTIVITY_CODE);
        } else {
            Toast.makeText(getContext(), "Contact cannot be edited", Toast.LENGTH_LONG);
        }
    }

//    private void deleteContact() {
//        String id = contactList.get(currentIndex).get("id");
//        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        ContentResolver contentResolver = getContext().getContentResolver();
//        Cursor cursor = contentResolver.query(uri, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
//        if (cursor.moveToFirst()) {
//            long idContact = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//            Intent intent = new Intent(Intent.ACTION_DELETE, ContactsContract.Contacts.CONTENT_URI);
//            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idContact);
//            intent.setData(contactUri);
//            startActivity(intent);
//        } else {
//            Toast.makeText(getContext(), "Contact cannot be deleted", Toast.LENGTH_LONG);
//        }
//    }

    /* FUNCTION: sort contactList */
    private void sortList() {
        Collections.sort(contactList, new Comparator<HashMap<String, String>>() {
            public int compare(HashMap<String, String> one, HashMap<String, String> two) {
                return one.get("name").compareTo(two.get("name"));
            }
        });
    }

    /* FUNCTION: convert JSON file content to string */
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

    /* FUNCTION: create a contact instance given name and number */
    private HashMap<String, String> createContact(String id, String name, String number) {
        HashMap<String, String> contactItem = new HashMap<String, String>();
        contactItem.put("id", id);
        contactItem.put("name", name);
        contactItem.put("number", number);
        return contactItem;
    }

    /* FUNCTION: format name such that last name comes first */
    private String formatName(String name) {
        String[] tokens = name.split("[ ]+");
        String result = tokens[1] + ", " + tokens[0];
        return result;
    }

    /* FUNCTION: format number with dashes */
    private String formatNumber(String number) {
        if (number.length() == 11) {
            String result = number.substring(0, 3) + "-" + number.substring(3, 7) + "-" + number.substring(7, 11);
            return result;
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

            CharSequence options[] = new CharSequence[]{"Call Contact", "Edit Contact"};

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
                    String number = contactList.get(currentIndex).get("number");
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(callIntent);
                    break;
                case 1:
                    writeContact();
                    break;
//                case 2:
//                    deleteContact();
//                    break;
            }
        }
    };

    View.OnClickListener addButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent("android.intent.action.INSERT", ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, EDIT_CONTACT_ACTIVITY_CODE);
        }
    };

    /* LISTENER: clicking call button invokes a phone call */
    View.OnClickListener callButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            String number = contactList.get(currentIndex).get("number");
            callIntent.setData(Uri.parse("tel:" + number));
            startActivityForResult(callIntent, EDIT_CONTACT_ACTIVITY_CODE);
        }
    };

    /* LISTENER: clicking edit button invokes a EditContactActivity */
    View.OnClickListener editButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent ni = new Intent(getContext(), EditContactActivity.class);
            ni.putExtra("index", currentIndex);
            ni.putExtra("name", contactList.get(currentIndex).get("name"));
            ni.putExtra("number", contactList.get(currentIndex).get("number"));
            startActivityForResult(ni, EDIT_CONTACT_ACTIVITY_CODE);
        }
    };
}
