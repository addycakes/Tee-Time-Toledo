package com.adamwilson.golf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.VideoView;

import com.adamwilson.golf.DataModel.GolfDB;
import com.google.android.gms.maps.MapFragment;
import java.util.ArrayList;

public class HoleActivity extends ActionBarActivity {
    public String course;
    public ArrayList<String> golfers;
    private ArrayList<String[]> holes;
    public String[] currentHole;
    public ProfileBar profileBar;
    private boolean isBeginningRound;
    public GolfDB roundsDB;
    private FrameLayout mainView;
    private VideoView videoView;
    private MediaController mediaController;
    RangeFinder rangeFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hole);

        isBeginningRound = true;
        roundsDB = GolfDB.getGolfDatabase(this);

        //Get extras
        Intent intent = getIntent();
        course = intent.getStringExtra("com.adamwilson.golf.Course");
        golfers = intent.getStringArrayListExtra("com.adamwilson.golf.Golfers");
        holes = (ArrayList<String[]>) intent.getSerializableExtra("com.adamwilson.golf.Holes");
        String[] mapData = intent.getStringArrayExtra("com.adamwilson.golf.Map");

        //set up action bar
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.range_finder);
        rangeFinder = new RangeFinder(mapFragment, this, mapData);
        holeDistanceLabel = (TextView) findViewById(R.id.textView6);
        holeParLabel = (TextView) findViewById(R.id.textView5);
        holeNumberLabel = (TextView) findViewById(R.id.textView4);

        mainView = (FrameLayout) findViewById(R.id.main_view);
        mainView.setOnTouchListener(new SwipeListener(this) {
            public void swipeRight() {
                cycleHoles("up");
            }
            public void swipeLeft() {
                cycleHoles("down");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        profileBar = ProfileBar.getProfileBar(this);
        profileBar.areSettingsAvailable = false;
        profileBar.shouldShowBackButton = true;
        profileBar.setupProfileBar(getSupportActionBar(), this);

        mainView.setBackgroundResource(R.drawable.world_bg);

        //load golfers into round
        if (!CourseSelectActivity.isContinuing && isBeginningRound){
            isBeginningRound = false;
            setupGolfers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        profileBar = null;
        mainView.setBackgroundResource(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hole, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        //start on hole 1
        if  (currentHole == null){
            currentHole = (String[]) holes.get(0);
            updateHole();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            ArrayList<String[]> round = roundsDB.getAllEntriesForCurrentRound();

            int n = 0;
            for (String[] entry : round){
                if (entry[1].equalsIgnoreCase(profileBar.currentProfileName)){
                    n++;
                }
            }

            if  (n < 11) {
                builder.setTitle("Select Next Course?");
                // Add the buttons
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        CourseSelectActivity.isContinuing = true;
                        rangeFinder.prepareForFinish();
                        finish();
                    }
                });
                builder.setNegativeButton("quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        CourseSelectActivity.isContinuing = false;
                        rangeFinder.prepareForFinish();
                        finish();
                    }
                });
                builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
            }else{
                builder.setTitle("Quit?");
                // Add the buttons
                builder.setNegativeButton("quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        CourseSelectActivity.isContinuing = false;
                        rangeFinder.prepareForFinish();
                        finish();
                    }
                });
                builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
            }

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cycleHoles(String dir) {

        //get index of current hole
        int index = holes.indexOf(currentHole);

        if (dir.equalsIgnoreCase("up")) {
            index++;
            //if past last item, circle back around
            if (index >= holes.size()) {
                index = 0;
            }
        } else if (dir.equalsIgnoreCase("down")) {
            index--;
            //if past first item, circle back around
            if (index < 0) {
                index = holes.size() - 1;
            }
        }

        currentHole = (String[]) holes.get(index);
        updateHole();

        if (index == 5) {
            if (!hasDisplayedAd) {
                displayAd();
                hasDisplayedAd = true;
            }
        }
    }

    TextView holeNumberLabel;
    TextView holeParLabel;
    TextView holeDistanceLabel;
    private void updateHole() {
        //set label values
        holeNumberLabel.setText("Hole " + currentHole[0]);
        holeParLabel.setText("Par " + currentHole[1]);
        holeDistanceLabel.setText(currentHole[2]);

        //rangefinder send current hole for map properties
        rangeFinder.loadMap(currentHole);
    }

    private boolean hasDisplayedAd = false;
    private void displayAd() {

        LinearLayout adLayout = new LinearLayout(this);
        LinearLayout.LayoutParams adParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        adLayout.setOrientation(LinearLayout.VERTICAL);
        adLayout.setLayoutParams(adParams);
        adLayout.setBackgroundColor(getResources().getColor(R.color.lt_gray));

        TextView title = new TextView(this);
        title.setText("Your Ad Here");
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(32);

        adLayout.addView(title, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        AlertDialog.Builder advertisement = new AlertDialog.Builder(this);
        advertisement.setView(adLayout);

        // Add the buttons
        advertisement.setNeutralButton("close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });

        advertisement.show();
    }

    public void logStroke(View view) {
        StrokeLog strokeLog = new StrokeLog();
        strokeLog.context = this;
        strokeLog.hole = currentHole;
        strokeLog.show(getFragmentManager(), "StrokeLog");
    }

    private boolean historyIsHidden = true;
    public void toggleHistory(View view) {
        historyIsHidden = !historyIsHidden;
        rangeFinder.toggleHistory(historyIsHidden);
    }

    public void viewMedia(View view) {

        VideoActivity videoActivity = new VideoActivity();
        videoActivity.videoURL = currentHole[12];
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
    }

    public void viewScorecard(View view) {
        Intent intent = new Intent(this, ScorecardActivity.class);
        ArrayList<String[]> allEntries = roundsDB.getAllEntriesForCurrentRound();
        intent.putExtra("com.adamwilson.golf.scorecard_round", allEntries);
        intent.putExtra("com.adamwilson.golf.scorecard_isPlaying", true);
        intent.putExtra("com.adamwilson.golf.scorecard_course", course);
        startActivity(intent);
    }

    private void setupGolfers() {
        for (String golfer : golfers){
            roundsDB.insertHole(course, golfer, currentHole[0], "0", "0", "0", "0", "", "");
        }
    }

}

