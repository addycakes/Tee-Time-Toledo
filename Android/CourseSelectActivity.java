package com.adamwilson.golf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adamwilson.golf.DataModel.GolfDB;
import com.adamwilson.golf.TeeTime.TeeTimeActivity;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import xmlwise.Plist;

public class CourseSelectActivity extends ActionBarActivity{
    private GolfDB roundsDB;
    public static boolean isContinuing;

    private ProfileBar profileBar;
    private Intent intent;
    private Context context;

    private ArrayList<String> allCourses;
    private ArrayList<String> allGolfers = new ArrayList<String>();
    private ArrayList<ArrayList<String[]>> allRounds;
    private Map<String, String[]> allMapsForCourses;
    public static Map<String, ArrayList<String[]>> allHolesForCourses;

    private boolean isSelectingForPlay;
    private boolean didJustComeFromPlay = false;
    private String optionalKey = "";
    public String selectedCourse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_select);
        isContinuing = true;

        roundsDB = GolfDB.getGolfDatabase(this);

        intent = getIntent();
        isSelectingForPlay = intent.getBooleanExtra("com.adamwilson.golf.isSelectingForPlay", true);
        optionalKey = intent.getStringExtra("com.adamwilson.golf.optionalKey");
        context = this;

        //get the list of course from courses.xml
        //getCoursesFromXML();
        getCoursesFromPlist();
    }

    @Override
    protected void onResume(){
        super.onResume();
        profileBar = ProfileBar.getProfileBar(this);
        profileBar.areSettingsAvailable = false;
        profileBar.shouldShowBackButton = true;
        profileBar.setupProfileBar(getSupportActionBar(), this);

        if (!isContinuing){
            //update player handicap
            profileBar.updateGolferHandicap();
            finish();
        }else{
            //set up listviews
            setupCoursesListView();

            if (!didJustComeFromPlay){
                isContinuing = false;
                didJustComeFromPlay = true;
            }
        }
        
        if (optionalKey.equalsIgnoreCase("RoundReady")){
            optionalKey = "";
            selectedCourse = intent.getStringExtra("selected_course");
            allGolfers = intent.getStringArrayListExtra("entered_golfers");

            View view = new View(this);
            startRound(view);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        profileBar = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course_select, menu);
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

    private RoundSelectAdapter roundAdapter;
    private void setupCoursesListView(){
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toggleCourseInfo(view);
            }
        });

        if (optionalKey.equalsIgnoreCase("Scorecard")){
            allRounds = roundsDB.getAllRounds();
            roundAdapter = new RoundSelectAdapter(this, allRounds);
            listView.setAdapter(roundAdapter);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Round?");
                    // Add the buttons
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            deleteRound(view);
                            allRounds.remove(position);
                            roundAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });
        }else{
            CourseSelectAdapter courseAdapter = new CourseSelectAdapter(this, allCourses);
            listView.setAdapter(courseAdapter);
        }
    }

    private void deleteRound(View view) {
        TextView courseName = (TextView) view.findViewById(R.id.course_name_label);
        //remove space from string
        String roundName = courseName.getText().toString().replace(" ", "");
        roundsDB.deleteRound(roundName);
    }

    private ImageView selectedBackground;
    private TextView selectedCourseName;
    private View selectedShadow;
    private void toggleCourseInfo(View view) {
        ImageView background = (ImageView) view.findViewById(R.id.course_cell_image);
        TextView courseName = (TextView) view.findViewById(R.id.course_name_label);
        View shadow = (View) view.findViewById(R.id.shadow_bar);
        Animation bg_animation;
        //if already selected, hide info.
        //else
        //show info
        if(optionalKey.equalsIgnoreCase("Scorecard")){
            Intent intent = new Intent(this, ScorecardActivity.class);

            //remove space from string
            String roundName = courseName.getText().toString().replace(" ","").replace("/","");
            ArrayList<String[]> allEntries = roundsDB.getAllEntriesForRound(roundName);
            selectedCourse = roundName.substring(0, roundName.length()-10);
            System.out.println(selectedCourse);
            intent.putExtra("com.adamwilson.golf.scorecard_round", allEntries);
            intent.putExtra("com.adamwilson.golf.scorecard_isPlaying", false);
            intent.putExtra("com.adamwilson.golf.scorecard_course",selectedCourse);
            startActivity(intent);
            isContinuing = false;
        }else {
            if (courseName.getText().toString().equalsIgnoreCase(selectedCourse)) {
                bg_animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,
                        (Animation.RELATIVE_TO_SELF + view.getHeight()), Animation.RELATIVE_TO_SELF);
                //selectedCourse = "";
                //goButton.setEnabled(false);
                if (isSelectingForPlay) {
                    startRound(courseName);
                } else {
                    if (optionalKey.equalsIgnoreCase("TeeTime")) {
                        TeeTimeActivity teeTimeActivity = (TeeTimeActivity) getParent();
                        TeeTimeActivity.course = selectedCourse;
                        finish();
                    }
                }
            } else {
                //if different selected, hide first
                if (!selectedCourse.equalsIgnoreCase("")) {
                    Animation return_animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,
                            (Animation.RELATIVE_TO_SELF + view.getHeight()), Animation.RELATIVE_TO_SELF);
                    return_animation.setDuration(800);
                    return_animation.setFillAfter(true);

                    selectedBackground.startAnimation(return_animation);
                    selectedCourseName.startAnimation(return_animation);
                    selectedShadow.startAnimation(return_animation);
                }

                bg_animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,
                        Animation.RELATIVE_TO_SELF, (Animation.RELATIVE_TO_SELF + background.getHeight()));

                selectedCourse = courseName.getText().toString();
                selectedBackground = background;
                selectedShadow = shadow;
                selectedCourseName = courseName;
            }
            bg_animation.setDuration(800);
            bg_animation.setFillAfter(true);

            background.startAnimation(bg_animation);
            courseName.startAnimation(bg_animation);
            shadow.startAnimation(bg_animation);
        }
    }

    //move to hole activity
    public void startRound(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, HoleActivity.class);

        //create new round
        if (!isContinuing) {
            roundsDB.createNewRound(selectedCourse);
            System.out.println("created");
        }

        if (!allGolfers.contains(profileBar.currentProfileName)){
            allGolfers.add(profileBar.currentProfileName);
        }

        intent.putExtra("com.adamwilson.golf.Golfers", allGolfers);
        intent.putExtra("com.adamwilson.golf.Course", selectedCourse);
        intent.putExtra("com.adamwilson.golf.Holes", allHolesForCourses.get(selectedCourse));
        intent.putExtra("com.adamwilson.golf.Map", allMapsForCourses.get(selectedCourse));

        startActivity(intent);
    }


    private void getCoursesFromPlist() {
        Map<String, Object> coursesPlist = null;
        allCourses = new ArrayList<String>();
        allMapsForCourses = new HashMap<String, String[]>();
        allHolesForCourses = new HashMap<String, ArrayList<String[]>>();

        try {
            InputStream inputStream =getResources().openRawResource(R.raw.courses);
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                coursesPlist = Plist.fromXml(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                br.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (String course : coursesPlist.keySet()){
            Map<String, Object> courseDict = (Map<String, Object>)coursesPlist.get(course);
            ArrayList<String[]> courseHoles = new ArrayList<String[]>();
            System.out.println(course);
            allCourses.add(course);

            for (String holeKey : courseDict.keySet()){
                Map<String, Object> holeDict = (Map<String, Object>)courseDict.get(holeKey);
                String[] hole = new String[13];
                System.out.println(holeDict.keySet());

                if (holeKey.equalsIgnoreCase("Map")){
                    String[] map = new String[5];
                    for (String item : holeDict.keySet()) {
                        if (item.equalsIgnoreCase("bottomLeftCoordinate")) {
                            String coordString = (String) holeDict.get(item);
                            String[] coord = coordString.split(",");
                            map[0] = coord[0]; //SWLAT
                            map[1] = coord[1]; //SWLNG
                        } else if (item.equalsIgnoreCase("topLeftCoordinate")) {
                            String[] coord = getNECoord(holeDict);
                            map[2] = coord[0]; //NELAT
                            map[3] = coord[1]; //NELNG
                        }else if (item.equalsIgnoreCase("imageName")){
                            map[4] = (String) holeDict.get(item);

                        }
                    }
                    allMapsForCourses.put(course, map);
                }else{
                    for (String item : holeDict.keySet()){
                        if (item.equalsIgnoreCase("Number")) {
                            hole[0] = (String)holeDict.get(item);
                        }else if (item.equalsIgnoreCase("Par")) {
                            hole[1] = (String)holeDict.get(item);
                        } else if (item.equalsIgnoreCase("Distance")) {
                            hole[2] = (String)holeDict.get(item);
                        } else if (item.equalsIgnoreCase("centerCoordinate")) {
                            String coordString = (String) holeDict.get(item);
                            String[] coord = coordString.split(",");
                            hole[3] = coord[0];
                            hole[4] = coord[1];
                        } else if (item.equalsIgnoreCase("bottomLeftCoordinate")) {
                            String coordString = (String) holeDict.get(item);
                            String[] coord = coordString.split(",");
                            hole[5] = coord[0];
                            hole[6] = coord[1];
                        } else if (item.equalsIgnoreCase("bottomRightCoordinate")) {
                            String[] coord = getNECoord(holeDict);
                            hole[7] = coord[0];
                            hole[8] = coord[1];
                        } else if (item.equalsIgnoreCase("ZOOM")) {
                            hole[9] = (String)holeDict.get(item);
                        } else if (item.equalsIgnoreCase("ROTATE")) {
                            hole[10] = (String)holeDict.get(item);
                        } else if (item.equalsIgnoreCase("Handicap")) {
                            hole[11] = (String) holeDict.get(item);
                        } else if (item.equalsIgnoreCase("videoURL")) {
                            hole[12] = (String)holeDict.get(item);
                        }
                    }
                    courseHoles.add(hole);
                }
            }
            Comparator comparator = new Comparator() {
                @Override
                public int compare(Object lhs, Object rhs) {
                    String[] objOne = (String[])lhs;
                    String[] objTwo = (String[])rhs;

                    int numOne = Integer.parseInt(objOne[0]);
                    int numTwo = Integer.parseInt(objTwo[0]);

                    if (numOne > numTwo){
                        return 1;
                    }else if (numOne < numTwo){
                        return -1;
                    }else{
                        return 0;
                    }
                }
            };

            Collections.sort(courseHoles, comparator);
            allHolesForCourses.put(course, courseHoles);
        }
    }

    private String[] getNECoord(Map<String, Object> dict){
        String nwCoordString = (String) dict.get("topLeftCoordinate");
        String seCoordString = (String) dict.get("bottomRightCoordinate");

        String[] nwCoord = nwCoordString.split(",");
        String[] seCoord = seCoordString.split(",");
        String[] neCoord = {nwCoord[0],seCoord[1]};

        return neCoord;
    }
}
