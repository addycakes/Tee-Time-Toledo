package com.adamwilson.golf.Stats;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by adam on 7/6/15.
 */
public class CirclesGraphGLSurfaceView extends GLSurfaceView {
    CirclesGraphGLRenderer renderer = new CirclesGraphGLRenderer();

    public CirclesGraphGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);
        setRenderer(renderer);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    public void animateCircles(float leftGreen, float centerGreen, float rightGreen){

        renderer.centerCirclePercent = centerGreen;
        renderer.rightCirclePercent = rightGreen;
        renderer.leftCirclePercent = leftGreen;

        renderer.shouldAnimate = true;
    }
}