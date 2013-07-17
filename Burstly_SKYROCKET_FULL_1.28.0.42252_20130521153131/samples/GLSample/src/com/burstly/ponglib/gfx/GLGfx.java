package com.burstly.ponglib.gfx;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * GL primitive drawing helper functions
 */
public class GLGfx {
    /**
     * Vertex buffer used to push verts to the gl render pipeline
     */
    private static FloatBuffer sVertexBuffer;

    /**
     * Initialize the primitive drawing system
     *
     * @param maxFloats Maximum number of vertex components that will be used for drawing
     */
    public static void initGfx(int maxFloats) {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(maxFloats * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        sVertexBuffer = byteBuffer.asFloatBuffer();
    }

    /**
     * Clear the screen
     *
     * @param gl openGL context reference
     * @param clearColor true will clear the color buffer, false will not
     * @param color color to clear the color buffer to
     * @param clearDepth true clears the color buffer, false leaves it alone
     */
    public static void clearScreen(final GL10 gl, boolean clearColor, final Color color, boolean clearDepth)
    {
        int mask = 0;

        if(clearColor)
        {
            mask = GL10.GL_COLOR_BUFFER_BIT;
            gl.glClearColor(color.getRf(), color.getGf(), color.getBf(), color.getAf());
        }

        if(clearDepth)
            mask |= GL10.GL_DEPTH_BUFFER_BIT;

        if(mask != 0)
            gl.glClear(mask);
    }

    /**
     * Draw a rectangle of a given color
     *
     * @param gl OpenGL context reference
     * @param x Upper left corner X coordinate
     * @param y Upper left corner Y coordinate
     * @param w Width of the rectangle
     * @param h Height of the rectangle
     * @param color Color used to draw the triangle
     */
    public static void fillRect(final GL10 gl, float x, float y, float w, float h, final Color color) {
        final float[] verts = {
            x, y,
            x + w, y,
            x, y + h,
            x + w, y + h
        };

        sVertexBuffer.put(verts);
        sVertexBuffer.position(0);

        color.setAsGLColor(gl);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, sVertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, verts.length / 2);

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

    /**
     * Draw a polyline between vertex component pairs
     *
     * @param gl openGL context reference
     * @param color line color
     * @param verts x,y float component pairs to draw the lines between
     * @param close true will draw an additional line between the last and first elements to close the loop
     */
    public static void drawPolyLine(final GL10 gl, final Color color, final float[] verts, boolean close)
    {
        sVertexBuffer.put(verts);
        sVertexBuffer.position(0);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);

        color.setAsGLColor(gl);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, sVertexBuffer);

        if(close)
            gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, verts.length / 2);
        else
            gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, verts.length / 2);

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

    /**
     * Draw a line between 2 x, y points
     *
     * @param gl openGL context reference
     * @param color line Color
     * @param x1 first point's x component
     * @param y1 first point's y component
     * @param x2 second point's x component
     * @param y2 second point's y component
     */
    public static void drawLine(final GL10 gl, final Color color, float x1, float y1, float x2, float y2) {
        final float[] verts = {x1, y1, x2, y2};
        drawPolyLine(gl, color, verts, false);
    }
}
