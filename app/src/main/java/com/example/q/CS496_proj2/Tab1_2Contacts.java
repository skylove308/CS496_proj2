package com.example.q.CS496_proj2;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab1_2Contacts extends Fragment {

    private CallbackManager callbackManager;

    public static int contactCount = 1;
    public static ArrayList<String[]> facebookContacts = new ArrayList<>();
    public static ArrayList<String[]> phoneContacts = new ArrayList<>();

    public static String userName = "Anonymous";


    List<HashMap<String, String>> contactList2 = new ArrayList<HashMap<String, String>>();
    SimpleAdapter simpleAdapter2;

    private static boolean firstView = true;
    private static ListView listView;

    private Context context;


    public Tab1_2Contacts() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab1_2_contacts, container, false);

        ListView listview = (ListView) view.findViewById(R.id.listview2);

        simpleAdapter2 = new SimpleAdapter(
                getActivity(), contactList2, android.R.layout.simple_list_item_2,
                new String[]{"name", "number"}, new int[]{android.R.id.text1, android.R.id.text2}
        );
        listview.setAdapter(simpleAdapter2);


        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result", "************************************** " + object.toString());
                        try {
                            String userID = object.getString("id");
                            userName = object.getString("name");
                            Log.d("USERID", "*********************************** user ID : " + userID);
                            getFacebookContacts(userID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getFacebookContacts(String userID) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userID + "/taggable_friends?limit=50",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("RESPONSE", "********************************************** " + response.toString());
                        try {
                            JSONArray friendList = response.getJSONObject().getJSONArray("data");
                            String[] data;
                            for (int i = 0; i < friendList.length(); i++) {
                                data = new String[3];
                                data[0] = Integer.toString(contactCount);
                                contactCount += 1;
                                data[1] = friendList.getJSONObject(i).getString("name");
                                data[2] = "facebook";
                                facebookContacts.add(data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new PostFacebookContacts().execute();
                    }
                }
        ).executeAsync();
    }

    public void sendHttpWithContact(String url, String json) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            //Log.d("RESPONSE", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String contactStringArrayToJSON(String[] stringArray) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", stringArray[1]);
            jsonObject.put("phoneNumber", stringArray[2]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JSON", jsonObject.toString());
        return jsonObject.toString();
    }

    public class PostFacebookContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... none) {
            for (String[] FBcontact : facebookContacts) {
                sendHttpWithContact("http://52.78.101.202:3000/api/contacts", contactStringArrayToJSON(FBcontact));
            }
            return null;
        }

        protected void onPostExecute(Void none){

            simpleAdapter2 = new SimpleAdapter(context, facebookContacts);
            listView.setAdapter(simpleAdapter2);
        }
    }
}
}
