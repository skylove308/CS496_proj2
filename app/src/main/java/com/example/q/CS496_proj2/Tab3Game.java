package com.example.q.CS496_proj2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Tab3Game extends Fragment {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final String server = "http://52.79.200.191:3000";
    final private int duration = 500;
    private int currentIndex = 0;
    private int currentStage = 0;

    private TextView stageTextView;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    private List<Integer> answers = new ArrayList<Integer>();
    private ArrayList<String[]> scores = new ArrayList<String[]>();
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private JSONObject userData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3game, container, false);

        GridView gv = (GridView) rootView.findViewById(R.id.gameGridView);
        GameGridViewAdapter gAdapter = new GameGridViewAdapter(getContext());
        gv.setAdapter(gAdapter);

        stageTextView = (TextView) rootView.findViewById(R.id.stageTextView);

        /* listener for start button */
        Button startButton = (Button) rootView.findViewById(R.id.startButton);
        Button showButton = (Button) rootView.findViewById(R.id.showButton);

        startButton.setOnClickListener(startButtonListener);
        showButton.setOnClickListener(showButtonListener);

        loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile"));

        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            userData = object;
                            Log.d("Already Logged In", response.toString());
                        }
                    });
            request.executeAsync();
        }

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                userData = object;
                                Log.d("LoginActivity", response.toString());
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });

        loadRank();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public class GameGridViewAdapter extends BaseAdapter {
        Context context;
        Integer[] colors = {Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE};

        public GameGridViewAdapter(Context c) {
            context = c;
        }

        @Override
        public int getCount() {
            return colors.length;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LinearLayout linear = new LinearLayout(context);
            linear.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 500));
            linear.setPadding(32, 32, 32, 32);

            final Button button = new Button(context);

            switch (position) {
                case 0:
                    button.setBackgroundResource(R.drawable.game_shape_1);
                    button1 = button;
                    button1.setId(R.id.button1);
                    button1.setEnabled(false);
                    break;
                case 1:
                    button.setBackgroundResource(R.drawable.game_shape_2);
                    button2 = button;
                    button2.setId(R.id.button2);
                    button2.setEnabled(false);
                    break;
                case 2:
                    button.setBackgroundResource(R.drawable.game_shape_3);
                    button3 = button;
                    button3.setId(R.id.button3);
                    button3.setEnabled(false);
                    break;
                case 3:
                    button.setBackgroundResource(R.drawable.game_shape_4);
                    button4 = button;
                    button4.setId(R.id.button4);
                    button1.setEnabled(false);
                    break;
            }

            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            linear.addView(button);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (button1.isEnabled()) {
                        checkAnswer(position);
                    }
                }
            });

            return linear;
        }
    }

    private void simulateOne(int buttonNumber, int order, final boolean last) {
        final Handler h1 = new Handler();
        final Handler h2 = new Handler();
        Runnable r1;
        Runnable r2;

        switch (buttonNumber) {
            case 0:
                r1 = new Runnable() {
                    @Override
                    public void run() {
                        button1.setPressed(true);
                    }
                };

                r2 = new Runnable() {
                    @Override
                    public void run() {
                        button1.setPressed(false);
                        if (last) {
                            turnOn();
                        }
                    }
                };
                break;
            case 1:
                r1 = new Runnable() {
                    @Override
                    public void run() {
                        button2.setPressed(true);
                    }
                };

                r2 = new Runnable() {
                    @Override
                    public void run() {
                        button2.setPressed(false);
                        if (last) {
                            turnOn();
                        }
                    }
                };
                break;
            case 2:
                r1 = new Runnable() {
                    @Override
                    public void run() {
                        button3.setPressed(true);
                    }
                };

                r2 = new Runnable() {
                    @Override
                    public void run() {
                        button3.setPressed(false);
                        if (last) {
                            turnOn();
                        }
                    }
                };
                break;
            case 3:
                r1 = new Runnable() {
                    @Override
                    public void run() {
                        button4.setPressed(true);
                    }
                };

                r2 = new Runnable() {
                    @Override
                    public void run() {
                        button4.setPressed(false);
                        if (last) {
                            turnOn();
                        }
                    }
                };
                break;
            default:
                r1 = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "SOMETHING WENT WRONG in simulate()", Toast.LENGTH_LONG).show();
                    }
                };
                r2 = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "SOMETHING WENT WRONG in simulate()", Toast.LENGTH_LONG).show();
                    }
                };

                Log.e("simulate", "SOMETHING WENT WRONG");
                break;
        }

        h1.postDelayed(r1, order * duration + 100);
        h2.postDelayed(r2, (order + 1) * duration);
    }

    private void turnOff() {
        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
    }

    private void turnOn() {
        button1.setEnabled(true);
        button2.setEnabled(true);
        button3.setEnabled(true);
        button4.setEnabled(true);
    }

    private void simulateAll() {
        turnOff();

        for (int i = 0; i < answers.size(); i++) {
            if (i < answers.size() - 1) {
                simulateOne(answers.get(i), i, false);
            } else {
                simulateOne(answers.get(i), i, true);
            }
        }
    }

    private void initGame() {
        answers.clear();
        currentIndex = 0;
        currentStage = 0;
        turnOff();
        TextView stageTextView = (TextView) getActivity().findViewById(R.id.stageTextView);
        Button startButton = (Button) getActivity().findViewById(R.id.startButton);
        Button showButton = (Button) getActivity().findViewById(R.id.showButton);
//        Button resetButton = getActivity().findViewById(R.id.readtxt);
        stageTextView.setText("SIMON!");
        startButton.setVisibility(View.VISIBLE);
        showButton.setVisibility(View.VISIBLE);

//        resetButton.setVisibility(View.VISIBLE);

    }

    private void startGame() {
        Button startButton = (Button) getActivity().findViewById(R.id.startButton);
        Button showButton = (Button) getActivity().findViewById(R.id.showButton);
//        Button resetButton = getActivity().findViewById(R.id.readtxt);
        startButton.setVisibility(View.GONE);
        showButton.setVisibility(View.GONE);
//        resetButton.setVisibility(View.GONE);
        stageTextView.setText("STAGE " + Integer.toString(currentStage));

        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            answers.add(random.nextInt(4));
        }
    }

    private void checkAnswer(int number) {
        if (number != answers.get(currentIndex)) {
            restartGame();
            return;
        }

        currentIndex++;

        if (currentIndex >= answers.size()) {
            nextStage();
        }
    }

    private int nextStage() {
        currentIndex = 0;
        currentStage++;

        Random random = new Random();
        int newNumber = random.nextInt(4);

        answers.add(newNumber);
        stageTextView.setText("Stage " + Integer.toString(currentStage));
        simulateAll();
        return newNumber;
    }

    private void restartGame() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final Integer stageResult = currentStage;

        AddAsyncTask add = new AddAsyncTask(stageResult);
        add.execute();

        String message = (accessToken == null) ? "You must log in to save your score" : "Score uploaded!";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Stage " + Integer.toString(stageResult) + ": Game Over!");
        alertDialogBuilder.setMessage(message);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        initGame();
    }

    View.OnClickListener startButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            turnOff();
            initGame();
            startGame();
            nextStage();
        }
    };

    View.OnClickListener showButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sortList();
            showRank();
        }
    };

    public class AddAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        OkHttpClient httpClient = new OkHttpClient();
        int score;

        public AddAsyncTask(int s) {
            this.score = s;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            try {

                Log.d("doinbackground", (String) userData.get("id") + userData.get("name") + Integer.toString(score));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", userData.get("id"));
                jsonObject.put("name", userData.get("name"));
                jsonObject.put("score", score);
                String data = jsonObject.toString();

                RequestBody requestBody = RequestBody.create(JSON, data);

                Request request = new Request.Builder()
                        .url(server + "/syncToGame")
                        .post(requestBody)
                        .build();

                Response response = httpClient.newCall(request).execute();
                Log.d("RANKASYNC", response.body().string());
            } catch (Exception e) {
                Log.e("rankAsyncTask", e.toString());
            }
            return 0;
        }
    }

    private void loadRank() {
//        RankAsyncTask rat = new RankAsyncTask();
//        rat.execute();


//        try {
//            final FileInputStream file = getContext().openFileInput("scores.txt");
//            byte[] txt = new byte[1000];
//            file.read(txt);
//            String str = new String(txt);
//            String[] tokens = str.split("\r\n");
//
//            scores.clear();
//
//            for (int i = 0; i < tokens.length; i++) {
//                if (tokens[i].contains("!v!r!")) {
//                    String[] tmpStr = tokens[i].split("!v!r!");
//                    scores.add(tmpStr);
//                }
//            }
//
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }

    }

    private void sortList() {
        Collections.sort(scores, new Comparator<String[]>() {
            public int compare(String[] two, String[] one) {
                return one[1].compareTo(two[1]);
            }
        });
    }

    private void showRank() {
        final View rankView = View.inflate(getContext(), R.layout.tab3rank, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("High Scores");
        alertDialogBuilder.setView(rankView);

        ListView scoreListView = (ListView) rankView.findViewById(R.id.rankListView);
        if (scores.size() == 0) {
            loadRank();
        }
        mAdapter adapter = new mAdapter(getContext(), scores);
        adapter.notifyDataSetChanged();
        scoreListView.setAdapter(adapter);

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public class mAdapter extends ArrayAdapter<String[]> {

        public mAdapter(Context c, ArrayList<String[]> r) {
            super(c, 0, r);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String[] rowContents = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.tab3element, parent, false);
            }

            TextView rankTextView = (TextView) convertView.findViewById(R.id.rankTextView);
            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameText);
            TextView scoreTextView = (TextView) convertView.findViewById(R.id.scoreTextView);

            rankTextView.setText(Integer.toString(position + 1));
            nameTextView.setText(rowContents[0]);
            scoreTextView.setText(rowContents[1]);

            return convertView;
        }
    }


    private void addRank(String userName, Integer currentStage) {
        String[] currentScore = new String[2];
        currentScore[0] = userName;
        if (currentScore[0].equals("")) {
            currentScore[0] = "Anonymous";
        }
        currentScore[1] = String.valueOf(currentStage);
        scores.add(currentScore);
        String newLine = currentScore[0] + "!v!r!" + currentScore[1] + "\r\n";

        try {

            FileOutputStream file = getContext().openFileOutput("scores.txt", Context.MODE_APPEND);
            file.write(newLine.getBytes());
            file.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


    }
}