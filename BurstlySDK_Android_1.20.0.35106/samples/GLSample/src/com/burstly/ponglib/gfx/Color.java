package com.burstly.ponglib.gfx;


import javax.microedition.khronos.opengles.GL10;

/**
 * Simple color class
 */
public  class Color
{
    public static final Color BLACK = new Color(1.0f, 0.0f, 0.0f, 0.0f);
    public static final Color WHITE = new Color();

    /**
     * ARGB color components
     */
    protected float mA;
    protected float mR;
    protected float mG;
    protected float mB;

    /**
     * Default constructor sets color to white
     */
    public Color() {
        mA = mR = mG = mB = 1.0f;
    }

    /**
     * Set ARGB color
     *
     * @param a alpha channel 0.0f - 1.0f
     * @param r red channel 0.0f - 1.0f
     * @param g green channel 0.0f - 1.0f
     * @param b blue channel 0.0f - 1.0f
     */
    public Color(float a, float r, float g, float b) {
        mA = a;
        mR = r;
        mG = g;
        mB = b;
    }

    /**
     * Get the alpha channel value.
     *
     * @return Alpha 0.0f - 1.0f
     */
    public float getAf() {
        return mA;
    }

    /**
     * Get the red channel value.
     *
     * @return Red 0.0f - 1.0f
     */
    public float getRf() {
        return mR;
    }

    /**
     * Get the green channel value.
     *
     * @return Green 0.0f - 1.0f
     */
    public float getGf() {
        return mG;
    }

    /**
     * Get the blue channel value.
     *
     * @return Blue 0.0f - 1.0f
     */
    public float getBf() {
        return mB;
    }

    /**
     * Set the alpha channel
     *
     * @param a Alpha 0.0f - 1.0f
     */
    public void setA(float a) {
        mA = a;
    }

    /**
     * Set the red channel
     *
     * @param r Red 0.0f - 1.0f
     */
    public void setR(float r) {
        mR = r;
    }

    /**
     * Set the green channel
     *
     * @param g Green 0.0f - 1.0f
     */
    public void setG(float g) {
        mG = g;
    }

    /**
     * Set the blue channel
     *
     * @param b Blue 0.0f - 1.0f
     */
    public void setB(float b) {
        mB = b;
    }

    /**
     * Use this color as the current gl color
     *
     * @param gl OpenGL context reference
     */
    public void setAsGLColor(GL10 gl) {
        gl.glColor4f(mR, mG, mB, mA);
    }
}
