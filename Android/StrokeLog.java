package com.adamwilson.golf;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.adamwilson.golf.DataModel.GolfDB;

import java.util.ArrayList;

/**
 * Created by adam on 8/19/15.
 */
public class StrokeLog extends DialogFragment implements View.OnClickListener{
    private GolfDB golfDB;

    private ImageButton leftFairwayButton;
    private ImageButton rightFairwayButton;
    private ImageButton centerFairwayButton;

    private ImageButton leftGreensButton;
    private ImageButton rightGreensButton;
    private ImageButton centerGreensButton;

    private Button scoreButton;
    private Button puttsButton;

    public Activity context;
    public String[] hole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.dialog_stroke_log, container, false);

        golfDB = GolfDB.getGolfDatabase(this.context);

        leftFairwayButton = (ImageButton) content.findViewById(R.id.leftFairwayButton);
        rightFairwayButton = (ImageButton) content.findViewById(R.id.rightFairwayButton);
        centerFairwayButton = (ImageButton) content.findViewById(R.id.centerFairwayButton);
        leftGreensButton = (ImageButton) content.findViewById(R.id.leftGreensButton);
        rightGreensButton = (ImageButton) content.findViewById(R.id.rightGreensButton);
        centerGreensButton = (ImageButton) content.findViewById(R.id.centerGreensButton);
        scoreButton = (Button) content.findViewById(R.id.scoreButton);
        puttsButton = (Button) content.findViewById(R.id.puttsButton);

        leftGreensButton.setOnClickListener(this);
        rightGreensButton.setOnClickListener(this);
        centerGreensButton.setOnClickListener(this);

        if (!hole[1].equalsIgnoreCase("3")){
            leftFairwayButton.setOnClickListener(this);
            rightFairwayButton.setOnClickListener(this);
            centerFairwayButton.setOnClickListener(this);
        }

        scoreButton.setOnClickListener(this);
        puttsButton.setOnClickListener(this);

        loadHoleFromDatabase();

        return content;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSceenSize();
    }

    private void getSceenSize(){
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenDensity = metrics.densityDpi;

        if (screenDensity == DisplayMetrics.DENSITY_MEDIUM) {
            if ((context.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) ==
                    Configuration.SCREENLAYOUT_SIZE_NORMAL) {
                getDialog().getWindow().setLayout(325, 375);
            }else{
                getDialog().getWindow().setLayout(425, 475);
            }
        } else if (screenDensity == DisplayMetrics.DENSITY_HIGH) {
            getDialog().getWindow().setLayout(500, 600);
        } else if (screenDensity == DisplayMetrics.DENSITY_XXHIGH) {
            getDialog().getWindow().setLayout(1000, 1100);
        }else{
            getDialog().getWindow().setLayout(650, 750);
        }
    }

    private int score = 0;
    private int putts = 0;
    private String fairway = "";
    private String greens = "";
    private boolean isFairwaySet = false;
    private boolean isGreensSet = false;
    @Override
    public void onClick(View v) {
        if (v.getId() == leftFairwayButton.getId()) {
            if (!leftFairwayButton.isSelected() && !isFairwaySet){
                fairway = "Left";
                saveScoresInDatabase();
                leftFairwayButton.setSelected(true);
                leftFairwayButton.setImageResource(R.drawable.left_fairway_button_selected);
                isFairwaySet = true;
            }else if (leftFairwayButton.isSelected() && isFairwaySet){
                fairway = "";
                saveScoresInDatabase();
                leftFairwayButton.setSelected(false);
                leftFairwayButton.setImageResource(R.drawable.left_fairway_button);
                isFairwaySet = false;
            }
        }else if (v.getId() == rightFairwayButton.getId()) {
            if (!rightFairwayButton.isSelected() && !isFairwaySet) {
                fairway = "Right";
                isFairwaySet = true;
                saveScoresInDatabase();
                rightFairwayButton.setSelected(true);
                rightFairwayButton.setImageResource(R.drawable.right_fairway_button_selected);
            }else if (rightFairwayButton.isSelected() && isFairwaySet){
                fairway = "";
                isFairwaySet = false;
                saveScoresInDatabase();
                rightFairwayButton.setSelected(false);
                rightFairwayButton.setImageResource(R.drawable.right_fairway_button);
            }

        }else if (v.getId() == centerFairwayButton.getId()) {
            if (!centerFairwayButton.isSelected() && !isFairwaySet) {
                fairway = "Center";
                isFairwaySet = true;
                saveScoresInDatabase();
                centerFairwayButton.setSelected(true);
                centerFairwayButton.setImageResource(R.drawable.center_fairway_button_selected);
            }else if (centerFairwayButton.isSelected() && isFairwaySet){
                fairway = "";
                isFairwaySet = false;
                saveScoresInDatabase();
                centerFairwayButton.setSelected(false);
                centerFairwayButton.setImageResource(R.drawable.center_fairway_button);
            }
        }else if (v.getId() == leftGreensButton.getId()) {
            if (!leftGreensButton.isSelected() && !isGreensSet) {
                greens = "Left";
                isGreensSet = true;
                saveScoresInDatabase();
                leftGreensButton.setSelected(true);
                leftGreensButton.setImageResource(R.drawable.left_green_button_selected);
            }else if (leftGreensButton.isSelected() && isGreensSet){
                greens = "";
                isGreensSet = false;
                saveScoresInDatabase();
                leftGreensButton.setSelected(false);
                leftGreensButton.setImageResource(R.drawable.left_green_button);
            }

        }else if (v.getId() == rightGreensButton.getId()) {
            if (!rightGreensButton.isSelected() && !isGreensSet) {
                greens = "Right";
                isGreensSet = true;
                saveScoresInDatabase();
                rightGreensButton.setSelected(true);
                rightGreensButton.setImageResource(R.drawable.right_green_button_selected);
            }else if (rightGreensButton.isSelected() && isGreensSet){
                greens = "";
                isGreensSet = false;
                saveScoresInDatabase();
                rightGreensButton.setSelected(false);
                rightGreensButton.setImageResource(R.drawable.right_green_button);
            }

        }else if (v.getId() == centerGreensButton.getId()) {
            if (!centerGreensButton.isSelected() && !isGreensSet) {
                greens = "Center";
                isGreensSet = true;
                saveScoresInDatabase();
                centerGreensButton.setSelected(true);
                centerGreensButton.setImageResource(R.drawable.center_green_button_selected);
            } else if (centerGreensButton.isSelected() && isGreensSet){
                greens = "";
                isGreensSet = false;
                saveScoresInDatabase();
                centerGreensButton.setSelected(false);
                centerGreensButton.setImageResource(R.drawable.center_green_button);
            }
        }
        if (v.getId() == scoreButton.getId()){
            setScore();
        }
        if (v.getId() == puttsButton.getId()){
            setPutts();
        }
    }

    private final int max_score = 8;
    public void setScore(){
        score = Integer.parseInt(scoreButton.getText().toString());

        if (score < max_score){
            score++;
        }else{
            score = 0;
        }

        scoreButton.setText(Integer.toString(score));
        saveScoresInDatabase();
    }

    public void setPutts(){
        putts = Integer.parseInt(puttsButton.getText().toString());

        if (putts < max_score){
            putts++;
        }else{
            putts = 0;
        }

        puttsButton.setText(Integer.toString(putts));
        saveScoresInDatabase();
    }

    private void saveScoresInDatabase(){
        //HoleActivity parentAct = ((HoleActivity) this.context);
        String course = ((HoleActivity) this.context).course;
        String hole = ((HoleActivity) this.context).currentHole[0];
        String par = ((HoleActivity) this.context).currentHole[1];
        String handicap = ((HoleActivity) this.context).currentHole[11];
        String golfer = ((HoleActivity) this.context).profileBar.currentProfileName;

        //check if new hole
        boolean isNewEntry = true;
        Integer index = 1;
        ArrayList<String[]> allEntries = golfDB.getAllEntriesForCurrentRound();

        //check if current course, hole
        for (String[] entry : allEntries) {
            if ((entry[2].equalsIgnoreCase(hole)) && (entry[0].equalsIgnoreCase(course)) && (entry[1].equalsIgnoreCase(golfer))) {
                isNewEntry = false;
                golfDB.updateHole(index, course, golfer,
                        hole, par, handicap, Integer.toString(score), Integer.toString(putts), greens, fairway);
                break;
            }
            index++;
        }

        if (isNewEntry){
            golfDB.insertHole(course, golfer, hole, par, handicap, Integer.toString(score),
                    Integer.toString(putts), greens, fairway);
        }
    }

    private void loadHoleFromDatabase(){

        String course = ((HoleActivity) this.context).course;
        String hole = ((HoleActivity) this.context).currentHole[0];
        String par = ((HoleActivity) this.context).currentHole[1];
        String handicap = ((HoleActivity) this.context).currentHole[11];
        String golfer = ((HoleActivity) this.context).profileBar.currentProfileName;

        //check if new hole
        ArrayList<String[]> allEntries = golfDB.getAllEntriesForCurrentRound();

        //check if current course, hole
        for (String[] entry : allEntries) {
            if ((entry[2].equalsIgnoreCase(hole))
                    && (entry[0].equalsIgnoreCase(course))
                    && (entry[1].equalsIgnoreCase(golfer))) {

                score = Integer.parseInt(entry[5]);
                scoreButton.setText(entry[5]);

                putts = Integer.parseInt(entry[6]);
                puttsButton.setText(entry[6]);

                greens = entry[7];
                if (greens.equalsIgnoreCase("Left")){
                    leftGreensButton.setSelected(true);
                    leftGreensButton.setImageResource(R.drawable.left_green_button_selected);
                    isGreensSet = true;
                }else if (greens.equalsIgnoreCase("Center")){
                    centerGreensButton.setSelected(true);
                    centerGreensButton.setImageResource(R.drawable.center_green_button_selected);
                    isGreensSet = true;
                }else if (greens.equalsIgnoreCase("Right")){
                    rightGreensButton.setSelected(true);
                    rightGreensButton.setImageResource(R.drawable.right_green_button_selected);
                    isGreensSet = true;
                }

                fairway = entry[8];
                if (fairway.equalsIgnoreCase("Left")){
                    leftFairwayButton.setSelected(true);
                    leftFairwayButton.setImageResource(R.drawable.left_fairway_button_selected);
                    isFairwaySet = true;
                }else if (fairway.equalsIgnoreCase("Center")){
                    centerFairwayButton.setSelected(true);
                    centerFairwayButton.setImageResource(R.drawable.center_fairway_button_selected);
                    isFairwaySet = true;
                }else if (fairway.equalsIgnoreCase("Right")){
                    rightFairwayButton.setSelected(true);
                    rightFairwayButton.setImageResource(R.drawable.right_fairway_button_selected);
                    isFairwaySet = true;
                }

                break;
            }
        }
    }
}