package com.example.q.project1;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class ContactActivity extends AppCompatActivity {

    final String server = "http://52.79.200.191:3000";

    int mode;
    String oldName;
    String oldNumber;
    String newName = "";
    String newNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);

        if (mode == 1) {
            oldName = intent.getStringExtra("oldName");
            oldNumber = intent.getStringExtra("oldNumber");

            ((EditText) findViewById(R.id.nameEditText)).setText(oldName);
            ((EditText) findViewById(R.id.numberEditText)).setText(oldNumber);
            ((TextView) findViewById(R.id.titleTextView)).setText("EDIT CONTACT");
        } else {
            ((TextView) findViewById(R.id.titleTextView)).setText("ADD CONTACT");
        }

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(submit);
    }

    View.OnClickListener submit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
            EditText numberEditText = (EditText) findViewById(R.id.numberEditText);

            newName = nameEditText.getText().toString();
            newNumber = numberEditText.getText().toString();

            new Thread() {
                @Override
                public void run() {
                    syncToDB();
                }
            }.start();
            Intent intent = getIntent();
            intent.putExtra("newName", newName);
            intent.putExtra("newNumber", newNumber);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    public void syncToDB() {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(server + "/add");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            JSONObject data = new JSONObject();
            data.put("id", id);
            data.put("newName", newName);
            data.put("newNumber", newNumber);
            data.put("mode", mode);

            if (mode == 1) {
                data.put("oldName", oldName);
                data.put("oldNumber", oldNumber);
            }

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(data));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d("response code", Integer.toString(responseCode));

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
        result.put("newName", name);
        result.put("newNumber", number);

        return result;
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
}
