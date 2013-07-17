package com.burstly.ponglib.gfx;

import android.graphics.PointF;
import android.graphics.RectF;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 9 quads used to draw a rectangle that doesn't stretch the corners but acts as a sprite
 */
public class Sprite9Patch extends Sprite {
    /**
     * Index buffer storing the vertex order
     */
    private ByteBuffer mIndexBuffer;

    /**
     * Stores the size of a single section of the textured used
     */
    private final PointF mTexSectionSize;

    /**
     * Create a Sprite9Patch based on the given texture params
     *
     * @param texRect The UV coordinates for the texture
     * @param texSizePx The size of the texture rect in pixels
     */
    public Sprite9Patch(RectF texRect, PointF texSizePx) {
        super(texRect);

        mTexSectionSize = new PointF(texSizePx.x / 3.0f, texSizePx.y / 3.0f);
        createIndexBuffer();
    }

    /**
     * Creates a FloatBuffer and stores vertex texCoords for the quad.
     *
     * @param texRect UV coordinates
     */
    @Override
    protected void createTexCoordBuffer(RectF texRect) {
        float uDelta = (texRect.right - texRect.left) / 3.0f;
        float vDelta = (texRect.bottom - texRect.top) / 3.0f;

        final float[] texCoords = {
            texRect.left, texRect.top,
            texRect.left + uDelta, texRect.top,
            texRect.left + (2.0f * uDelta), texRect.top,
            texRect.right, texRect.top,
            texRect.left, texRect.top + vDelta,
            texRect.left + uDelta, texRect.top + vDelta,
            texRect.left + (2.0f * uDelta), texRect.top + vDelta,
            texRect.right, texRect.top + vDelta,
            texRect.left, texRect.top + (2.0f * vDelta),
            texRect.left + uDelta, texRect.top + (2.0f * vDelta),
            texRect.left + (2.0f * uDelta), texRect.top + (2.0f * vDelta),
            texRect.right, texRect.top + (2.0f * vDelta),
            texRect.left, texRect.bottom,
            texRect.left + uDelta, texRect.bottom,
            texRect.left + (2.0f * uDelta), texRect.bottom,
            texRect.right, texRect.bottom,
        };

        mTexCoords = createFloatBuffer(texCoords.length);
        mTexCoords.put(texCoords);
        mTexCoords.position(0);
    }

    /**
     * Create vertex buffer for vertex positions
     */
    @Override
    protected void createVertexBuffer() {
        mVerts = createFloatBuffer(32);
    }

    /**
     * Create the index buffer for storing vertex order
     */
    protected void createIndexBuffer() {
        final byte[] indices = {
            0,  1,  4,  1,  4,  5,  1,  2,  5,  2,  5,  6,  2,  3,  6,  3,  6,  7,
            4,  5,  8,  5,  8,  9,  5,  6,  9,  6,  9, 10,  6,  7, 10,  7, 10, 11,
            8,  9, 12,  9, 12, 13,  9, 10, 13, 10, 13, 14, 10, 11, 14, 11, 14, 15
        };

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.order(ByteOrder.nativeOrder());
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }

    /**
     * Draw the indexed arrays using the currently bound texture
     *
     * @param gl openGL context reference
     */
    @Override
    public void draw(GL10 gl) {
        mColor.setAsGLColor(gl);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVerts);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexCoords);
        gl.glDrawElements(GL10.GL_TRIANGLES, mIndexBuffer.capacity(), GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
    }

    /**
     * Set the vertex positions in the FloatBuffer based on the position and width
     */
    @Override
    protected void updateVertexBuffer() {
        float xStretch = mWidth - (2.0f * mTexSectionSize.x);
        float yStretch = mHeight - (2.0f * mTexSectionSize.y);

        final float[] verts = {
            mPos.x,                                 mPos.y,
            mPos.x + mTexSectionSize.x,                  mPos.y,
            mPos.x + mTexSectionSize.x + xStretch,       mPos.y,
            mPos.x + mWidth,                        mPos.y,
            mPos.x,                                 mPos.y + mTexSectionSize.y,
            mPos.x + mTexSectionSize.x,                  mPos.y + mTexSectionSize.y,
            mPos.x + mTexSectionSize.x + xStretch,       mPos.y + mTexSectionSize.y,
            mPos.x + mWidth,                        mPos.y + mTexSectionSize.y,
            mPos.x,                                 mPos.y + mTexSectionSize.y + yStretch,
            mPos.x + mTexSectionSize.x,                  mPos.y + mTexSectionSize.y + yStretch,
            mPos.x + mTexSectionSize.x + xStretch,       mPos.y + mTexSectionSize.y + yStretch,
            mPos.x + mWidth,                        mPos.y + mTexSectionSize.y + yStretch,
            mPos.x,                                 mPos.y + mHeight,
            mPos.x + mTexSectionSize.x,                  mPos.y + mHeight,
            mPos.x + mTexSectionSize.x + xStretch,       mPos.y + mHeight,
            mPos.x + mWidth,                        mPos.y + mHeight,
        };

        mVerts.put(verts);
        mVerts.position(0);
    }
}
