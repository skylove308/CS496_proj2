package com.example.q.project1;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

public class Tab3Game extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3game, container, false);

        GridView gv = (GridView) rootView.findViewById(R.id.gameGridView);
        GameGridViewAdapter gAdapter = new GameGridViewAdapter(getContext());
        gv.setAdapter(gAdapter);


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
}
