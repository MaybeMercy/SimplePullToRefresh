package com.main.maybe.simplepulltorefresh;

import android.content.Context;
import android.view.animation.Interpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by Maybe霏 on 2015/4/16.
 */
public class MyFrameLayout extends FrameLayout{

    private float mLastMotionX, mLastMotionY;
    private float mDeltax, mDeltay;
    private ScrollToHomeRunnable mScrollToHomeRunnable;

    private enum State{
        REFRESHING,
        PULLING_HORIZONTAL,
        PULLING_VERTICAL,
        NORMAL
    };

    private enum Orientation{
        HORIZONTAL,
        VERTICAL
    }
    private State mState;
    private Orientation mOrientation;

    private void init(Context context){
        mState = State.NORMAL;
    }

    public MyFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public MyFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mState == State.REFRESHING)
            return true;
        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();
                mDeltax = .0f;
                mDeltay = .0f;
                break;
            case MotionEvent.ACTION_MOVE:
                float innerDeltay = event.getY() - mLastMotionY;
                float innerDeltax = event.getX() - mLastMotionX;
                float absInnerDeltay = Math.abs(innerDeltay);
                float absInnerDeltax = Math.abs(innerDeltax);

                if (absInnerDeltax > absInnerDeltay && mState != State.PULLING_VERTICAL){
                    mOrientation = Orientation.HORIZONTAL;
                    mState = State.PULLING_HORIZONTAL;
                    if (innerDeltax > 1.0f){
                        mDeltax -= absInnerDeltax;
                        pull(mDeltax);
                    }else if (innerDeltax < -1.0f){
                        mDeltax += absInnerDeltax;
                        pull(mDeltax);
                    }
                }else if(absInnerDeltay > absInnerDeltax && mState != State.PULLING_HORIZONTAL){
                    mOrientation = Orientation.VERTICAL;
                    mState = State.PULLING_VERTICAL;
                    if (innerDeltay > 1.0f){
                        mDeltay -= absInnerDeltay;
                        pull(mDeltay);
                    }else if(innerDeltay < -1.0f){
                        mDeltay += absInnerDeltay;
                        pull(mDeltay);
                    }
                }

                // record the x,y
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                switch (mOrientation){
                    case VERTICAL:
                        smoothScrollTo(mDeltay);
                        break;
                    case HORIZONTAL:
                        smoothScrollTo(mDeltax);
                        break;
                    default:
                        break;
                }
                break;
        }
        return true;
    }

    private void pull(float diff){
        int value = Math.round(diff / 2.0f);
        if (mOrientation == Orientation.VERTICAL)
            scrollTo(0, value);
        else if (mOrientation == Orientation.HORIZONTAL)
            scrollTo(value, 0);
    }

    private void smoothScrollTo(float diff){
        int value = Math.round(diff / 2.0f);
        mScrollToHomeRunnable = new ScrollToHomeRunnable(value, 0);
        mState = State.REFRESHING;
        post(mScrollToHomeRunnable);
    }

    final class ScrollToHomeRunnable implements Runnable{

        private final Interpolator mInterpolator;
        private int target;
        private int current;
        private long mStartTime = -1;

        public ScrollToHomeRunnable(int current, int target) {
            this.target = target;
            this.current = current;
            this.mInterpolator = new DecelerateInterpolator();
        }

        @Override
        public void run() {
            if (mStartTime == -1){
                mStartTime = System.currentTimeMillis();
            }else{
                long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime))/200;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);
                final int delta = Math.round((current - target) * mInterpolator.getInterpolation(normalizedTime / 1000f));

                current = current - delta;
                if (mOrientation == Orientation.HORIZONTAL)
                    scrollTo(current, 0);
                else if(mOrientation == Orientation.VERTICAL)
                    scrollTo(0, current);
            }

            if (current != target)
                postDelayed(this, 16);
            else
                mState = State.NORMAL;
        }
    }
}
