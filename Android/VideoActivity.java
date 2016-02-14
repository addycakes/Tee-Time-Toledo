package com.adamwilson.golf;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by adam on 8/20/15.
 */
public class VideoActivity extends Activity{
    private VideoView videoView;
    private MediaController mediaController;
    public String videoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);

        videoView = (VideoView) findViewById(R.id.videoView);

        if (mediaController == null) {
            mediaController = new MediaController(this);
        }

        videoView.setMediaController(mediaController);
        videoView.setZOrderOnTop(true);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.adam_shot));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
                android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
                params.width =  metrics.widthPixels;
                params.height = metrics.heightPixels;
                params.leftMargin = 0;
                videoView.setLayoutParams(params);
                mediaController.show();
                return true;
            }
        });
        /*
        LinearLayout mediaLayout = new LinearLayout(this);
        LinearLayout.LayoutParams mediaParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mediaParams.gravity = Gravity.CENTER;
        mediaLayout.setOrientation(LinearLayout.VERTICAL);
        mediaLayout.setLayoutParams(mediaParams);
        mediaLayout.setBackgroundColor(getResources().getColor(R.color.lt_gray));
        mediaLayout.setPaddingRelative(8, 8, 8, 8);
        mediaLayout.addView(videoView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        final Dialog moviePlayer = new Dialog(this);
        moviePlayer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moviePlayer.setContentView(mediaLayout);
        moviePlayer.show();
        */
        videoView.requestFocus();



    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
