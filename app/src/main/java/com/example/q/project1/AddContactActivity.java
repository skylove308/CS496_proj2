package com.example.q.project1;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddContactActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    FileOutputStream outFS = openFileOutput("contacts.json", Context.MODE_APPEND);
//                    String str = "hello world";
//                    outFS.write(str.getBytes());
//                    outFS.close();
//                    Toast.makeText(getApplicationContext(), "contacts.json created", Toast.LENGTH_SHORT).show();
//                } catch (IOException e) {
//                    // TODO
//                }
            }
        });

//        List<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();
//        ContactsSerializable cs = (ContactsSerializable) savedInstanceState.getSerializable("contacts");
//        JSONArray csArray = cs.showContacts();
//        try {
//            JSONObject jsonChildNode = csArray.getJSONObject(0);
//            String name = jsonChildNode.optString("name");
//            String number = jsonChildNode.optString("number");
//            EditText tmp = (EditText) findViewById(R.id.tmp);
//            tmp.setText(name);
//        } catch (JSONException e) {
//            // TODO
//        }

        Button cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    FileInputStream inFS = openFileInput("contacts.json");
//                    byte[] txt = new byte[5];
//                    inFS.read(txt);
//                    String str = new String(txt);
//                    EditText tmp = (EditText) findViewById(R.id.tmp);
//                    tmp.setText(str);
//                    inFS.close();
//                } catch (IOException e) {
//                    Toast.makeText(getApplicationContext(), "No contacts.json", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }
}
