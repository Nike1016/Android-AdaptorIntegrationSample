package com.burstly.ponglib;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * GLSurfaceView.Renderer wrapper initializes openGL view and runs the IGLGame on the GL Thread.
 */
public class BurstlyGLRenderer implements GLSurfaceView.Renderer
{
    /**
     * time at the start of the last frame
     */
    private long mLastMS;

    /**
     * IGLGame containing the main game logic and renderring code
     */
    private IGLGame mGame;

    /**
     * Constructs a BurstlyGLRenderer which will run the main game loop.
     *
     * @param game IGLGame contaning the main game and game render logic
     */
    public BurstlyGLRenderer(IGLGame game) {
        mLastMS = 0;
        mGame = game;
    }

    /**
     * Called by GL Thread for the main game loop logic
     *
     * @param gl openGL context reference
     */
    public void onDrawFrame(GL10 gl)
    {
        float delta = 0.10f;

        if(mLastMS != 0) {
            long now = System.currentTimeMillis();
            delta = (now - mLastMS) / 1000.0f;
            mLastMS = now;
        }
        else {
            mLastMS = System.currentTimeMillis();
        }

        mGame.update(delta);
        mGame.render(gl);
    }

    /**
     * When the surface is changed update the viewport, and the model and projection matrices
     *
     * @param gl openGL context reference
     * @param width Width of the surface
     * @param height Height of the surface
     */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU.gluOrtho2D(gl, 0.0f, width, height, 0.0f);
    }

    /**
     * Called when the surface is created.
     *
     * Initializes GL states and calls IGLGame.loadAssets
     *
     * @param gl openGL context reference
     * @param config Current config of the newly created surface
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        mGame.loadAssets(gl);
    }
}
