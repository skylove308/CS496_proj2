package com.example.q.project1;

import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tab3Game extends Fragment {

    private boolean userMode = false;
    final private int duration = 500;
    private int currentIndex = 0;
    private int currentStage = 0;

    private TextView stageTextView;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    private List<Integer> answers = new ArrayList<Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3game, container, false);

        GridView gv = (GridView) rootView.findViewById(R.id.gameGridView);
        GameGridViewAdapter gAdapter = new GameGridViewAdapter(getContext());
        gv.setAdapter(gAdapter);

        stageTextView = (TextView) rootView.findViewById(R.id.stageTextView);

        /* listener for start button */
        Button startButton = (Button) rootView.findViewById(R.id.startButton);
        startButton.setOnClickListener(startButtonListener);

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
                        Toast.makeText(getActivity(), "SOMETHING WENT WRONG in simulate()", Toast.LENGTH_LONG);
                    }
                };
                r2 = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "SOMETHING WENT WRONG in simulate()", Toast.LENGTH_LONG);
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
        stageTextView.setText("SIMON!");
        startButton.setVisibility(View.VISIBLE);
    }

    private void startGame() {
        Button startButton = (Button) getActivity().findViewById(R.id.startButton);
        startButton.setVisibility(View.GONE);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Game Over!");
        alertDialogBuilder.setMessage("You got to stage " + Integer.toString(currentStage));
        alertDialogBuilder.show();

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
}