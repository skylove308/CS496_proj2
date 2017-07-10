package com.example.q.CS496_proj2;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
    public static ArrayList<String> facebookName = new ArrayList<>();
    public static ArrayList<String> facebookPicture = new ArrayList<>();

    public static String userName = "Taek";

    FacebookViewAdapter facebooklist;
    private static ListView listview;
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

        listview = (ListView) view.findViewById(R.id.listview22);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile"));
        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        final AccessToken accesstoken = AccessToken.getCurrentAccessToken();
        if (accesstoken != null) {

            GraphRequest graphRequest = GraphRequest.newMeRequest(accesstoken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        String userID = object.getString("id");
                        getFacebookContacts(userID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            graphRequest.executeAsync();

    }
        else{
                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String userID = object.getString("id");
                                    getFacebookContacts(userID);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

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
            }

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

                            String[] data = new String[2];

                            for (int i = 0; i < friendList.length(); i++) {
                                data[0] = friendList.getJSONObject(i).getString("name");
                                data[1] = friendList.getJSONObject(i).getJSONObject("picture").getJSONObject("data").getString("url");

                                facebookName.add(data[0]);
                                facebookPicture.add(data[1]);
                                Log.d("name", "*****************************" + data[0]);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new PostFacebookContacts().execute();
                    }
                }
        ).executeAsync();
    }


    public class PostFacebookContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... none) {
            return null;

        }

        protected void onPostExecute(Void none){

            facebooklist = new FacebookViewAdapter(facebookName, facebookPicture);
            listview.setAdapter(facebooklist);
        }
    }
}

