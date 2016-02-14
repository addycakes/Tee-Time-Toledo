package com.adamwilson.golf.Stats;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.opengl.Matrix;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.adamwilson.golf.CourseSelectActivity;
import com.adamwilson.golf.DataModel.GolfDB;
import com.adamwilson.golf.DataModel.StrokesDB;
import com.adamwilson.golf.ProfileBar;
import com.adamwilson.golf.R;
import com.adamwilson.golf.Stats.BarGraphGLSurfaceView;
import com.adamwilson.golf.Stats.CirclesGraphGLSurfaceView;
import com.adamwilson.golf.Stats.PieWheelGLSurfaceView;
import com.adamwilson.golf.Stats.PlotsGraphGLSurfaceView;

import java.util.ArrayList;

public class StatsActivity extends ActionBarActivity {
    private PieWheelGLSurfaceView pieWheelGLView;
    private CirclesGraphGLSurfaceView circlesGraphGLView;
    private BarGraphGLSurfaceView barGraphGLView;
    private PlotsGraphGLSurfaceView plotsGraphGLView;
    private GolfDB statsDB;

    private boolean didAnimatePieWheel = false;
    private boolean didAnimateBars = false;
    private boolean didAnimateCircles = false;
    private boolean didAnimatePlots = false;

    private TextView averageScoreLabel;
    private TextView leftGreensLabel;
    private TextView rightGreensLabel;
    private TextView centerGreensLabel;
    private TextView leftFairwayLabel;
    private TextView rightFairwayLabel;
    private TextView centerFairwayLabel;
    public TextView firstPreviousRoundLabel;
    public TextView secondPreviousRoundLabel;
    public TextView thirdPreviousRoundLabel;
    public TextView fourthPreviousRoundLabel;
    public TextView fifthPreviousRoundLabel;
    private ScrollView scrollView;

