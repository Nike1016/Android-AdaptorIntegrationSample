package com.burstly.ponglib;

import javax.microedition.khronos.opengles.GL10;

/**
 * Game interface
 */
interface IGLGame {
    /**
     * Called once the surface is created and the GL10 object can be used for creating textures.
     *
     * @param gl openGL context reference
     */
    public void loadAssets(GL10 gl);

    /**
     * Frame update function
     *
     * @param delta The amount of time that has passed since the previous update
     */
    public void update(float delta);

    /**
     * Draw the frame.
     *
     * @param gl openGL context reference
     */
    public void render(GL10 gl);
}
