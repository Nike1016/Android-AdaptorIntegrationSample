package com.burstly.ponglib;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.*;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import com.burstly.ponglib.gfx.*;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * BurstlyPong is a simple pong clone which sizes the gaming area to allow for banners and triggers interstitials every
 * 3 rounds.
 */
public class BurstlyPong extends GLSurfaceView implements IGLGame, IPongListener
{
    /**
     * Constants
     */
    private static final float BANNER_HEIGHT = 50.0f;
    private static final PointF BACK_SIZE = new PointF(279.0f, 478.0f);
    private static final PointF PADDLE_SIZE = new PointF(64.0f, 64.0f);
    private static final PointF BALL_SIZE = new PointF(64.0f, 64.0f);
    private static final RectF BACK_TEXRECT = new RectF(0.0f, 0.0f, BACK_SIZE.x / 512.0f, BACK_SIZE.y / 512.0f);
    private static final RectF PADDLE_TEXRECT = new RectF(BACK_SIZE.x / 512.0f, 0.0f, (BACK_SIZE.x + PADDLE_SIZE.x) / 512.0f, PADDLE_SIZE.y / 512.0f );
    private static final RectF BALL_TEXRECT = new RectF(BACK_SIZE.x / 512.0f, PADDLE_SIZE.y / 512.0f, (BACK_SIZE.x + BALL_SIZE.x) / 512.0f, (PADDLE_SIZE.y + BALL_SIZE.y) / 512.0f);

