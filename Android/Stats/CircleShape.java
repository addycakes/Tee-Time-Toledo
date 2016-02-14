package com.adamwilson.golf.Stats;

import android.opengl.GLES20;
import android.util.Size;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.lang.Math;

/**
 * Created by adam on 7/8/15.
 */
public class CircleShape {
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;
    private final FloatBuffer vertexBuffer;
    private int CIRCLE_POINTS = 300;
    static final int COORDS_PER_VERTEX = 3;
    private float circleRadius;
    float circleCoords[] = new float[(1 + CIRCLE_POINTS) * COORDS_PER_VERTEX];
    private float circleColor[] = new float[4];
    private final int mProgram;

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public CircleShape(float r, float[] color, float[] coordinates) {
        circleRadius = r;
        //set center coordinates (x,y,z)
        circleCoords[0] = coordinates[0];
        circleCoords[1] = coordinates[1];
        circleCoords[2] = coordinates[2];
        circleColor = color;

        int idx = 3;
        for (int i = 0; i < CIRCLE_POINTS; i++){
            float percent = (i / (float) (CIRCLE_POINTS-2));
            double rad = percent * 2 * Math.PI;

            //vertex position
            double outer_x = Math.cos(rad);
            double outer_y = Math.sin(rad);
            double outer_z = 0;

            circleCoords[idx] = circleCoords[0] + circleRadius * (float)outer_x;
            idx++;
            circleCoords[idx] = circleCoords[1] +circleRadius * (float)outer_y;
            idx++;
            circleCoords[idx] = (float)outer_z;
            idx++;
        }

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                circleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(circleCoords);
        vertexBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = BarGraphGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = BarGraphGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);

    }

    private int mPositionHandle;
    private int mColorHandle;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(mColorHandle, 1, circleColor, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the square
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, (CIRCLE_POINTS+1));

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
