package com.adamwilson.golf.Stats;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by adam on 7/6/15.
 */


public class CirclesGraphGLRenderer implements GLSurfaceView.Renderer {
    public boolean shouldAnimate = false;

    private CircleShape leftGreensCircle;
    private CircleShape leftGreensCircleShadow;
    private CircleShape centerGreensCircle;
    private CircleShape centerGreensCircleShadow;
    private CircleShape rightGreensCircle;
    private CircleShape rightGreensCircleShadow;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mScaleMatrixLeftCircle = new float[16];
    private float[] mScaleMatrixCenterCircle = new float[16];
    private float[] mScaleMatrixRightCircle = new float[16];

    private int[] leftCircleCurrentScale = {1000, 1000};
    private int[] rightCircleCurrentScale = {1000, 1000};
    private int centerCircleCurrentScale = 1000;
    private int scaleFactor = 5;

    public float leftCirclePercent;
    public float rightCirclePercent;
    public float centerCirclePercent;
    @Override
    public void onDrawFrame(GL10 unused){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mScaleMatrixLeftCircle, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mScaleMatrixCenterCircle, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mScaleMatrixRightCircle, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        int leftCircleScale = Math.round(1000 * leftCirclePercent) + 500;
        int centerCircleScale = Math.round(1000 * centerCirclePercent) + 500;
        int rightCircleScale = Math.round(1000 * rightCirclePercent) + 500;

        if (shouldAnimate){
            if (rightCircleCurrentScale[0] < rightCircleScale){
                rightCircleCurrentScale[0] += scaleFactor;
                rightCircleCurrentScale[1] += scaleFactor;
            }else if (rightCircleCurrentScale[0] > rightCircleScale){
                rightCircleCurrentScale[0] = rightCircleCurrentScale[0] - scaleFactor;
                rightCircleCurrentScale[1] = rightCircleCurrentScale[1] - scaleFactor;
            }
            if (leftCircleCurrentScale[0] < leftCircleScale){
                leftCircleCurrentScale[0] += scaleFactor;
                leftCircleCurrentScale[1] += scaleFactor;
            }else if (leftCircleCurrentScale[0] > leftCircleScale){
                leftCircleCurrentScale[0] = leftCircleCurrentScale[0] - scaleFactor;
                leftCircleCurrentScale[1] = leftCircleCurrentScale[1] - scaleFactor;
            }
            if (centerCircleCurrentScale < centerCircleScale){
                centerCircleCurrentScale += scaleFactor;
            }else if (centerCircleCurrentScale > centerCircleScale){
                centerCircleCurrentScale = centerCircleCurrentScale - scaleFactor;
            }

            Matrix.scaleM(mScaleMatrixCenterCircle, 0, (centerCircleCurrentScale/1000.0f), (centerCircleCurrentScale/1000.0f), 0);
            Matrix.scaleM(mScaleMatrixLeftCircle, 0, (leftCircleCurrentScale[0]/1000.0f), (leftCircleCurrentScale[1]/1000.0f), 0);
            Matrix.scaleM(mScaleMatrixRightCircle, 0, (rightCircleCurrentScale[0]/1000.0f), (rightCircleCurrentScale[1]/1000.0f), 0);
        }

        leftGreensCircleShadow.draw(mScaleMatrixLeftCircle);
        leftGreensCircle.draw(mScaleMatrixLeftCircle);
        rightGreensCircleShadow.draw(mScaleMatrixRightCircle);
        rightGreensCircle.draw(mScaleMatrixRightCircle);
        centerGreensCircleShadow.draw(mScaleMatrixCenterCircle);
        centerGreensCircle.draw(mScaleMatrixCenterCircle);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.82f, 0.84f, 0.85f, 1.0f);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        float colorYellow[] = { .83f, 0.65f, 0.16f, 1.0f };
        float colorGreen[] = { .33f, 0.47f, 0.25f, 1.0f };
        float colorBlue[] = { .14f, .35f, .48f, 1.0f };
        float colorShadow[] = { 0f, 0f, 0f, 0.7f };

        float leftCircleCoords[] = {.4f, 0f, 0f};
        float leftCircleShadowCoords[] = {.43f, -.03f, 0f};
        float rightCircleCoords[] = {-.4f, 0f, 0f};
        float rightCircleShadowCoords[] = {-.37f, -.03f, 0f};
        float centerCircleCoords[] = {0f, 0f, 0f};
        float centerCircleShadowCoords[] = {.03f, -.03f, 0f};

        float radius = .4f;

        leftGreensCircle = new CircleShape(radius,colorBlue,leftCircleCoords);
        leftGreensCircleShadow = new CircleShape(radius,colorShadow,leftCircleShadowCoords);
        centerGreensCircle = new CircleShape(radius,colorGreen,centerCircleCoords);
        centerGreensCircleShadow = new CircleShape(radius,colorShadow,centerCircleShadowCoords);
        rightGreensCircle = new CircleShape(radius,colorYellow,rightCircleCoords);
        rightGreensCircleShadow = new CircleShape(radius,colorShadow,rightCircleShadowCoords);
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