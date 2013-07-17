package com.burstly.ponglib.gfx;

import android.graphics.PointF;
import android.graphics.RectF;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Simple sprite class
 */
public class Sprite {
    /**
     * Buffers used for drawing textured quads
     */
    protected FloatBuffer mVerts;
    protected FloatBuffer mTexCoords;

    /**
     * Upper left corner of the quad
     */
    protected PointF mPos;

    /**
     * Color used to draw the sprite
     */
    protected Color mColor;

    /**
     * Width of the quad
     */
    protected float mWidth;

    /**
     * Height of the quad
     */
    protected float mHeight;

    /**
     * Helper function for creating a FloatBuffer
     *
     * @param numFloats Capacity of the float buffer
     * @return newly created FloatBuffer
     */
    protected static FloatBuffer createFloatBuffer(int numFloats) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(numFloats * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        return byteBuffer.asFloatBuffer();
    }

    /**
     * Creates a FloatBuffer and stores vertex texCoords for the quad.
     *
     * @param texRect UV coordinates
     */
    protected void createTexCoordBuffer(final RectF texRect) {
        final float[] texCoords = {
            texRect.left, texRect.top,
            texRect.right, texRect.top,
            texRect.left, texRect.bottom,
            texRect.right, texRect.bottom
        };

        mTexCoords = createFloatBuffer(texCoords.length);
        mTexCoords.put(texCoords);
        mTexCoords.position(0);
    }

    /**
     * Create vertex buffer for vertex positions
     */
    protected void createVertexBuffer() {
        mVerts = createFloatBuffer(8);
    }

    /**
     * Construct a new sprite with internal buffers for rendering a textured quad
     *
     * @param texRect UV Coordinates
     */
    public Sprite(final RectF texRect) {
        mPos = new PointF(0.0f, 0.0f);
        mWidth = texRect.right - texRect.left;
        mHeight = texRect.bottom - texRect.top;
        mColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);

        createTexCoordBuffer(texRect);
        createVertexBuffer();
    }

    /**
     * Draw sprite quad with the currently bound texture.
     *
     * @param gl openGL context reference
     */
    public void draw(final GL10 gl) {
        mColor.setAsGLColor(gl);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVerts);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexCoords);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    }

    /**
     * Set the sprites vertex positions in the FloatBuffer based on the position and width
     */
    protected void updateVertexBuffer() {
        final float[] verts = {
            mPos.x,             mPos.y,
            mPos.x + mWidth,    mPos.y,
            mPos.x,             mPos.y + mHeight,
            mPos.x + mWidth,    mPos.y + mHeight,
        };

        mVerts.put(verts);
        mVerts.position(0);
    }

    /**
     * Sets the color the sprite is drawn with
     *
     * @param color ARGB color
     */
    public void setColor(final Color color) {
        mColor = color;
    }

    /**
     * Moves the sprite to the designated position
     *
     * @param x New Position's x component
     * @param y New Position's y component
     */
    public void setPos(float x, float y) {
        mPos.x = x;
        mPos.y = y;

        updateVertexBuffer();
    }

    /**
     * Set the size of the sprite
     *
     * @param width Sprite's width
     * @param height Sprite's height
     */
    public void setSize(float width, float height) {
        mWidth = width;
        mHeight = height;

        updateVertexBuffer();
    }

    /**
     * Set the position and size of the sprite
     *
     * @param x New Position's x component
     * @param y New Position's y component
     * @param width Sprite's width
     * @param height Sprite's height
     */
    public void setPosAndSize(float x, float y, float width, float height) {
        mPos.x = x;
        mPos.y = y;
        mWidth = width;
        mHeight = height;

        updateVertexBuffer();
    }

    /**
     * Determine if a point is contained within a sprite
     *
     * @param point point to check
     * @return true if its inside, false otherwise
     */
    public boolean pointIsIn(final PointF point) {
        return (point.x >= mPos.x && point.x <= (mPos.x + mWidth) && point.y > mPos.y && point.y < (mPos.y + mHeight));
    }
}

