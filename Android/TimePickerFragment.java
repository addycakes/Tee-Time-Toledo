package com.adamwilson.golf;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.adamwilson.golf.TeeTime.TeeTimeActivity;

import java.util.Calendar;

/**
 * Created by adam on 9/13/15.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        TeeTimeActivity teeTimeActivity = (TeeTimeActivity) getActivity();
        teeTimeActivity.selectedTime[0] = hourOfDay;
        teeTimeActivity.selectedTime[1] = minute;

        int time = hourOfDay;
        String period = " AM";
        if (hourOfDay > 13){
            time = hourOfDay % 12;
            period = " PM";
        }

        String timeString = Integer.toString(time) + ":" + ("00" + Integer.toString(minute)).substring(Integer.toString(minute).length()) + period;
        teeTimeActivity.setTime(timeString);

        //teeTimeActivity.timePickerButton.setText(timeString);
        //teeTimeActivity.scheduleButton.setEnabled(true);

    }
}