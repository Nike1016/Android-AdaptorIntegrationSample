package com.burstly.ponglib;

import android.graphics.PointF;
import android.graphics.RectF;
import com.burstly.ponglib.gfx.Sprite;

/**
 * Game logic handles moving the game entities, handling collision, and determining when a round is over
 */
public class PongGameLogic {
    /**
     * Constants
     */
    private static final float BALL_SIZE = 0.025f;
    private static final float INITIAL_SPEED_MULT = 0.50f;
    private static final float INITIAL_SIZE = 0.50f;

    /**
     * Ball variables
     */
    private PointF mBallCenter;
    private float mBallRadius;
    private float mBallRadiusSquared;
    private PointF mBallVel;

    /**
     * Paddle variables
     */
    private PointF[] mPaddleUL;
    private PointF mPaddleSize;

    /**
     * The area used for the game
     */
    private RectF mGameArea;

    /**
     * Reference to the BurstlyPong game which is using this game logic
     */
    private IPongListener mListener;

    /**
     * Number of times the ball was hit this round
     */
    private int mHits;

    /**
     * Create the game.&nbsp;Create all objects so allocation isn't needed during the main loop
     *
     * @param listener interface for receiving game events
     */
    PongGameLogic(IPongListener listener) {
        mListener = listener;
        mHits = 0;

        mBallCenter = new PointF();
        mBallVel = new PointF();

        mPaddleSize = new PointF();
        mPaddleUL = new PointF[2];
        mPaddleUL[0] = new PointF();
        mPaddleUL[1] = new PointF();
    }

    /**
     * setup the game to run within the given rect.
     *
     * @param gameArea bounds of the game area
     */
    void setupRect(RectF gameArea) {
        mGameArea = gameArea;
        mBallRadius = (gameArea.bottom - gameArea.top) * BALL_SIZE;
        mBallRadiusSquared = mBallRadius * mBallRadius;

        float width = (mGameArea.right - mGameArea.left);
        mPaddleSize.x = 0.05f * width;
        mPaddleUL[0].x = 2.0f * mPaddleSize.x;
        mPaddleUL[1].x = width - (3.0f * mPaddleSize.x);
    }

    /**
     * Start a new round of pong
     */
    void initRound() {
        mHits = 0;

        if(mGameArea != null) {
            float width = (mGameArea.right - mGameArea.left);
            float height = (mGameArea.bottom - mGameArea.top);
            mBallCenter.x = mGameArea.left + ((width / 2.0f) - (BALL_SIZE));
            mBallCenter.y = mGameArea.top + ((height / 2.0f) - (BALL_SIZE));

            mPaddleSize.y = height * INITIAL_SIZE;
            mBallVel.x = width * (INITIAL_SPEED_MULT / 2.0f);
            mBallVel.y = height * (INITIAL_SPEED_MULT / 2.0f);
        }
    }

    /**
     * Update the game entities, and do collision
     *
     * @param delta time since the last update
     * @param paddleCenterY the Y positions that should be used for the paddles
     */
    void update(float delta, float[] paddleCenterY) {
        mBallCenter.set(mBallCenter.x + (mBallVel.x * delta), mBallCenter.y + (mBallVel.y * delta));
        mPaddleUL[0].set(mPaddleUL[0].x, paddleCenterY[0] - (mPaddleSize.y / 2.0f));
        mPaddleUL[1].set(mPaddleUL[1].x, paddleCenterY[1] - (mPaddleSize.y / 2.0f));

        //Keep paddles within the game area
        paddlesLockToGameArea();

        //if we have a paddle collision count it and make the paddles smaller and the ball faster
        if(paddleBallCollision()) {
            mHits++;
            makeHarder();
        }

        //check for collisions against the top and bottom boundaries
        ballWallCollision();

        //round end conditions
        if(mBallCenter.x > mGameArea.right) {
            mListener.pointScored(0, mHits);
        }
        else if(mBallCenter.x < mGameArea.left) {
            mListener.pointScored(1, mHits);
        }
    }

    /**
     * If paddles are outside the game area then bring them back in.
     */
    private void paddlesLockToGameArea() {
        for(int i = 0; i < 2; i++) {
            if(mPaddleUL[i].y < mGameArea.top)
                mPaddleUL[i].y = mGameArea.top;
            else if(mPaddleUL[i].y + mPaddleSize.y > mGameArea.bottom)
                mPaddleUL[i].y = mGameArea.bottom - mPaddleSize.y;
        }
    }

