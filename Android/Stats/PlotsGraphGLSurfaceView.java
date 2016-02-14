package com.adamwilson.golf.Stats;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by adam on 7/6/15.
 */
public class PlotsGraphGLSurfaceView extends GLSurfaceView{
    PlotsGraphGLRenderer renderer = new PlotsGraphGLRenderer();


    public PlotsGraphGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(renderer);
    }

    public void animatePlots(int roundOne, int roundTwo, int roundThree, int roundFour, int roundFive){
        renderer.firstRoundFinalYPos = roundOne;
        renderer.secondRoundFinalYPos = roundTwo;
        renderer.thirdRoundFinalYPos = roundThree;
        renderer.fourthRoundFinalYPos = roundFour;
        renderer.fifthRoundFinalYPos = roundFive;

        renderer.shouldAnimate = true;
    }
}