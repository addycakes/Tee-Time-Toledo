package com.adamwilson.golf;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adamwilson.golf.DataModel.GolfDB;

import java.util.ArrayList;

/**
 * Created by adam on 7/31/15.
 */
public class CourseSelectAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private CourseSelectActivity context;
    private String packageName;

    public CourseSelectAdapter(CourseSelectActivity context, ArrayList<String> courses) {
        this.list = courses;
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

        courseName.setText(list.get(position));

        String imageUri = "android.resource://" + packageName + "/drawable/" +
                courseName.getText().toString().toLowerCase() + "_cell_bg";
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
}
