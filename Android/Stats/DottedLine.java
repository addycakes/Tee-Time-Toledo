package com.adamwilson.golf.Stats;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by adam on 7/15/15.
 */
public class DottedLine {
    /*private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "uniform mat4 mv;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 a_color;" +
                    "varying vec4 v_color;" +
                    "varying vec4 position;" +
                    "void main() {" +
                    "gl_Position = uMVPMatrix * vPosition;" +
                    "position = mv * vPosition;" +
                    "v_color = a_color;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec2 sourcePoint;" +
                    "varying vec4 vColor;" +
                    "varying vec4 position; " +
                    "void main() {" +
                    "if (cos(0.1*abs(distance(sourcePoint.xy, position.xy))) + 0.5 > 0.0) {" +
                    "gl_FragColor = vec4(0,0,0,0);" +
                    "} else {" +
                    "gl_FragColor = vColor;" +
                    "}" +
                    "}";
*/
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


    private int mMVPMatrixHandle;
    private final FloatBuffer vertexBuffer;
    static final int COORDS_PER_VERTEX = 3;
    //float lineCoords[] = new float[2 * COORDS_PER_VERTEX];
    //float lineCoords[] = {0f, 0f, 0f, -1f, -1f, 0f};
    float lineCoords[] = new float[6];
    private float lineColor[] = { 0f, 0f, 0f, 0.7f };
    private final int mProgram;

    public DottedLine(float startX, float startY, float endX, float endY){

        //line coordinates
        lineCoords[0] = startX;
        lineCoords[1] = startY;
        lineCoords[2] = 0f;
        lineCoords[3] = endX;
        lineCoords[4] = endY;
        lineCoords[5] = 0f;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                lineCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(lineCoords);
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

        GLES20.glUniform4fv(mColorHandle, 1, lineColor, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glLineWidth(GLES20.GL_LINE_WIDTH);

        // Draw the square
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, lineCoords.length/2);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


}
