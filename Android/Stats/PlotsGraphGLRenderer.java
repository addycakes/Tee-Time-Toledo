package com.adamwilson.golf.Stats;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.DisplayMetrics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by adam on 7/6/15.
 */
public class PlotsGraphGLRenderer implements GLSurfaceView.Renderer {
    public boolean shouldAnimate = false;
    int density;

    private CircleShape firstPreviousRound;
    private CircleShape secondPreviousRound;
    private CircleShape thirdPreviousRound;
    private CircleShape fourthPreviousRound;
    private CircleShape fifthPreviousRound;
    private CircleShape firstPreviousRoundShadow;
    private CircleShape secondPreviousRoundShadow;
    private CircleShape thirdPreviousRoundShadow;
    private CircleShape fourthPreviousRoundShadow;
    private CircleShape fifthPreviousRoundShadow;

    private DottedLine fifthFourthLine = null;
    private DottedLine fourthThirdLine = null;
    private DottedLine thirdSecondLine = null;
    private DottedLine secondFirstLine = null;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mTranslationMatrixFirstRound = new float[16];
    private float[] mTranslationMatrixSecondRound = new float[16];
    private float[] mTranslationMatrixThirdRound = new float[16];
    private float[] mTranslationMatrixFourthRound = new float[16];
    private float[] mTranslationMatrixFifthRound = new float[16];

    private int firstRoundYPos = 0;
    private int secondRoundYPos = 0;
    private int thirdRoundYPos = 0;
    private int fourthRoundYPos = 0;
    private int fifthRoundYPos = 0;

    private int TIME_COUNTER = 0;
    private final int PAUSE_TIME = 30;

    public int firstRoundFinalYPos;
    public int secondRoundFinalYPos;
    public int thirdRoundFinalYPos;
    public int fourthRoundFinalYPos;
    public int fifthRoundFinalYPos;

