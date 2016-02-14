package com.adamwilson.golf.Stats;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by adam on 7/6/15.
 */
public class PieWheelGLSurfaceView extends GLSurfaceView {

    public PieWheelGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);
    }

    public void animatePieWheel(float birdies, float pars, float bogies){
        PieWheelGLRenderer renderer = new PieWheelGLRenderer();
        renderer.birdiePercent = birdies;
        renderer.parPercent = pars;
        renderer.bogiePercent = bogies;

        setRenderer(renderer);
    }

}