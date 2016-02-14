package com.adamwilson.golf.Stats;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by adam on 7/6/15.
 */
public class BarGraphGLSurfaceView extends GLSurfaceView {
    BarGraphGLRenderer renderer = new BarGraphGLRenderer();
    //public float leftBarLabelPosition = 0;
    //public float rightBarLabelPosition = 0;
    //public float centerBarLabelPosition = 0;

    public BarGraphGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);
        setRenderer(renderer);

    }
    public void animateBars(int leftFairway, int centerFairway, int rightFairway){
        renderer.centerBarScale = centerFairway;
        renderer.leftBarScale = leftFairway;
        renderer.rightBarScale = rightFairway;

        renderer.shouldAnimate = true;
    }

}