    private static final Color CLEAR_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.0f);
    private static final Color RAIL_COLOR = new Color(1.0f, 0.4f, 0.4f, 0.4f);

    /**
     * GLSurfaceView.Renderer which runs the main loop
     */
    protected final BurstlyGLRenderer mRenderer;

    /**
     * Activity which this surface is drawing on
     */
    protected final Activity mActivity;

    /**
     * Object listening for game events
     */
    protected final IPongListener mPongListener;

    /**
     * The game logic
     */
    protected final PongGameLogic mGameLogic;

    /**
     * Is the game paused
     */
    protected volatile boolean mPaused = true;

    /**
     * If the interstitial has shown or failed then start the next round
     */
    protected volatile boolean mStartNextRound;

    /**
     * Graphical members
     */
    protected Texture mTex;
    protected Sprite mBack;
    protected Sprite[] mPaddles;
    protected Sprite mBall;

    /**
     * Screen density for calculating banner widget size
     */
    protected float mDensity;

    /**
     * The two locations used for moving the paddles
     */
    protected PointF[] mTouchLocs;

    /**
     * Instantiate the game
     *
     * @param activity Activity that holds this surface
     *
     */
    public BurstlyPong(Activity activity, IPongListener pongListener)
    {
        super(activity);
        mPaused = false;
        mStartNextRound = false;
        mActivity = activity;
        mPongListener = pongListener;
        mRenderer = new BurstlyGLRenderer(this);
        mGameLogic = new PongGameLogic(this);

        mTouchLocs = new PointF[2];
        mTouchLocs[0]= new PointF(0.0f, 0.0f);
        mTouchLocs[1] = new PointF(1.0f, 1.0f);

        //Initialize touch handling
        setFocusable(true);
        setFocusableInTouchMode(true);
        setRenderer(mRenderer);
        setTouchDelegate(new TouchDelegate(new Rect(0, 0, this.getWidth(), this.getHeight()), this) {
            /**
             * If there is just 1 touch location use it for both paddles.
             * If there is more than 1 touch location use the leftmost for the left paddle and the rightmost for the right
             */
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                float width = getWidth();
                float maxX = (width / 2.0f);
                float minX = (width / 2.0f);
                boolean set[] = {false, false};

                for (int i = 0; i < event.getPointerCount(); i++) {
                    float x = event.getX(i);
                    float y = event.getY(i);

                    if(x < minX) {
                        set[0] = true;
                        minX = (int)x;
                        mTouchLocs[0].set(x, y);
                    }
                    else if(x > maxX) {
                        set[1] = true;
                        maxX = (int)x;
                        mTouchLocs[1].set(x, y);
                    }
                }

                if(event.getPointerCount() == 1) {
                    if(set[0])
                        mTouchLocs[1].y = mTouchLocs[0].y;
                    else
                        mTouchLocs[0].y = mTouchLocs[1].y;
                }

                return true;
            }
        });
    }

	@Override
	public void onPause() {
		super.onResume();
		mPaused = true;
		mStartNextRound = false;
	}

    /**
     * When the activity is resumed we will resume the game this happens after an interstitial is shown
     */
	@Override
	public void onResume() {
		super.onResume();
		mPaused = false;
		mStartNextRound = true;
	}

    /**
     * Called when an interstitial fails and we need to start the game back up manually
     */
    public void nextRound() {
        mStartNextRound = true;
    }

    /**
     * Load game assets and initialize
     *
     * @param gl openGL context reference
     */
    public void loadAssets(GL10 gl) {
        GLGfx.initGfx(40);

        //Get the density for banner size calcs.
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;

        //initialize the only texture
        try {
            AssetManager assetMgr = mActivity.getAssets();
            mTex = Texture.createTextureFromStream((GL11)gl, true, assetMgr.open("assets_tex.png"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        //setup the sprites
        mPaddles = new Sprite[2];
        mPaddles[0] = new Sprite9Patch(PADDLE_TEXRECT, PADDLE_SIZE);
        mPaddles[1] = new Sprite9Patch(PADDLE_TEXRECT, PADDLE_SIZE);
        mBall = new Sprite(BALL_TEXRECT);
        mBack = new Sprite(BACK_TEXRECT);

        //position the background image so it is centered in the game area drawn at it's native size if it fits
        //if it doesn't fit scale it so it draws as large as possible
        float gameAreaHeight = getHeight() - BANNER_HEIGHT * mDensity;
        float scale = (BACK_SIZE.y > gameAreaHeight) ? (gameAreaHeight / BACK_SIZE.y) : 1.0f;
        float bgX = (getWidth() - (scale * BACK_SIZE.x)) / 2.0f;
        float bgY = (BANNER_HEIGHT * mDensity) + (gameAreaHeight - (scale * BACK_SIZE.y)) / 2.0f;

        mBack.setPosAndSize(bgX, bgY, scale * BACK_SIZE.x, scale * BACK_SIZE.y);

        //Initialize the game
        mGameLogic.setupRect(new RectF(0.0f, BANNER_HEIGHT * mDensity, getWidth(), getHeight()));
        mGameLogic.initRound();
    }

    /**
     * Main update code (We don't want any allocations happening anywhere in the main loop).
     *
     * @param delta The amount of time that has passed since the previous update
     */
    public void update(float delta) {
    	float[] paddleCenterY = {
            mTouchLocs[0].y,
            mTouchLocs[1].y
        };

        if(!mPaused) {
            mGameLogic.update(delta, paddleCenterY);
            mGameLogic.updateBallSprite(mBall);
            mGameLogic.updatePaddleSprites(mPaddles);
        }
        else if(mStartNextRound) {
            mGameLogic.initRound();
            mPaused = false;
            mStartNextRound = false;
        }
    }

    /**
     * Render the game (part of the main loop, no allocations anywhere in here).
     *
     * @param gl openGL context reference
     */
    public void render(GL10 gl) {

        GLGfx.clearScreen(gl, true, CLEAR_COLOR, true);

        mBack.draw(gl);

        GLGfx.drawLine(gl, RAIL_COLOR, 0, BANNER_HEIGHT * mDensity, getWidth(), BANNER_HEIGHT * mDensity);

        mPaddles[0].draw(gl);
        mPaddles[1].draw(gl);
        mBall.draw(gl);
    }

	/**
	 * called by the game logic when a point scored
	 */
	public boolean pointScored(int winner, int hits) {
		if (mPongListener != null && !mPaused) {
			if (mPongListener.pointScored(winner, hits)) {
				mPaused = true;
				return true;
			}
		}

		mGameLogic.initRound();
		return false;
	}

    /**
     * Pause / unpause the game
     * @param paused true for paused, false for unpaused
     */
    public void setPaused(final boolean paused) {
    	mPaused = paused;
    }
}