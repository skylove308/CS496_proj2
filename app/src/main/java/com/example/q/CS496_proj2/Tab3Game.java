package com.example.q.CS496_proj2;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Tab3Game extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3game, container, false);

        GridView gv = rootView.findViewById(R.id.gameGridView);
        GameGridViewAdapter gAdapter = new GameGridViewAdapter(getContext());
        gv.setAdapter(gAdapter);

        stageTextView = rootView.findViewById(R.id.stageTextView);

        /* listener for start button */
        Button startButton = rootView.findViewById(R.id.startButton);
        Button showButton = rootView.findViewById(R.id.showButton);
//        Button readtxt = rootView.findViewById(R.id.readtxt);

        startButton.setOnClickListener(startButtonListener);
        showButton.setOnClickListener(showButtonListener);
//        readtxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                File dir = getContext().getFilesDir();
//                File file = new File(dir, "scores.txt");
//
//                boolean result = file.delete();
//
//                scores.clear();
//            }
//        });

        loadRank();

        return rootView;
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
        TextView stageTextView = getActivity().findViewById(R.id.stageTextView);
        Button startButton = getActivity().findViewById(R.id.startButton);
        Button showButton = getActivity().findViewById(R.id.showButton);
//        Button resetButton = getActivity().findViewById(R.id.readtxt);
        stageTextView.setText("SIMON!");
        startButton.setVisibility(View.VISIBLE);
        showButton.setVisibility(View.VISIBLE);
//        resetButton.setVisibility(View.VISIBLE);

    }

    private void startGame() {
        Button startButton = getActivity().findViewById(R.id.startButton);
        Button showButton = getActivity().findViewById(R.id.showButton);
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

        final Integer stageResult = currentStage;

        final View dialogView = View.inflate(getContext(), R.layout.tab3dialog, null);
        final EditText editName = dialogView.findViewById(R.id.editName);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Game Over!");
        alertDialogBuilder.setMessage("You got to stage " + Integer.toString(currentStage));
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String userName = editName.getText().toString();
                addRank(userName, stageResult);
            }
        });

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

    private void loadRank() {

        try {
            final FileInputStream file = getContext().openFileInput("scores.txt");
            byte[] txt = new byte[1000];
            file.read(txt);
            String str = new String(txt);
            String[] tokens = str.split("\r\n");

            scores.clear();

            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].contains("!v!r!")) {
                    String[] tmpStr = tokens[i].split("!v!r!");
                    scores.add(tmpStr);
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

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

        ListView scoreListView = rankView.findViewById(R.id.rankListView);
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

            TextView rankTextView = convertView.findViewById(R.id.rankTextView);
            TextView nameTextView = convertView.findViewById(R.id.nameTextView);
            TextView scoreTextView = convertView.findViewById(R.id.scoreTextView);

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