    private ProfileBar profileBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
    }

    @Override
    protected void onResume(){
        super.onResume();
        profileBar = ProfileBar.getProfileBar(this);
        profileBar.shouldShowBackButton = true;
        profileBar.areSettingsAvailable = true;
        profileBar.setupProfileBar(getSupportActionBar(), this);
        getSceenSize();
        loadStatistics();
    }

    @Override
    protected void onPause(){
        super.onPause();
        profileBar = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_scorecard) {
            Intent intent = new Intent(this, CourseSelectActivity.class);
            intent.putExtra("com.adamwilson.golf.isSelectingForPlay",false);
            intent.putExtra("com.adamwilson.golf.optionalKey","Scorecard");
            startActivity(intent);
            return true;
        }
        //if (id == R.id.action_view_leaderboard) {
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    private int screenDensity;
    private void getSceenSize(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenDensity = metrics.densityDpi;
    }

    private void labelCounterAnimation(final TextView textView, int finalValue){

        Integer initialValue;
        //only deal in positivies
        if (finalValue >= 10) {
            initialValue = finalValue - 10;
        }else {
            initialValue = 0;
        }

        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(initialValue, finalValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                if (textView.getText().toString().contains("%")){
                    textView.setText(String.valueOf(animation.getAnimatedValue()) + "%");
                }else{
                    textView.setText(String.valueOf(animation.getAnimatedValue()));
                }
            }
        });
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator.setDuration(4000);
        animator.start();
    }

    //labels and values: first:-60 second:-30 third:60 fourth:20 fifth:-10
    private void animatePlotLabels(int first, int second, int third, int fourth, int fifth){

        int densityMultiplier = 1;
        if (screenDensity == DisplayMetrics.DENSITY_MEDIUM) {
            densityMultiplier = 2;
        } else if (screenDensity == DisplayMetrics.DENSITY_HIGH) {
            densityMultiplier = 3;
        } else if (screenDensity == DisplayMetrics.DENSITY_XHIGH) {
            densityMultiplier = 4;
        } else if (screenDensity == DisplayMetrics.DENSITY_XXHIGH) {
            densityMultiplier = 6;
        }

        Animation plotLabelAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF, (Animation.RELATIVE_TO_SELF - densityMultiplier*first));
        plotLabelAnimation.setDuration(1200);
        plotLabelAnimation.setFillAfter(true);
        firstPreviousRoundLabel.startAnimation(plotLabelAnimation);

        Animation plotSecondLabelAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF, (Animation.RELATIVE_TO_SELF - densityMultiplier*second));
        plotSecondLabelAnimation.setDuration(1200);
        plotSecondLabelAnimation.setFillAfter(true);
        secondPreviousRoundLabel.startAnimation(plotSecondLabelAnimation);

        Animation plotThirdLabelAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF, (Animation.RELATIVE_TO_SELF - densityMultiplier*third));
        plotThirdLabelAnimation.setDuration(1200);
        plotThirdLabelAnimation.setFillAfter(true);
        thirdPreviousRoundLabel.startAnimation(plotThirdLabelAnimation);

        Animation plotFourthLabelAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF, (Animation.RELATIVE_TO_SELF - densityMultiplier*fourth));
        plotFourthLabelAnimation.setDuration(1200);
        plotFourthLabelAnimation.setFillAfter(true);
        fourthPreviousRoundLabel.startAnimation(plotFourthLabelAnimation);

        Animation plotFifthLabelAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF, (Animation.RELATIVE_TO_SELF - densityMultiplier*fifth));
        plotFifthLabelAnimation.setDuration(1200);
        plotFifthLabelAnimation.setFillAfter(true);
        fifthPreviousRoundLabel.startAnimation(plotFifthLabelAnimation);
    }

    //labels and values: left:20 center:180 right:60
    private void animateBarLabels(int left, int center, int right){

        float densityMultiplier = 1f;
        if (screenDensity == DisplayMetrics.DENSITY_MEDIUM) {
            densityMultiplier = 1f;
        } else if (screenDensity == DisplayMetrics.DENSITY_HIGH) {
            densityMultiplier = 1.5f;
        } else if (screenDensity == DisplayMetrics.DENSITY_XHIGH) {
            densityMultiplier = 2f;
        } else if (screenDensity == DisplayMetrics.DENSITY_XXHIGH) {
            densityMultiplier = 2.5f;
        }

        Animation leftBarLabelAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF, (Animation.RELATIVE_TO_SELF - densityMultiplier*(left - 100)));
        leftBarLabelAnimation.setDuration(1500);
        leftBarLabelAnimation.setFillAfter(true);
        leftFairwayLabel.startAnimation(leftBarLabelAnimation);

        Animation centerBarLabelAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF, (Animation.RELATIVE_TO_SELF - densityMultiplier*(center - 100)));
        centerBarLabelAnimation.setDuration(1500);
        centerBarLabelAnimation.setFillAfter(true);
        centerFairwayLabel.startAnimation(centerBarLabelAnimation);

        Animation rightBarLabelAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF, (Animation.RELATIVE_TO_SELF - densityMultiplier*(right - 100)));
        rightBarLabelAnimation.setDuration(1500);
        rightBarLabelAnimation.setFillAfter(true);
        rightFairwayLabel.startAnimation(rightBarLabelAnimation);
    }

    private void animateCircleLabels(int left, int right){

        Animation leftCircleLabelAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,(Animation.RELATIVE_TO_SELF - 70*(float)left/100.0f),Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF);
        leftCircleLabelAnimation.setDuration(1500);
        leftCircleLabelAnimation.setFillAfter(true);
        leftGreensLabel.startAnimation(leftCircleLabelAnimation);

        Animation rightCircleLabelAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,(Animation.RELATIVE_TO_SELF + 70*(float)right/100.0f),Animation.RELATIVE_TO_SELF , Animation.RELATIVE_TO_SELF);
        rightCircleLabelAnimation.setDuration(1500);
        rightCircleLabelAnimation.setFillAfter(true);
        rightGreensLabel.startAnimation(rightCircleLabelAnimation);
    }


    int scoreForAllRounds;
    int numRounds;
    int totalPars;
    int totalBogies;
    int totalBirdies;
    int totalStrokes;
    float totalLeftFairways;
    float totalRightFairways;
    float totalCenterFairways;
    float totalFairwayHits;
    float totalLeftGreens;
    float totalRightGreens;
    float totalCenterGreens;
    float totalGreensHits;
    int roundOne;
    int roundTwo;
    int roundThree;
    int roundFour;
    int roundFive;
    private void loadStatistics(){
        scoreForAllRounds = 0;
        numRounds = 0;

        totalPars = 0;
        totalBogies = 0;
        totalBirdies = 0;
        totalStrokes = 0;

        totalLeftFairways = 0;
        totalRightFairways = 0;
        totalCenterFairways = 0;
        totalFairwayHits = 0;

        totalLeftGreens = 0;
        totalRightGreens = 0;
        totalCenterGreens = 0;
        totalGreensHits = 0;

        roundOne = 0;
        roundTwo = 0;
        roundThree = 0;
        roundFour = 0;
        roundFive = 0;

        statsDB = GolfDB.getGolfDatabase(this);
        ArrayList<ArrayList<String[]>> allRounds = new ArrayList<ArrayList<String[]>>();

        allRounds = statsDB.getAllRounds();

        int previousRound = 0;
        for (ArrayList<String[]> round : allRounds){
            numRounds++;
            for (String[] entry : round){
                if (entry[1].equalsIgnoreCase(profileBar.currentProfileName)){
                    //accumulate score
                    scoreForAllRounds += Integer.parseInt(entry[5]);

                    //get 5 previous rounds scores
                    if (previousRound == 4) {
                        roundFive += Integer.parseInt(entry[5]);
                    }else  if (previousRound == 3) {
                        roundFour += Integer.parseInt(entry[5]);
                    }else  if (previousRound == 2) {
                        roundThree += Integer.parseInt(entry[5]);
                    }else  if (previousRound == 1) {
                        roundTwo += Integer.parseInt(entry[5]);
                    }else  if (previousRound == 0) {
                        roundOne += Integer.parseInt(entry[5]);
                    }

                    //check for par, bogie, birdie
                    if (Integer.parseInt(entry[5]) == Integer.parseInt(entry[3])) {
                        totalPars += 1;
                        totalStrokes++;
                    }else if (Integer.parseInt(entry[5]) == Integer.parseInt(entry[3]) + 1) {
                        totalBogies += 1;
                        totalStrokes++;
                    }else if (Integer.parseInt(entry[5]) == Integer.parseInt(entry[3]) - 1) {
                        totalBirdies += 1;
                        totalStrokes++;
                    }

                    //accumulate greens hits
                    if (entry[8].equalsIgnoreCase("Left")) {
                        totalLeftFairways += 1;
                        totalFairwayHits++;
                    }else if (entry[8].equalsIgnoreCase("Right")) {
                        totalRightFairways += 1;
                        totalFairwayHits++;
                    }else if (entry[8].equalsIgnoreCase("Center")) {
                        totalCenterFairways += 1;
                        totalFairwayHits++;
                    }

                    //accumulate fairway hits
                    if (entry[7].equalsIgnoreCase("Left")) {
                        totalLeftGreens += 1;
                        totalGreensHits++;
                    }else if (entry[7].equalsIgnoreCase("Right")) {
                        totalRightGreens += 1;
                        totalGreensHits++;
                    }else if (entry[7].equalsIgnoreCase("Center")) {
                        totalCenterGreens += 1;
                        totalGreensHits++;
                    }
                }
            }
            previousRound++;
        }

        //check for any data, if not set values to stop divid by zero
        if  (numRounds == 0){
            totalGreensHits = 1;
            totalFairwayHits = 1;
            totalStrokes = 1;
            numRounds = 1;
        }

        grabViews();
    }

    private void grabViews() {
        pieWheelGLView = (PieWheelGLSurfaceView) findViewById(R.id.pieWheel);
        circlesGraphGLView = (CirclesGraphGLSurfaceView) findViewById(R.id.circles);
        barGraphGLView = (BarGraphGLSurfaceView) findViewById(R.id.bars);
        plotsGraphGLView = (PlotsGraphGLSurfaceView) findViewById(R.id.plots);

        scrollView = (ScrollView) findViewById(R.id.scrollView2);

        if (!didAnimatePieWheel){
            didAnimatePieWheel = true;
            averageScoreLabel = (TextView) findViewById(R.id.averageScoreLabel);
            labelCounterAnimation(averageScoreLabel, (scoreForAllRounds / numRounds));
            pieWheelGLView.animatePieWheel((float)(totalBirdies /(float)totalStrokes),
                ((float)totalPars /(float)totalStrokes), ((float)totalBogies /(float)totalStrokes));
        }

        leftGreensLabel = (TextView) findViewById(R.id.leftGreensLabel);
        centerGreensLabel = (TextView) findViewById(R.id.centerGreensLabel);
        rightGreensLabel = (TextView) findViewById(R.id.rightGreensLabel);

        leftFairwayLabel = (TextView) findViewById(R.id.leftFairwayLabel);
        rightFairwayLabel = (TextView) findViewById(R.id.rightFairwayLabel);
        centerFairwayLabel = (TextView) findViewById(R.id.centerFairwayLabel);

        firstPreviousRoundLabel = (TextView) findViewById(R.id.firstPreviousRoundLabel);
        secondPreviousRoundLabel = (TextView) findViewById(R.id.secondPreviousRoundLabel);
        thirdPreviousRoundLabel = (TextView) findViewById(R.id.thirdPreviousRoundLabel);
        fourthPreviousRoundLabel = (TextView) findViewById(R.id.fourthPreviousRoundLabel);
        fifthPreviousRoundLabel = (TextView) findViewById(R.id.fifthPreviousRoundLabel);

        setupScrollView();
    }

    private void setupScrollView(){
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                int scrollY = scrollView.getScrollY(); //for verticalScrollView
                int totalHeight = scrollView.getChildAt(0).getHeight();
                int graphHeight = totalHeight/4;

                if (scrollY >= (graphHeight)){
                    if (!didAnimateCircles) {
                        labelCounterAnimation(leftGreensLabel, Math.round(totalLeftGreens / totalGreensHits * 100));
                        labelCounterAnimation(centerGreensLabel, Math.round(totalCenterGreens/totalGreensHits * 100));
                        labelCounterAnimation(rightGreensLabel, Math.round(totalRightGreens / totalGreensHits * 100));

                        float leftGreen = totalLeftGreens/totalGreensHits;
                        float centerGreen = totalCenterGreens/totalGreensHits;
                        float rightGreen = totalRightGreens/totalGreensHits;

                        circlesGraphGLView.animateCircles(leftGreen,
                                centerGreen,rightGreen);
                        animateCircleLabels(Math.round(leftGreen*100),Math.round(rightGreen*100));
                        didAnimateCircles = true;
                    }
                }
                if (scrollY >= (graphHeight * 2)){
                    if (!didAnimateBars) {
                        labelCounterAnimation(leftFairwayLabel, Math.round(totalLeftFairways / totalFairwayHits * 100));
                        labelCounterAnimation(centerFairwayLabel, Math.round(totalCenterFairways / totalFairwayHits * 100));
                        labelCounterAnimation(rightFairwayLabel, Math.round(totalRightFairways / totalFairwayHits * 100));

                        int leftFairway =  Math.round(170 * totalLeftFairways/totalFairwayHits) + 10;
                        int rightFairway =  Math.round(170 * totalRightFairways/totalFairwayHits) + 10;
                        int centerFairway =  Math.round(170 * totalCenterFairways/totalFairwayHits) + 10;

                        animateBarLabels(leftFairway,centerFairway,rightFairway);
                        barGraphGLView.animateBars(leftFairway,centerFairway,rightFairway);
                        didAnimateBars = true;
                    }
                }
                if (scrollY >= (graphHeight * 3) - 600){
                    if (!didAnimatePlots) {
                        labelCounterAnimation(firstPreviousRoundLabel, roundOne);
                        labelCounterAnimation(secondPreviousRoundLabel, roundTwo);
                        labelCounterAnimation(thirdPreviousRoundLabel, roundThree);
                        labelCounterAnimation(fourthPreviousRoundLabel, roundFour);
                        labelCounterAnimation(fifthPreviousRoundLabel, roundFive);

                        // -60 to 60 to stay in frame
                        int offset = -60;
                        int firstRoundFinalYPos = Math.round(roundOne / 1.5f) + offset;
                        int secondRoundFinalYPos = Math.round(roundTwo / 1.5f) + offset;
                        int thirdRoundFinalYPos = Math.round(roundThree / 1.5f) + offset;
                        int fourthRoundFinalYPos = Math.round(roundFour / 1.5f) + offset;
                        int fifthRoundFinalYPos = Math.round(roundFive / 1.5f) + offset;

                        animatePlotLabels(firstRoundFinalYPos, secondRoundFinalYPos, thirdRoundFinalYPos, fourthRoundFinalYPos, fifthRoundFinalYPos);
                        plotsGraphGLView.animatePlots(firstRoundFinalYPos, secondRoundFinalYPos, thirdRoundFinalYPos, fourthRoundFinalYPos, fifthRoundFinalYPos);
                        didAnimatePlots = true;
                    }
                }
            }
        });
    }
}

