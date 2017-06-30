package com.example.q.project1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.ListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Tab1Contacts extends Fragment {
    List<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1contacts, container, false);

        ListView listview = (ListView) rootView.findViewById(R.id.contacts_listview);
        initList();
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getActivity(),
                contactList,
                android.R.layout.simple_list_item_1,
                new String[] {"contact"},
                new int[] {android.R.id.text1});
        listview.setAdapter(simpleAdapter);
        return rootView;
    }

    private void initList() {
        try {
            JSONObject jsonResponse = new JSONObject(loadJSONFromAsset("contacts.json"));
            JSONArray jsonMainNode = jsonResponse.optJSONArray("contacts");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String name = jsonChildNode.optString("name");
                String number = jsonChildNode.optString("email");
                contactList.add(createContact("contact", name + " " + number));
            }
        } catch (JSONException e) {

        }
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

    private HashMap<String, String> createContact(String name,String number) {
        HashMap<String, String> employeeNameNo = new HashMap<String, String>();
        employeeNameNo.put(name, number);
        return employeeNameNo;
    }
}
