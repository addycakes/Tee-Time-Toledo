package com.adamwilson.golf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.adamwilson.golf.DataModel.GolfDB;

import java.util.ArrayList;

/**
 * Created by adam on 6/28/15.
 */
public class ScoreAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private String[] scores;
    private Context context;

    public ScoreAdapter(ArrayList<String> list, String[] scoreList, Context context) {
        this.list = list;
        this.context = context;
        this.scores = scoreList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return list.get(pos).getId();
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.score_listview_item, null);
        }

        //Handle TextView and display string from your list
        final TextView scoreView = (TextView)view.findViewById(R.id.list_item_score);
        final TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));
        scoreView.setText(scores[position]);

        //Handle buttons and add onClickListeners
        Button minusBtn = (Button)view.findViewById(R.id.minus_btn);
        Button plusBtn = (Button)view.findViewById(R.id.plus_btn);

        //save scores in arraylist for db; same positions as list_item_string
        minusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                String score = scoreView.getText().toString();
                Integer s = Integer.valueOf(score);
                if (s > 0) {
                    s -= 1;
                    String newScore = String.valueOf(s);
                    scoreView.setText(newScore);
                    //scores.set(position, newScore);
                    scores[position] = newScore;

                    notifyDataSetChanged();
                    saveScoresInDatabase(list.get(position),scores[position]);
                }
            }
        });
        plusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                String score = scoreView.getText().toString();
                Integer s = Integer.valueOf(score);
                s += 1;
                String newScore = String.valueOf(s);
                scoreView.setText(newScore);
                //scores.set(position, newScore);
                scores[position] = newScore;

                notifyDataSetChanged();
                saveScoresInDatabase(list.get(position),scores[position]);
            }
        });

        return view;
    }

    private void saveScoresInDatabase(String golfer, String score){
        //HoleActivity parentAct = ((HoleActivity) this.context);
        GolfDB golfDB = GolfDB.getGolfDatabase(this.context);

        String course = ((HoleActivity) this.context).course;
        String[] hole = ((HoleActivity) this.context).currentHole;

        //check if new hole
        boolean isNewEntry = true;
        Integer index = 1;
        ArrayList<String[]> allEntries = golfDB.getAllEntriesForCurrentRound();

        //check if current course, hole
        for (String[] entry : allEntries) {
            if ((entry[2].equalsIgnoreCase(hole[0])) && (entry[0].equalsIgnoreCase(course))) {
                // check golfer exists in database
                for (int i = 0; i < list.size(); i++) {
                    if (entry[1].equalsIgnoreCase(list.get(i))) {
                        isNewEntry = false;
                       // golfDB.updateHole(index, course, golfer, hole[0], hole[1], score, hole[4], hole[5], hole[6]);
                        break;
                    }
                }
            }
            index++;
        }

        if (isNewEntry){
            //golfDB.insertHole(course, golfer, hole[0], hole[1], score);
        }
    }
}