    @Override
    public void onDrawFrame(GL10 unused){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mTranslationMatrixFirstRound, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mTranslationMatrixSecondRound, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mTranslationMatrixThirdRound, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mTranslationMatrixFourthRound, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mTranslationMatrixFifthRound, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        if (shouldAnimate){

            if (firstRoundYPos < firstRoundFinalYPos){
                firstRoundYPos++;
            }else if (firstRoundYPos > firstRoundFinalYPos){
                firstRoundYPos--;
            }
            if (secondRoundYPos < secondRoundFinalYPos){
                secondRoundYPos++;
            }else if (secondRoundYPos > secondRoundFinalYPos){
                secondRoundYPos--;
            }
            if (thirdRoundYPos < thirdRoundFinalYPos){
                thirdRoundYPos++;
            }else if (thirdRoundYPos > thirdRoundFinalYPos){
                thirdRoundYPos--;
            }
            if (fourthRoundYPos < fourthRoundFinalYPos){
                fourthRoundYPos++;
            }else if (fourthRoundYPos > fourthRoundFinalYPos){
                fourthRoundYPos--;
            }
            if (fifthRoundYPos < fifthRoundFinalYPos){
                fifthRoundYPos++;
            }else if (fifthRoundYPos > fifthRoundFinalYPos){
                fifthRoundYPos--;
            }

            Matrix.translateM(mTranslationMatrixFirstRound, 0, 0, (firstRoundYPos / 100.0f), 0);
            Matrix.translateM(mTranslationMatrixSecondRound, 0, 0, (secondRoundYPos / 100.0f), 0);
            Matrix.translateM(mTranslationMatrixThirdRound, 0, 0, (thirdRoundYPos / 100.0f), 0);
            Matrix.translateM(mTranslationMatrixFourthRound, 0, 0, (fourthRoundYPos / 100.0f), 0);
            Matrix.translateM(mTranslationMatrixFifthRound, 0, 0, (fifthRoundYPos / 100.0f), 0);

            //all circles in final positions
            if ((fourthRoundYPos == fourthRoundFinalYPos) &&
                (thirdRoundYPos == thirdRoundFinalYPos)   &&
                (secondRoundYPos == secondRoundFinalYPos) &&
                (firstRoundYPos == firstRoundFinalYPos)) {

                //increment TIME_COUNTER to add time between line drawings
                if (fifthFourthLine == null) {
                    fifthFourthLine = new DottedLine(.6f, (fifthRoundFinalYPos / 100.0f), .3f, (fourthRoundFinalYPos / 100.0f));
                }else {
                    fifthFourthLine.draw(mMVPMatrix);
                }
                if (TIME_COUNTER > PAUSE_TIME) {
                    if (fourthThirdLine == null) {
                        fourthThirdLine = new DottedLine(.3f, (fourthRoundFinalYPos / 100.0f), 0f, (thirdRoundFinalYPos / 100.0f));
                    }else{
                        fourthThirdLine.draw(mMVPMatrix);
                    }
                }
                if (TIME_COUNTER > PAUSE_TIME*2) {
                    if (thirdSecondLine == null) {
                        thirdSecondLine = new DottedLine(0f, (thirdRoundFinalYPos / 100.0f), -.3f, (secondRoundFinalYPos / 100.0f));
                    }else{
                        thirdSecondLine.draw(mMVPMatrix);
                    }
                }
                if(TIME_COUNTER > PAUSE_TIME*3) {
                    if (secondFirstLine == null ) {
                        secondFirstLine = new DottedLine(-.3f, (secondRoundFinalYPos / 100.0f), -.6f, (firstRoundFinalYPos / 100.0f));
                    }else{
                        secondFirstLine.draw(mMVPMatrix);
                    }
                }
                TIME_COUNTER++;
            }
        }


        firstPreviousRoundShadow.draw(mTranslationMatrixFirstRound);
        secondPreviousRoundShadow.draw(mTranslationMatrixSecondRound);
        thirdPreviousRoundShadow.draw(mTranslationMatrixThirdRound);
        fourthPreviousRoundShadow.draw(mTranslationMatrixFourthRound);
        fifthPreviousRoundShadow.draw(mTranslationMatrixFifthRound);

        firstPreviousRound.draw(mTranslationMatrixFirstRound);
        secondPreviousRound.draw(mTranslationMatrixSecondRound);
        thirdPreviousRound.draw(mTranslationMatrixThirdRound);
        fourthPreviousRound.draw(mTranslationMatrixFourthRound);
        fifthPreviousRound.draw(mTranslationMatrixFifthRound);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.82f, 0.84f, 0.85f, 1.0f);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        float colorBlue[] = { .14f, .35f, .48f, 1.0f };
        float colorShadow[] = { 0f, 0f, 0f, 0.7f };

        float firstCircleCoords[] = {-.6f, 0f, 0f};
        float secondCircleCoords[] = {-.3f, 0f, 0f};
        float thirdCircleCoords[] = {0f, 0f, 0f};
        float fourthCircleCoords[] = {.3f, 0f, 0f};
        float fifthCircleCoords[] = {.6f, 0f, 0f};

        float firstCircleShadowCoords[] = {-.58f, -.02f, 0f};
        float secondCircleShadowCoords[] = {-.28f, -.02f, 0f};
        float thirdCircleShadowCoords[] = {.02f, -.02f, 0f};
        float fourthCircleShadowCoords[] = {.32f, -.02f, 0f};
        float fifthCircleShadowCoords[] = {.62f, -.02f, 0f};

        float radius = .1f;

        firstPreviousRound = new CircleShape(radius,colorBlue,firstCircleCoords);
        secondPreviousRound = new CircleShape(radius,colorBlue,secondCircleCoords);
        thirdPreviousRound = new CircleShape(radius,colorBlue,thirdCircleCoords);
        fourthPreviousRound = new CircleShape(radius,colorBlue,fourthCircleCoords);
        fifthPreviousRound = new CircleShape(radius,colorBlue,fifthCircleCoords);

        firstPreviousRoundShadow = new CircleShape(radius,colorShadow,firstCircleShadowCoords);
        secondPreviousRoundShadow = new CircleShape(radius,colorShadow,secondCircleShadowCoords);
        thirdPreviousRoundShadow = new CircleShape(radius,colorShadow,thirdCircleShadowCoords);
        fourthPreviousRoundShadow = new CircleShape(radius,colorShadow,fourthCircleShadowCoords);
        fifthPreviousRoundShadow = new CircleShape(radius,colorShadow,fifthCircleShadowCoords);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}