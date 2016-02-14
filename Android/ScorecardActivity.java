package com.adamwilson.golf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adamwilson.golf.DataModel.GolfDB;

import java.util.ArrayList;
import java.util.List;


public class ScorecardActivity extends ActionBarActivity {
    private ProfileBar profileBar;
    private RelativeLayout layout;
    private ArrayList<String[]> round;
    private ArrayList<String[]> holes;
    private String currentCourse;
    private String continueCourse;

    public boolean isPlaying = true;
    private boolean is18HoleCourse = true;

    private boolean golferHasMultipleCards = false;
    private int currentCard = 1;

    private TextView golfer1;

    private TextView golfer2;
    private ArrayList<TextView> golfer2Labels;
    private TextView golfer3;
    private ArrayList<TextView> golfer3Labels;
    private TextView golfer4;
    private ArrayList<TextView> golfer4Labels;

    private LinearLayout scorecardBG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorecard);

        scorecardBG = (LinearLayout) findViewById(R.id.scorecard_bg);
        layout = (RelativeLayout) findViewById(R.id.scorecard_Layout);

        Intent intent = getIntent();
        round = (ArrayList<String[]>) intent.getSerializableExtra("com.adamwilson.golf.scorecard_round");
        currentCourse = (String) intent.getStringExtra("com.adamwilson.golf.scorecard_course");
        holes = CourseSelectActivity.allHolesForCourses.get(currentCourse);
        isPlaying = intent.getBooleanExtra("com.adamwilson.golf.scorecard_isPlaying",true);

        golfer1 = (TextView) findViewById(R.id.sc_golfer1);
        golfer2 = (TextView) findViewById(R.id.sc_golfer2);
        golfer3 = (TextView) findViewById(R.id.sc_golfer3);
        golfer4 = (TextView) findViewById(R.id.sc_golfer4);

        golfer2Labels = new ArrayList<TextView>();
        golfer3Labels = new ArrayList<TextView>();
        golfer4Labels = new ArrayList<TextView>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scorecard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();

        profileBar = ProfileBar.getProfileBar(this);
        profileBar.areSettingsAvailable = false;
        profileBar.shouldShowBackButton = true;
        profileBar.setupProfileBar(getSupportActionBar(), this);

        scorecardBG.setBackgroundResource(R.drawable.scorecard);

        golfer1.setText(profileBar.currentProfileName);

        if (numHolesForGolfer() > 9){
            golferHasMultipleCards = true;
        }

        if (golferHasMultipleCards){
            layout.setOnTouchListener(new SwipeListener(this) {
                //                public void swipeRight() {cycleCards();}
                public void swipeLeft() {
                    cycleCards();
                }
            });
        }

        scorecardForRound(currentCourse);
    }

    @Override
    protected void onPause() {
        super.onPause();
        profileBar = null;
        scorecardBG.setBackgroundResource(0);
    }

    private int numHolesForGolfer() {
        int entriesForPlayer = 0;
        for (String[] entry : round) {
            String golfer = entry[1];
            if (golfer.equalsIgnoreCase(profileBar.currentProfileName) && (entry[0].equalsIgnoreCase(currentCourse))){
                entriesForPlayer++;
            }
            if (!entry[0].equalsIgnoreCase(currentCourse)){
                continueCourse = entry[0];
                is18HoleCourse = false;
            }
        }
        return entriesForPlayer;
    }

    public void scorecardForRound(String courseName) {
        //set the static label values
        for (String[] entry : holes) {
            String holeNumber = entry[0];
            String par = entry[1];
            String handicap = entry[11];

            //holeNumber = holeNumber.replaceFirst("[A-Z]", "");
            if (is18HoleCourse) {
                if (currentCard == 1) {
                    if (Integer.parseInt(holeNumber) > 9) {
                        continue;
                    }
                } else if (currentCard == 2) {
                    if (Integer.parseInt(holeNumber) < 10) {
                        continue;
                    }
                }
            }

            /*else if (currentCard == 3){
                if (Integer.parseInt(holeNumber) < 18) {
                    continue;
                }
            }*/


            //trim off hole number identifier to get the label number to fill
            int labelID;
            if (Integer.parseInt(holeNumber) > 9) {
                labelID = Integer.parseInt(holeNumber) % 9;
            } else {
                labelID = Integer.parseInt(holeNumber);
            }

            //get all the static labels
            String parXML = "sc_p" + labelID;
            int parID = this.getResources().getIdentifier(parXML, "id", this.getPackageName());
            TextView parLabel = (TextView) findViewById(parID);
            String holeXML = "sc_h" + labelID;
            int holeID = this.getResources().getIdentifier(holeXML, "id", this.getPackageName());
            TextView holeLabel = (TextView) findViewById(holeID);
            String handicapXML = "sc_ha" + labelID;
            int handicapID = this.getResources().getIdentifier(handicapXML, "id", this.getPackageName());
            TextView handicapLabel = (TextView) findViewById(handicapID);

            //fill static labels with values from entry
            parLabel.setText(par);
            holeLabel.setText(holeNumber);
            handicapLabel.setText(handicap);
        }

        for (String[] entry : round) {
            String course = entry[0];
            String golfer = entry[1];
            String score = entry[5];
            String holeNumber = entry[2];

            //holeNumber = holeNumber.replaceFirst("[A-Z]", "");
            if (is18HoleCourse) {
                if (currentCard == 1) {
                    if (Integer.parseInt(holeNumber) > 9) {
                        continue;
                    }
                } else if (currentCard == 2) {
                    if (Integer.parseInt(holeNumber) < 10) {
                        continue;
                    }
                }
            }else{
                if (!course.equalsIgnoreCase(courseName)) {
                    continue;
                }
            }

            //trim off hole number identifier to get the label number to fill
            int labelID;
            //holeNumber = holeNumber.replaceFirst("[A-Z]", "");
            if (Integer.parseInt(holeNumber) > 9) {
                labelID = Integer.parseInt(holeNumber) % 9;
            } else {
                labelID = Integer.parseInt(holeNumber);
            }

            if (golfer.equalsIgnoreCase(profileBar.currentProfileName)) {
                String golferXML = "sc_g1" + labelID;
                int golfer1ID = this.getResources().getIdentifier(golferXML, "id", this.getPackageName());
                TextView golferScoreLabel = (TextView) findViewById(golfer1ID);
                golferScoreLabel.setText(score);
            } else {
                if (golfer2.getText().toString().equalsIgnoreCase("") || golfer2.getText().toString().equalsIgnoreCase(golfer)) {
                    golfer2.setText(golfer);
                    int golfer1ID = this.getResources().getIdentifier("sc_g2" + labelID, "id", this.getPackageName());
                    TextView golfer2ScoreLabel = (TextView) findViewById(golfer1ID);
                    golfer2ScoreLabel.setText(score);
                } else if (golfer3.getText().toString().equalsIgnoreCase("") || golfer3.getText().toString().equalsIgnoreCase(golfer)) {
                    golfer3.setText(golfer);
                    int golfer1ID = this.getResources().getIdentifier("sc_g3" + labelID, "id", this.getPackageName());
                    TextView golfer3ScoreLabel = (TextView) findViewById(golfer1ID);
                    golfer3ScoreLabel.setText(score);
                } else if (golfer4.getText().toString().equalsIgnoreCase("") || golfer4.getText().toString().equalsIgnoreCase(golfer)) {
                    golfer4.setText(golfer);
                    int golfer1ID = this.getResources().getIdentifier("sc_g4" + labelID, "id", this.getPackageName());
                    TextView golfer4ScoreLabel = (TextView) findViewById(golfer1ID);
                    golfer4ScoreLabel.setText(score);
                }
            }
        }
        setLabelBackgroungs();
    }

    private void setLabelBackgroungs(){

        int parTotal = 0;
        int g1ScoreTotal = 0;
        int g2ScoreTotal = 0;
        int g3ScoreTotal = 0;
        int g4ScoreTotal = 0;

        for (int i = 1; i < 10;i++) {
            String par = "sc_p" + Integer.toString(i);
            int parId = this.getResources().getIdentifier(par, "id", this.getPackageName());
            TextView parLabel = (TextView) findViewById(parId);
            int p = Integer.parseInt(parLabel.getText().toString());
            parTotal += p;

            String golfer = "sc_g1" + Integer.toString(i);
            int golfer1Id = this.getResources().getIdentifier(golfer, "id", this.getPackageName());
            TextView golfer1Label = (TextView) findViewById(golfer1Id);

            if (golfer1Label.getText() != null && !golfer1Label.getText().toString().equalsIgnoreCase("")) {
                int score = Integer.parseInt(golfer1Label.getText().toString());
                g1ScoreTotal += score;
            }

            ArrayList<TextView> labelsArray = new ArrayList<>();
            labelsArray.add(golfer1Label);

            //get the golferlabels if needed
            String g2 = "sc_g2" + i;
            int golfer2Id = this.getResources().getIdentifier(g2, "id", this.getPackageName());
            TextView golfer2Label = (TextView) findViewById(golfer2Id);
            if (!golfer2.getText().toString().equalsIgnoreCase("")) {
                labelsArray.add(golfer2Label);
                golfer2Labels.add(golfer2Label);

                if (!golfer2Label.getText().toString().equalsIgnoreCase("")) {
                    int score = Integer.parseInt(golfer2Label.getText().toString());
                    g2ScoreTotal += score;
                }
            } else {
                golfer2Label.setEnabled(false);
            }

            String g3 = "sc_g3" + i;
            int golfer3Id = this.getResources().getIdentifier(g3, "id", this.getPackageName());
            TextView golfer3Label = (TextView) findViewById(golfer3Id);
            if (!golfer3.getText().toString().equalsIgnoreCase("")) {
                labelsArray.add(golfer3Label);
                golfer3Labels.add(golfer3Label);

                if (!golfer1Label.getText().toString().equalsIgnoreCase("")) {
                    int score = Integer.parseInt(golfer3Label.getText().toString());
                    g3ScoreTotal += score;
                }
            } else {
                golfer3Label.setEnabled(false);
            }

            String g4 = "sc_g4" + i;
            int golfer4Id = this.getResources().getIdentifier(g4, "id", this.getPackageName());
            TextView golfer4Label = (TextView) findViewById(golfer4Id);
            if (!golfer4.getText().toString().equalsIgnoreCase("")) {
                labelsArray.add(golfer4Label);
                golfer4Labels.add(golfer4Label);

                if (!golfer1Label.getText().toString().equalsIgnoreCase("")) {
                    int score = Integer.parseInt(golfer4Label.getText().toString());
                    g4ScoreTotal += score;
                }
            } else {
                golfer4Label.setEnabled(false);
            }

            //add backgrounds and colors to scorelabels
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.scorecard_Layout);


            for (TextView textView : labelsArray) {
                if (textView.getText() != null && !textView.getText().toString().equalsIgnoreCase("")) {
                    int golferScore = Integer.parseInt(textView.getText().toString());

                    if (golferScore == p) { //par
                        textView.setTextColor(getResources().getColor(R.color.g_green));

                    } else if (golferScore == p + 1) { //bogie
                        textView.setTextColor(getResources().getColor(R.color.g_yellow));
                        textView.setBackgroundResource(R.drawable.scorecard_bogie);

                    } else if (golferScore == p + 2) { //double bogie
                        textView.setTextColor(getResources().getColor(R.color.lt_gray));
                        textView.setBackgroundResource(R.drawable.scorecard_doublebogie);

                    } else if (golferScore == p - 1) { // birdie
                        textView.setTextColor(getResources().getColor(R.color.g_blue));
                        textView.setBackgroundResource(R.drawable.scorecard_birdie);

                    } else if (golferScore == p - 2) { // eagle
                        textView.setTextColor(getResources().getColor(R.color.lt_gray));
                        textView.setBackgroundResource(R.drawable.scorecard_eagle);
                    }
                }
            }
        }

        String parKey = "sc_p10";
        int parTotalID = this.getResources().getIdentifier(parKey, "id", this.getPackageName());
        TextView parTotalLabel = (TextView) findViewById(parTotalID);
        parTotalLabel.setText(Integer.toString(parTotal));

        String golferXML = "sc_g110";
        int golfer1TotalID = this.getResources().getIdentifier(golferXML, "id", this.getPackageName());
        TextView golfer1TotalLabel = (TextView) findViewById(golfer1TotalID);
        golfer1TotalLabel.setText(Integer.toString(g1ScoreTotal));

        if (g2ScoreTotal != 0){
            int golfer2TotalID = this.getResources().getIdentifier("sc_g210", "id", this.getPackageName());
            TextView golfer2TotalLabel = (TextView) findViewById(golfer2TotalID);
            golfer2TotalLabel.setText(Integer.toString(g2ScoreTotal));
        }
        if (g3ScoreTotal != 0){
            int golfer3TotalID = this.getResources().getIdentifier("sc_g310", "id", this.getPackageName());
            TextView golfer3TotalLabel = (TextView) findViewById(golfer3TotalID);
            golfer3TotalLabel.setText(Integer.toString(g3ScoreTotal));
        }
        if (g4ScoreTotal != 0){
            int golfer4TotalID = this.getResources().getIdentifier("sc_g410", "id", this.getPackageName());
            TextView golfer4TotalLabel = (TextView) findViewById(golfer4TotalID);
            golfer4TotalLabel.setText(Integer.toString(g4ScoreTotal));
        }

    }

    public void addGolfer(View view){
        if (isPlaying) {
            //display textfield for user
            final TextView golferNameLabel = (TextView) view;
            final EditText enterGolferName = new EditText(this);

            final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.scorecard_Layout);
            RelativeLayout.LayoutParams r_layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            r_layout.addRule(RelativeLayout.CENTER_IN_PARENT);
            enterGolferName.setLayoutParams(r_layout);
            relativeLayout.addView(enterGolferName);

            //set editText properties
            enterGolferName.setHint("Enter golfer name");
            enterGolferName.setBackgroundColor(Color.GRAY);
            enterGolferName.setSingleLine(true);
            enterGolferName.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
            enterGolferName.setImeOptions(EditorInfo.IME_ACTION_DONE);

            //wait for "done"
            enterGolferName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String golferName = enterGolferName.getText().toString();
                        if (!golferName.trim().equalsIgnoreCase("")) {
                            golferNameLabel.setText(golferName);
                            saveScoresInDatabase(golferName, "1", "0");
                        }
                        //dismiss keyboard & editText
                        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(enterGolferName
                                        .getApplicationWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        relativeLayout.removeView(enterGolferName);

                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void incrementScore(View view) {
        TextView scoreLabel = (TextView) view;

        if (isPlaying) {
            if (scoreLabel.getText().toString().equalsIgnoreCase("")) {
                scoreLabel.setText("0");
            }

            int score = Integer.parseInt(scoreLabel.getText().toString());

            if (score == 8) {
                score = 0;
            } else {
                score += 1;
            }

            String scoreText = Integer.toString(score);
            scoreLabel.setText(scoreText);

            if (golfer2Labels.contains(scoreLabel)){
                String holeNumber = Integer.toString(golfer2Labels.indexOf(scoreLabel)+1);
                saveScoresInDatabase(golfer2.getText().toString(), holeNumber, scoreText);
            }else if (golfer3Labels.contains(scoreLabel)){
                String holeNumber = Integer.toString(golfer3Labels.indexOf(scoreLabel)+1);
                saveScoresInDatabase(golfer3.getText().toString(),holeNumber,scoreText);
            }else if (golfer4Labels.contains(scoreLabel)){
                String holeNumber = Integer.toString(golfer4Labels.indexOf(scoreLabel)+1);
                saveScoresInDatabase(golfer4.getText().toString(),holeNumber,scoreText);
            }
        }
    }

    private void saveScoresInDatabase(String golfer, String holeNumber, String score){
        //HoleActivity parentAct = ((HoleActivity) this.context);
        GolfDB golfDB = GolfDB.getGolfDatabase(this);

        //check if new hole
        boolean isNewEntry = true;
        Integer index = 1;
        ArrayList<String[]> allEntries = golfDB.getAllEntriesForCurrentRound();

        //check if current course, hole
        for (String[] entry : allEntries) {
            if ((entry[2].equalsIgnoreCase(holeNumber)) && entry[0].equalsIgnoreCase(currentCourse) && (entry[1].equalsIgnoreCase(golfer))) {
                isNewEntry = false;
                golfDB.updateHole(index, currentCourse, golfer,
                        holeNumber, "0", "0", score, "0", "", "");
                break;
            }
            index++;
        }

        if (isNewEntry){
            golfDB.insertHole(currentCourse, golfer, holeNumber, "0", "0", score,
                    "0", "", "");
        }
    }

    private void cycleCards(){
        //load next set of holes from db
        String courseToDisplay = currentCourse;

        if (currentCard + 1 > 2){
            currentCard = 1;
            if (!is18HoleCourse){
                courseToDisplay = currentCourse;
                holes = CourseSelectActivity.allHolesForCourses.get(currentCourse);
            }
        }else{
            currentCard++;
            if (!is18HoleCourse){
                courseToDisplay = continueCourse;
                holes = CourseSelectActivity.allHolesForCourses.get(continueCourse);
            }
        }

        scorecardForRound(courseToDisplay);
    }

    /*private ArrayList<String[]> getHolesForCourse(String courseName) {

        ArrayList<String[]> holesForCourse = new ArrayList<String[]>();
        char hole_prefix = courseName.charAt(0);

        for (String[] hole : CourseSelectActivity.allHolesForCourses) {
            String holeNumber = hole[0];
            if (holeNumber.charAt(0) == hole_prefix) {
                holesForCourse.add(hole);
            }
        }

        return holesForCourse;
    }*/
}
