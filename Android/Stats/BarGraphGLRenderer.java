package com.adamwilson.golf.Stats;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by adam on 7/6/15.
 */
public class BarGraphGLRenderer implements GLSurfaceView.Renderer {
    public boolean shouldAnimate = false;
    int density;

    private BarShape leftFairwayBar;
    private BarShape centerFairwayBar;
    private BarShape rightFairwayBar;
    private BarShape leftFairwayBarShadow;
    private BarShape centerFairwayBarShadow;
    private BarShape rightFairwayBarShadow;

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mScaleMatrixLeftBar = new float[16];
    private float[] mScaleMatrixCenterBar = new float[16];
    private float[] mScaleMatrixRightBar = new float[16];

    private int[] leftBarCurrentScale = {100, 100};
    private int[] rightBarCurrentScale = {100, 100};
    private int[] centerBarCurrentScale = {100, 100};
    private int scaleFactor = 1;


    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.82f, 0.84f, 0.85f, 1.0f);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        float rightBarCoords[] = {
            -0.8f,  .5f, 0.0f,   // top left
            -0.8f, 0f, 0.0f,   // bottom left
            -0.4f, 0f, 0.0f,   // bottom right
            -0.4f,  .5f, 0.0f }; // top right
        float centerBarCoords[] = {
            -0.2f,  .5f, 0.0f,   // top left
            -0.2f, 0f, 0.0f,   // bottom left
            0.2f, 0f, 0.0f,   // bottom right
            0.2f,  .5f, 0.0f }; // top right
        float leftBarCoords[] = {
            0.4f,  .5f, 0.0f,   // top left
            0.4f, 0f, 0.0f,   // bottom left
            0.8f, 0f, 0.0f,   // bottom right
            0.8f,  .5f, 0.0f }; // top right
        float rightBarShadowCoords[] = {
            -0.77f,  0.47f, 0.0f,   // top left
            -0.77f, -.03f, 0.0f,   // bottom left
            -0.37f, -.03f, 0.0f,   // bottom right
            -0.37f,  0.47f, 0.0f }; // top right
        float centerBarShadowCoords[] = {
            -0.17f,  0.47f, 0.0f,   // top left
            -0.17f, -.03f, 0.0f,   // bottom left
            0.23f, -.03f, 0.0f,   // bottom right
            0.23f,  0.47f, 0.0f }; // top right
        float leftBarShadowCoords[] = {
            0.43f,  0.47f, 0.0f,   // top left
            0.43f, -.03f, 0.0f,   // bottom left
            0.83f, -.03f, 0.0f,   // bottom right
            0.83f,  0.47f, 0.0f }; // top right

        float colorYellow[] = { .83f, 0.65f, 0.16f, 1.0f };
        float colorGreen[] = { .33f, 0.47f, 0.25f, 1.0f };
        float colorBlue[] = { .14f, .35f, .48f, 1.0f };
        float colorShadow[] = { 0f, 0f, 0f, 0.7f };

        leftFairwayBar = new BarShape(colorBlue,leftBarCoords);
        centerFairwayBar = new BarShape(colorGreen,centerBarCoords);
        rightFairwayBar = new BarShape(colorYellow,rightBarCoords);
        leftFairwayBarShadow = new BarShape(colorShadow,leftBarShadowCoords);
        centerFairwayBarShadow = new BarShape(colorShadow,centerBarShadowCoords);
        rightFairwayBarShadow = new BarShape(colorShadow,rightBarShadowCoords);
    }

    public int leftBarScale;
    public int centerBarScale;
    public int rightBarScale;
    private float[] mTranslationMatrix = new float[16];
    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mScaleMatrixLeftBar, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mScaleMatrixLeftBar, 0, 0, -.6f, 0);

        Matrix.multiplyMM(mScaleMatrixCenterBar, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mScaleMatrixCenterBar, 0, 0, -.6f, 0);

        Matrix.multiplyMM(mScaleMatrixRightBar, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mScaleMatrixRightBar, 0, 0, -.6f, 0);

        if (shouldAnimate){
            if (rightBarCurrentScale[1] < rightBarScale){
                rightBarCurrentScale[1] += scaleFactor;
            }else if (rightBarCurrentScale[1] > rightBarScale){
                rightBarCurrentScale[1] = rightBarCurrentScale[1] - scaleFactor;
            }
            if (leftBarCurrentScale[1] < leftBarScale){
                leftBarCurrentScale[1] += scaleFactor;
            }else if (leftBarCurrentScale[1] > leftBarScale){
                leftBarCurrentScale[1] = leftBarCurrentScale[1] - scaleFactor;
            }
            if (centerBarCurrentScale[1] < centerBarScale){
                centerBarCurrentScale[1] += scaleFactor;
            }else if (centerBarCurrentScale[1] > centerBarScale){
                centerBarCurrentScale[1] = centerBarCurrentScale[1] - scaleFactor;
            }

            Matrix.scaleM(mScaleMatrixCenterBar, 0, (centerBarCurrentScale[0]/100.0f), (centerBarCurrentScale[1]/100.0f), 0);
            Matrix.scaleM(mScaleMatrixLeftBar, 0, (leftBarCurrentScale[0]/100.0f), (leftBarCurrentScale[1]/100.0f), 0);
            Matrix.scaleM(mScaleMatrixRightBar, 0, (rightBarCurrentScale[0]/100.0f), (rightBarCurrentScale[1]/100.0f), 0);
        }

        leftFairwayBarShadow.draw(mScaleMatrixLeftBar);
        centerFairwayBarShadow.draw(mScaleMatrixCenterBar);
        rightFairwayBarShadow.draw(mScaleMatrixRightBar);

        leftFairwayBar.draw(mScaleMatrixLeftBar);
        centerFairwayBar.draw(mScaleMatrixCenterBar);
        rightFairwayBar.draw(mScaleMatrixRightBar);
    }

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