    /**
     * Collide ball with paddles
     *
     * @return true if there was a collision, false otherwise
     */
    private boolean paddleBallCollision() {
        //Get direction of movement
        boolean movingRight = mBallVel.x > 0.0f;
        boolean movingDown = mBallVel.y > 0.0f;

        //Only check collision with 1 paddle based on direction of ball
        int index = movingRight ? 1 : 0;

        float halfWidth = mPaddleSize.x / 2.0f;
        float halfHeight = mPaddleSize.y / 2.0f;

        //translate ball and paddle so that the paddles center is at the origin
        float rectCenterX = mPaddleUL[index].x + halfWidth;
        float rectCenterY = mPaddleUL[index].y + halfHeight;

        float transCircleCenterX = mBallCenter.x - rectCenterX;
        float transCircleCenterY = mBallCenter.y - rectCenterY;

        float absTransCircleCenterX = Math.abs(transCircleCenterX);
        float absTransCircleCenterY = Math.abs(transCircleCenterY);

        //if the center of ball is inside the paddle
        if(absTransCircleCenterX <= halfWidth && absTransCircleCenterY <= halfHeight) {
            //If the ball isn't half way through when the paddle was moved on top then change the direction

            if((movingDown && transCircleCenterY > 0.0f) || (!movingDown && transCircleCenterY < 0.0f))
                mBallVel.y = -mBallVel.y;

            if((movingRight && transCircleCenterX < 0.0f) || (!movingRight && transCircleCenterX > 0.0f)){
                mBallVel.x = -mBallVel.x;
                return true;
            }
        }
        //else if the balls center is above or below the paddle
        else if(absTransCircleCenterX <= halfWidth && absTransCircleCenterY - mBallRadius <= halfHeight) {
            //If the ball isn't half way through when the paddle was moved on top then change the direction
            if((movingDown && transCircleCenterY > 0.0f) || (!movingDown && transCircleCenterY < 0.0f)) {
                mBallVel.y = -mBallVel.y;
                return false;
            }
        }
        //else if the balls center is to the right or the left of the paddle
        else if(absTransCircleCenterY <= halfHeight && absTransCircleCenterX - mBallRadius <= halfWidth) {
            //If the ball isn't half way through when the paddle was moved on top then change the direction
            if((movingRight && transCircleCenterX < 0.0f) || (!movingRight && transCircleCenterX > 0.0f)) {
                mBallVel.x = -mBallVel.x;
                return true;
            }
        }
        //else balls center is to diagonal of the paddle
        else {
            float deltaX = transCircleCenterX - halfWidth;
            float deltaY = transCircleCenterY - halfHeight;
            float distSquared = (deltaX * deltaX) + (deltaY * deltaY);

            //If the distance between the balls center and the corner squared is less than the radius squared
            if(distSquared < mBallRadiusSquared) {
                //If the ball isn't half way through when the paddle was moved on top then change the direction
                if((movingDown && transCircleCenterY > 0.0f) || (!movingDown && transCircleCenterY < 0.0f))
                    mBallVel.y = -mBallVel.y;

                if((movingRight && transCircleCenterX < 0.0f) || (!movingRight && transCircleCenterX > 0.0f)) {
                    mBallVel.x = -mBallVel.x;
                    return true;
                }
            }
        }

        //collision didn't change the x direction of the ball
        return false;
    }

    /**
     * If the ball hits the top or bottom boundary bounce it
     */
    private void ballWallCollision() {
        if(mBallCenter.y - mBallRadius <= mGameArea.top) {
            mBallCenter.y = mGameArea.top + mBallRadius;
            mBallVel.y = -mBallVel.y;
        }
        else if(mBallCenter.y + mBallRadius >= mGameArea.bottom) {
            mBallCenter.y = mGameArea.bottom - mBallRadius;
            mBallVel.y = -mBallVel.y;
        }
    }

    /**
     * Make the game harder by making the paddles smaller and the ball faster
     */
    private void makeHarder() {
        mBallVel.x = mBallVel.x * 1.05f;
        mBallVel.y = mBallVel.y * 1.05f;
        mPaddleSize.y = mPaddleSize.y * 0.95f;
    }

    /**
     * Set the ball sprite to the appropriate position at the correct size from the game logic
     *
     * @param ball Sprite which is used to render the ball
     */
    void updateBallSprite(Sprite ball) {
        ball.setPosAndSize(mBallCenter.x - mBallRadius, mBallCenter.y - mBallRadius, mBallRadius, mBallRadius);
    }

    /**
     * Set the paddle sprites to the appropriate position and size from the game logic
     * @param paddles the sprites which render the player paddles
     */
    void updatePaddleSprites(Sprite[] paddles) {
        for(int i = 0; i < 2; i++) {
            paddles[i].setPosAndSize(mPaddleUL[i].x, mPaddleUL[i].y, mPaddleSize.x, mPaddleSize.y);
        }
    }
}
