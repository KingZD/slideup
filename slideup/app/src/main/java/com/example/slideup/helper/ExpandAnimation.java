package com.example.slideup.helper;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by ZD on 2017/5/5.
 */

public class ExpandAnimation extends Animation implements Animation.AnimationListener {
    private View view;
    private LinearLayout.LayoutParams mViewLayoutParams;
    private RelativeLayout.LayoutParams mViewLayoutParams1;
    private int mMarginStart, mMarginEnd;
    private boolean mIsVisibleAfter = false;
    private boolean mWasEndedAlready = false;

    public ExpandAnimation(View view, long duration, boolean isShow) {
        setDuration(duration);
        this.view = view;
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            mViewLayoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            mIsVisibleAfter = (mViewLayoutParams.bottomMargin == 0);
            if (isShow) {//首次是展开还是不展开
                mIsVisibleAfter = false;
                mViewLayoutParams.bottomMargin = (0 - view.getMeasuredHeight());
            }
            mMarginStart = mViewLayoutParams.bottomMargin;
        } else if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            mViewLayoutParams1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
            mIsVisibleAfter = (mViewLayoutParams1.bottomMargin == 0);
            if (isShow) {//首次是展开还是不展开
                mIsVisibleAfter = false;
                mViewLayoutParams1.bottomMargin = (0 - view.getMeasuredHeight());
            }
            mMarginStart = mViewLayoutParams1.bottomMargin;
        }
        mMarginEnd = (mMarginStart == 0) ? (0 - view.getMeasuredHeight()) : 0;
        view.setVisibility(View.VISIBLE);
        setAnimationListener(this);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        Log.i("info", "mMarginStart-->>" + mMarginStart + ", mMarginEnd-->>" + mMarginEnd + ", interpolatedTime-->>" + interpolatedTime);
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1f) {
            if (mViewLayoutParams != null)
                mViewLayoutParams.bottomMargin = mMarginStart + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);
            if (mViewLayoutParams1 != null)
                mViewLayoutParams1.bottomMargin = mMarginStart + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);
            if (mMarginStart < mMarginEnd)
                t.setAlpha(interpolatedTime);
            else
                t.setAlpha(1 - interpolatedTime);
            view.requestLayout();
        } else {
            if (mViewLayoutParams != null)
                mViewLayoutParams.bottomMargin = mMarginEnd;
            if (mViewLayoutParams1 != null)
                mViewLayoutParams1.bottomMargin = mMarginEnd;
            view.requestLayout();
            if (mIsVisibleAfter)
                view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (view != null)
            view.setAlpha(0);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}