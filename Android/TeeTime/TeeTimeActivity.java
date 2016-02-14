package com.adamwilson.golf.TeeTime;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.adamwilson.golf.CourseSelectActivity;
import com.adamwilson.golf.NotificationPublisher;
import com.adamwilson.golf.ProfileBar;
import com.adamwilson.golf.R;
import com.adamwilson.golf.Stats.StatsActivity;
import com.adamwilson.golf.TimePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class TeeTimeActivity extends ActionBarActivity {
    private ArrayList<String> allGolfers;
    private ArrayAdapter<String> golferAdapter;
    private CalendarView calendar;
    private Button cartButton;
    private Button coursesButton;
    private Button scheduleButton;
    public Button timePickerButton;
    private ProfileBar profileBar;
    private TeeTime teeTime;
    public static String course = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tee_time);

        cartButton = (Button) findViewById(R.id.cartsButton);
        coursesButton = (Button) findViewById(R.id.courseButton);
        scheduleButton = (Button) findViewById(R.id.scheduleButton);
        timePickerButton = (Button) findViewById(R.id.timePickerButton);

    }

    @Override
    protected void onResume(){
        super.onResume();
        profileBar = ProfileBar.getProfileBar(this);
        profileBar.areSettingsAvailable = false;
        profileBar.shouldShowBackButton = true;
        profileBar.setupProfileBar(getSupportActionBar(), this);

        if (!course.equalsIgnoreCase("")){
            timePickerButton.setEnabled(true);
            setCourse(course);
        }else{
            timePickerButton.setEnabled(false);
        }

        if (allGolfers == null){
            setupGolfersListView();
        }

        if (calendar == null){
            setupCalendar();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        profileBar = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tee_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void addCart(View view) {
        int numCarts = Integer.parseInt(cartButton.getText().toString());

        if (numCarts < 4){
            numCarts++;
        }else{
            numCarts = 0;
        }

        cartButton.setText(Integer.toString(numCarts));
    }

    private void setupGolfersListView() {

        allGolfers = new ArrayList<String>();
        allGolfers.add(profileBar.currentProfileName);
        golferAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, allGolfers);

        final ListView listView = (ListView) findViewById(R.id.golfersList);
        listView.setAdapter(golferAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //add golfer
                int index = listView.getPositionForView(view);
                System.out.println(index);
                if (index != 1) {
                    allGolfers.remove(index);
                    golferAdapter.notifyDataSetChanged();
                } else {
                    addGolfer();
                }
            }
        });

        TextView header = new TextView(this);
        header.setTextSize(13);
        header.setTextColor(getResources().getColor(R.color.black));
        header.setText("GOLFERS");
        listView.addHeaderView(header);
    }

    public void addGolfer(){
        //display textfield for user
        final EditText enterGolferName = new EditText(this);

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.teetime_layout);

        RelativeLayout.LayoutParams r_layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        r_layout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        r_layout.addRule(RelativeLayout.BELOW, R.id.timePickerButton);
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
                        allGolfers.add(golferName);
                        golferAdapter.notifyDataSetChanged();
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

    private int[] selectedDate = {0,0,0};
    private void setupCalendar() {
        calendar = (CalendarView) findViewById(R.id.calendarView);
        calendar.setFirstDayOfWeek(1);
        calendar.setShowWeekNumber(false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        selectedDate[0] = Integer.parseInt(sdf.format(new Date(calendar.getDate())));
        sdf = new SimpleDateFormat("MM");
        selectedDate[1] = Integer.parseInt(sdf.format(new Date(calendar.getDate())));
        sdf = new SimpleDateFormat("dd");
        selectedDate[2] = Integer.parseInt(sdf.format(new Date(calendar.getDate())));

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                System.out.println("selected: " + year + "/" + month + "/" + day);
                selectedDate[0] = year;
                selectedDate[1] = month + 1;
                selectedDate[2] = day;
            }
        });
    }

    public void showCourses(View view) {
        Intent intent = new Intent(this, CourseSelectActivity.class);
        intent.putExtra("com.adamwilson.golf.isSelectingForPlay",false);
        intent.putExtra("com.adamwilson.golf.optionalKey","TeeTime");
        startActivity(intent);
    }

    public void setCourse(String courseName){
        coursesButton.setText(courseName);

        TeeTimeFactory factory = new TeeTimeFactory();
        teeTime = factory.createTeeTime(courseName);
        teeTime.courseName = courseName;
        teeTime.date = selectedDate;
    }

    public int[] selectedTime = {0,0};
    public void showTimes(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void setTime(String time){
        timePickerButton.setText(time);
        scheduleButton.setEnabled(true);
        teeTime.time = selectedTime;
    }

    public void scheduleTeeTime(View view) {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);

        if ((selectedDate[0] != 0) && (selectedTime[0] != 0)) {

            Date today = new Date();
            Date teeTimeDate = null;

            String dateString = Integer.toString(selectedDate[0]) +  ("00" + Integer.toString(selectedDate[1])).substring(Integer.toString(selectedDate[1]).length()) +
                    ("00" + Integer.toString(selectedDate[2])).substring(Integer.toString(selectedDate[2]).length()) +
                    ("00" + Integer.toString(selectedTime[0])).substring(Integer.toString(selectedTime[0]).length()) +
                    ("00" + Integer.toString(selectedTime[1])).substring(Integer.toString(selectedTime[1]).length());
            System.out.println(dateString);
            try {
                teeTimeDate = format.parse(dateString);
            }catch (ParseException exception){
                System.out.println("failed");
            }
            System.out.println(teeTimeDate.getTime());
            System.out.println(today.getTime());

            if (teeTimeDate != null) {
                long threeMin = 1000 * 60 * 5;
                long difference = teeTimeDate.getTime() - today.getTime() - threeMin;

                Intent intent = new Intent(this, CourseSelectActivity.class);
                intent.putExtra("selected_course", course);
                intent.putExtra("entered_golfers", allGolfers);
                intent.putExtra("com.adamwilson.golf.optionalKey","RoundReady");

                //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(CourseSelectActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(intent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                Notification notification = new Notification.Builder(this)
                        .setContentTitle("Tee Time Reminder")
                        .setContentText("5 minutes")
                        .setSmallIcon(R.mipmap.golficon180)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        //.setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent)
                        .build();

                System.out.println("scheduled " + difference);
                scheduleNotification(notification, (int) difference);

                teeTime.submit();
            }
        }
    }

    private void scheduleNotification(Notification notification, int timeUntilGolf) {

        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + timeUntilGolf;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}
