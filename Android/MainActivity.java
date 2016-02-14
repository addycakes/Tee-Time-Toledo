package com.adamwilson.golf;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.adamwilson.golf.Stats.StatsActivity;
import com.adamwilson.golf.TeeTime.TeeTimeActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity{

    private ProfileBar profileBar = null;
    public boolean isCreatingProfile = false;
    private RelativeLayout mainLayout;
    private Button courseSelectBTN;
    private Button teeTimeBTN;
    private Button statsBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        courseSelectBTN = (Button) findViewById(R.id.button3);
        teeTimeBTN = (Button) findViewById(R.id.button);
        statsBTN = (Button) findViewById(R.id.button2);

    }

    @Override
    protected void onResume(){
        super.onResume();
        if (profileBar == null) {
            profileBar = ProfileBar.getProfileBar(this);
            profileBar.shouldShowBackButton = false;
            profileBar.areSettingsAvailable = true;
            profileBar.setupProfileBar(getSupportActionBar(), this);
        }

        if (isCreatingProfile) {
            profileBar.newProfilePicURI = imageUri;
            profileBar.createNewProfile();
            isCreatingProfile = false;
        }

        mainLayout.setBackgroundResource(R.drawable.main_background);
        statsBTN.setBackgroundResource(R.drawable.stats_button);
        teeTimeBTN.setBackgroundResource(R.drawable.tee_time_button);
        courseSelectBTN.setBackgroundResource(R.drawable.courses_button);

    }

    @Override
    protected  void onPause(){
        super.onPause();
        if (!isCreatingProfile) {
            profileBar = null;
        }

        mainLayout.setBackgroundResource(0);
        statsBTN.setBackgroundResource(0);
        teeTimeBTN.setBackgroundResource(0);
        courseSelectBTN.setBackgroundResource(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create_profile){
            profileBar.createNewProfile();
            return true;
        }
        if (id == R.id.action_change_profile) {
            profileBar.changeProfile();
            return true;
        }
        if (id == R.id.action_delete_profile) {
            profileBar.deleteProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Send button */
    public void showCourseSelect(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, CourseSelectActivity.class);
        intent.putExtra("com.adamwilson.golf.isSelectingForPlay",true);
        intent.putExtra("com.adamwilson.golf.optionalKey","");
        startActivity(intent);

    }
    public void showTeeTime(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, TeeTimeActivity.class);
        startActivity(intent);

    }
    public void showStats(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);

    }

    //handle profile pic selection from profile bar
    //TODO: fit into profilebar class
    private Uri imageUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 3) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HH").format(new Date());
            String imageFileName = "golf_" + timeStamp + "_";
            File image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(), imageFileName);
            imageUri = Uri.fromFile(image);
        }else if (requestCode == 4) {
            if (data != null) {
                imageUri = data.getData();
            }else{
                imageUri = null;
            }
        }else{
            imageUri = null;
        }
    }
}
