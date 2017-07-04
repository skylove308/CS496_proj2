package com.example.q.project1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.Random;

public class Tab3Game extends Fragment {

<<<<<<< HEAD
    private int currentStage = 1;
    private TextView stageTextView;
    private List<Integer> answers = new ArrayList<Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3game, container, false);

        GridView gv = (GridView) rootView.findViewById(R.id.gameGridView);
        GameGridViewAdapter gAdapter = new GameGridViewAdapter(getContext());
        gv.setAdapter(gAdapter);

        /* listener for start button */
        Button startButton = (Button) rootView.findViewById(R.id.startButton);
        stageTextView = (TextView) rootView.findViewById(R.id.stageTextView);
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
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linear = new LinearLayout(context);
            linear.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 500));
            linear.setPadding(32, 32, 32, 32);

            Button button = new Button(context);

            switch (position) {
                case 0:
                    button.setBackgroundResource(R.drawable.game_shape_1);
                    break;
                case 1:
                    button.setBackgroundResource(R.drawable.game_shape_2);
                    break;
                case 2:
                    button.setBackgroundResource(R.drawable.game_shape_3);
                    break;
                case 3:
                    button.setBackgroundResource(R.drawable.game_shape_4);
                    break;
            }

            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            linear.addView(button);

            return linear;
        }
    }

    private void simulate() {

    }

    View.OnClickListener startButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Button startButton = (Button) v.findViewById(R.id.startButton);
            startButton.setVisibility(View.INVISIBLE);
            stageTextView.setText("Stage " + Integer.toString(currentStage));

        }
    };
    public boolean playStage(Integer stage_cnt, ArrayList<Integer> answer) {
        // 문제가 나오는 과정
        // 사용자가 입력하는 과정
        ArrayList<Integer> input = new ArrayList<Integer>();
//        while () {
//            input.equals(answer);
//        }

    public void startGame() {

        Random random = new Random();
        Integer stage_cnt = 3;
        ArrayList<Integer> answer = new ArrayList<Integer>();

        for (int i = 0; i < 3; i++) {
            answer.add(random.nextInt(4));
        }

        playStage(stage_cnt, answer);

        while (playStage(stage_cnt, answer)) {
            stage_cnt++;
            answer.add(random.nextInt(4))
        }

    }
}
