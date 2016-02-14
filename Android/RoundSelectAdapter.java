package com.adamwilson.golf;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adam on 9/16/15.
 */
public class RoundSelectAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private CourseSelectActivity context;
    private String packageName;

    public RoundSelectAdapter(CourseSelectActivity context, ArrayList<ArrayList<String[]>> allRounds) {

        for (ArrayList<String[]> round : allRounds){
            String[] entry = round.get(0);
            String roundName = entry[9];
            list.add(roundName);
        }
        this.context = context;
        this.packageName = context.getPackageName();
    }

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
            view = inflater.inflate(R.layout.course_listview_item, parent, false);
        }

        //Handle TextView and display string from your list
        final TextView courseName = (TextView) view.findViewById(R.id.course_name_label);
        final ImageView courseInfo = (ImageView) view.findViewById(R.id.course_cell_info);
        final ImageView courseImage = (ImageView) view.findViewById(R.id.course_cell_image);
        final View shadowBar = (View) view.findViewById(R.id.shadow_bar);

        String roundName = list.get(position);
        String nameForImage = roundName.substring(0, roundName.length() - 10);
        String date = roundName.substring(roundName.length() - 10, roundName.length());
        String nameForList = getFormatNameForList(roundName);

        courseName.setText(nameForList);

        String imageUri = "android.resource://" + packageName + "/drawable/" +
                nameForImage.toLowerCase() + "_cell_bg";
        courseImage.setImageURI(Uri.parse(imageUri));
        courseInfo.setImageResource(R.drawable.course_cell_info);

        Rect bounds = new Rect();
        Paint textPaint = courseName.getPaint();
        textPaint.getTextBounds(courseName.getText().toString(), 0, courseName.getText().toString().length(), bounds);
        int height = bounds.height() + 20;
        int width = bounds.width() + 50;

        ViewGroup.LayoutParams layoutParams = shadowBar.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        shadowBar.setLayoutParams(layoutParams);

        return view;
    }

    private String getFormatNameForList(String roundName){

        //add space before hour
        String formattedName = roundName.substring(0,roundName.length() - 2) + " " + roundName.substring(roundName.length() - 2,roundName.length());

        //add slash before month
        formattedName = formattedName.substring(0,formattedName.length() - 5) + "/" + formattedName.substring(formattedName.length() - 5,formattedName.length());

        //add slash before day
        formattedName = formattedName.substring(0,formattedName.length() - 8) + "/" + formattedName.substring(formattedName.length() - 8,formattedName.length());

        //add space before year
        formattedName = formattedName.substring(0,formattedName.length() - 13) + " " + formattedName.substring(formattedName.length() - 13,formattedName.length());

        return formattedName;
    }
}
