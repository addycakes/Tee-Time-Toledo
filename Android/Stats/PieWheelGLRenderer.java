package com.adamwilson.golf.Stats;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by adam on 7/6/15.
 */
public class PieWheelGLRenderer implements GLSurfaceView.Renderer {
    private PieWheelShape parPieWheel;
    private PieWheelShape birdiePieWheel;
    private PieWheelShape bogiePieWheel;
    private PieWheelShape shadowPieWheel;
    private CircleShape wheelHole;
    private CircleShape wheelBG;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mRotationMatrixPar = new float[16];
    private float[] mRotationMatrixBirdie = new float[16];
    private float[] mRotationMatrixBogie = new float[16];
    private float[] mRotationMatrixShadow = new float[16];

    float angle = 0;

    @Override
    public void onDrawFrame(GL10 unused){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        float[] parScratch = new float[16];
        float[] bogieScratch = new float[16];
        float[] birdieScratch = new float[16];
        float[] shadowScratch1 = new float[16];
        float[] shadowScratch2 = new float[16];

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        float parAngle = -1 * (360 * parPercent);
        float birdieAngle = -1 * (360 * birdiePercent);
        float startingAngle = -90;

        //System.out.println("par angle = " + Float.toString(parAngle));
        //System.out.println("birdie angle = " + Float.toString(birdieAngle));
        //System.out.println("bogie angle = " + Float.toString(-1 * (360 * bogiePercent)));

        // Create a rotation transformation;
        if (angle > -450){
            //rotates birdie sector to starting angle from angle 0
            if (angle > startingAngle){
                Matrix.setRotateM(mRotationMatrixBirdie, 0, angle, 0, 0, -1.0f);
            }
            if (angle > startingAngle + birdieAngle){
                Matrix.setRotateM(mRotationMatrixPar, 0, angle, 0, 0, -1.0f);
            }
            if (angle > startingAngle + birdieAngle + parAngle){
                Matrix.setRotateM(mRotationMatrixBogie, 0, angle, 0, 0, -1.0f);
            }
            angle -= 2;
        }

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(shadowScratch1, 0, mMVPMatrix, 0, mRotationMatrixShadow, 0);
        Matrix.multiplyMM(bogieScratch, 0, mMVPMatrix, 0, mRotationMatrixBogie, 0);
        Matrix.multiplyMM(parScratch, 0, mMVPMatrix, 0, mRotationMatrixPar, 0);
        Matrix.multiplyMM(birdieScratch, 0, mMVPMatrix, 0, mRotationMatrixBirdie, 0);

        shadowPieWheel.draw(mMVPMatrix);
        wheelBG.draw(mMVPMatrix);
        bogiePieWheel.draw(bogieScratch);
        parPieWheel.draw(parScratch);
        birdiePieWheel.draw(birdieScratch);
        wheelHole.draw(mMVPMatrix);
    }

    public float birdiePercent;
    public float parPercent;
    public float bogiePercent;
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.82f, 0.84f, 0.85f, 1.0f);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        if (birdiePercent == 0 && parPercent == 0 && bogiePercent == 0){
            birdiePercent = .3333333f;
            parPercent = .3333333f;
            bogiePercent = .3333333f;
        }

        System.out.println("pars %" + Float.toString(parPercent));
        System.out.println("birdies %" + Float.toString(birdiePercent));
        System.out.println("bogies %" + Float.toString(bogiePercent));

        float colorYellow[] = { .83f, 0.65f, 0.16f, 1.0f };
        float colorGreen[] = { .33f, 0.47f, 0.25f, 1.0f };
        float colorBlue[] = { .14f, .35f, .48f, 1.0f };
        float colorBG[] = {0.82f, 0.84f, 0.85f, 1.0f};
        float colorWheelBG[] = {.66f, .66f, .66f, 1.0f};
        float colorShadow[] = { 0f, 0f, 0f, 0.7f };

        float wheelHoleCoords[] = {0f, 0f, 0f};
        float innerHoleRadius = .2f;
        wheelHole = new CircleShape(innerHoleRadius,colorBG,wheelHoleCoords);

        float wheelBGCoords[] = {0f, 0f, 0f};
        float radius = .5f;
        wheelBG = new CircleShape(radius,colorWheelBG,wheelBGCoords);

        float buffer = .005f;
        birdiePieWheel = new PieWheelShape(radius,colorBlue,(birdiePercent + buffer),false);
        parPieWheel = new PieWheelShape(radius,colorGreen,(parPercent + buffer),false);
        bogiePieWheel = new PieWheelShape(radius,colorYellow,(bogiePercent + buffer),false);
        shadowPieWheel = new PieWheelShape(radius,colorShadow,1f,true);
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
