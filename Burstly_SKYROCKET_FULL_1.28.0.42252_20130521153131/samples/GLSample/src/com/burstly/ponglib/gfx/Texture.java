package com.burstly.ponglib.gfx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL11;
import java.io.InputStream;

/**
 * Class for loading and initializing textures.
 */
public class Texture {
    /**
     * Utility function for finding if a number is a power of 2.
     *
     * @param n value to be checked to see if it is power of 2.
     * @return true if n is a power of 2, false otherwise
     */
    private static boolean isPowerOf2(int n) {
        return (n != 0) && ((n & (n - 1)) == 0);
    }

    /**
     * Utility function for finding the next power of 2 that is larger than n.
     *
     * @param n value whose closest larger power of 2 will be returned
     * @return closest power of 2 that is larger than n
     */
    private static int getNextPowerOf2(int n) {
        n--;
        n = (n >> 1) | n;
        n = (n >> 2) | n;
        n = (n >> 4) | n;
        n = (n >> 8) | n;
        n = (n >> 16) | n;
        return (n+1);
    }

    /**
     * Create a Texture from a resource
     *
     * @param gl openGL context reference
     * @param mips true generates mipmaps, false doesn't
     * @param context Android Context for getting the resource
     * @param res resource identifier
     * @return newly created texture
     */
    public static Texture createTextureFromResource(final GL11 gl, boolean mips, final Context context, int res) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 1;
        options.inDensity = 1;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), res, options);
        final Texture tex = new Texture(gl, mips, bitmap);
        bitmap.recycle();
        return tex;
    }

    /**
     * Create a Texture from a resource
     *
     * @param gl openGL context reference
     * @param mips true generates mipmaps, false doesn't
     * @param in InputStream reading an image file
     * @return newly created texture
     */
    public static Texture createTextureFromStream(final GL11 gl, boolean mips, final InputStream in) {
        final Bitmap bitmap = BitmapFactory.decodeStream(in);
        final Texture tex = new Texture(gl, mips, bitmap);
        bitmap.recycle();
        return tex;
    }

    /**
     * openGL context reference
     */
    private GL11 mGL;

    /**
     * openGL texture identifier
     */
    private int[] mTexs;

    /**
     * width of the original texture
     */
    private final int mTexWidth;

    /**
     * height of the original texture
     */
    private final int mTexHeight;

    /**
     * maximum U value which has pixel data loaded into it
     */
    private final float mMaxU;

    /**
     * maximum V value which has pixel data loaded into it
     */
    private final float mMaxV;

    /**
     * Creates a texture from a bitmap
     *
     * @param gl openGL context reference
     * @param mips true generates mipmaps, false doesn't
     * @param bitmap Bitmap to load the pixel data from
     */
    public Texture(GL11 gl, boolean mips, Bitmap bitmap)
    {
        if(bitmap == null) {
            mGL = null;
            mTexs = null;
            mTexWidth = mTexHeight = 0;
            mMaxV = mMaxU = 0.0f;
            return;
        }

        //initialize openGL texture and bind it
        mGL = gl;
        mTexs = new int[1];
        gl.glGenTextures(1, mTexs, 0);
        gl.glBindTexture(GL11.GL_TEXTURE_2D, mTexs[0]);

        //get the texture size
        mTexWidth = bitmap.getWidth();
        mTexHeight = bitmap.getHeight();

        //get the smallest square with dimensions which are powers of two for the internal tex size
        boolean created = false;
        int width = isPowerOf2(mTexWidth) ? mTexWidth : getNextPowerOf2(mTexWidth);
        int height = isPowerOf2(mTexHeight) ? mTexHeight : getNextPowerOf2(mTexHeight);

        //store the UV coordinates which store the original texture
        mMaxU = mTexWidth / (float)width;
        mMaxV = mTexHeight / (float)height;

        //If the bitmap needs to be resized create a new bitmap and copy data to it
        if(bitmap.getWidth() != width && bitmap.getHeight() != height) {
            created = true;
            Bitmap newBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());

            int[] pixelData = new int[mTexWidth * mTexHeight];
            bitmap.getPixels(pixelData, 0, mTexWidth, 0, 0, mTexWidth, mTexHeight);
            newBitmap.setPixels(pixelData, 0, mTexWidth, 0, 0, mTexWidth, mTexHeight);
            bitmap = newBitmap;
        }

        //set openGL texture parameters for filtering and wrapping
        gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
        gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        //generate mips if wanted
        if(mips) {
            gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
        }

        //move the mitmap data into the openGL texture
        GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bitmap, 0);

        //if we had to create a new bitmap for resizing then clean it up
        if(created)
            bitmap.recycle();
    }

    /**
     * Make sure the texture gets deleted
     */
    @Override
    public void finalize() {
        try {
            super.finalize();
        }
        catch (Throwable ignore) {
        }

        if(mTexs != null) {
            mGL.glDeleteTextures(1, mTexs, 0);
            mTexs = null;
            mGL = null;
        }
    }

    /**
     * binds the openGL tex
     */
    public void setAsDiffuseTex() {
        mGL.glBindTexture(GL11.GL_TEXTURE_2D, mTexs[0]);
    }

    /**
     * get the width of the original texture
     *
     * @return width
     */
    public float getWidth() {
        return mTexWidth;
    }

    /**
     * get the height of the original texture
     *
     * @return height
     */
    public float getHeight() {
        return mTexHeight;
    }

    /**
     * Get the texcoord of the right side of the original image
     *
     * @return maximum value of U holding texture data.
     */
    public float getMaxU() {
        return mMaxU;
    }

    /**
     * Get the texcoord of the bottom of the original image
     *
     * @return maximum value of v holding texture data.
     */
    public float getMaxV() {
        return mMaxV;
    }
}
