package com.example.slideup;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * Created by zd on 2017/9/26 0026.
 */

public class SlideUpView extends ScrollView {
    float mX = 0;
    float mY = 0, pY = 0, mStart = 0, mEnd = 0, mTop = 0;
    boolean isMove = false, animationRunning = false, isLoading = false, isAllowScroll = false;
    ObjectAnimator mAnimatorTranslateY;
    TranslateAnimation ta;
    View child, measureView;
    Rect mView;

    public SlideUpView(Context context) {
        super(context);
        init();
    }

    public SlideUpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideUpView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        mView = new Rect();
        setVerticalScrollBarEnabled(false);

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDisplayMetrics().heightPixels));

        setFillViewport(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        ViewGroup view = (ViewGroup) getChildAt(0);
        int max = Math.max(getMeasuredHeight(), view.getMeasuredHeight());
        view.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(max, MeasureSpec.EXACTLY));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (child == null)
            child = getChildAt(0);
        if (MotionEvent.ACTION_DOWN == ev.getAction()) {
            mY = ev.getY();
            mX = ev.getX();
            pY = ev.getY();
            mStart = ev.getY();
        } else if (MotionEvent.ACTION_MOVE == ev.getAction()) {
            if (mCallback != null)
                mCallback.callback(ev.getX(), ev.getY());
            float x = ev.getX();
            float y = ev.getY();
            //求角度
            double v = Math.toDegrees(Math.atan2(y - mY, x - mX));
            if (Math.abs(pY - y) < 50) return false;
            if (Math.abs(v) > 45 && Math.abs(v) < 135)
                return true;
            pY = ev.getY();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMove = false;
                mY = ev.getY();
                mStart = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mEnd = Math.abs(ev.getY() - mStart);
                if (this.listener != null && child != null)
                    this.listener.scroll(mStart, mEnd,
                            child.getTop(), getHeight());
                isMove = true;
                if (isAllowScroll) {
                    move(ev);
                }
                mY = ev.getY();
                if (((child.getMeasuredHeight() * 9 / 10) <= (getScrollY() + getHeight())) && !isLoading) {
                    //loadmore
                    if (mCallback != null)
                        mCallback.loadMore();
                    isLoading = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (animationRunning) return true;
                mTop = child.getTop();
                if (listener != null && child.getTop() > (getHeight() * 0.15)) {
                    animationBottom();
                    animationRunning = true;
                } else if (child.getTop() > 0) {
                    animationTop();
                    animationRunning = true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void move(MotionEvent ev) {
        float y = ev.getY() - mY;
        if (y < 0 && Math.abs(y) > child.getTop())
            y = -child.getTop();
        //处理
        if ((isNeedMove() && y > 0) || (child.getTop() > 0 && y < 0)) {
            if (mView.isEmpty()) {
                mView.set(child.getLeft(), child.getTop(),
                        child.getRight(), child.getBottom());
            }
            child.layout(child.getLeft(), child.getTop() + (int) y, child.getRight(), child.getBottom());
        }
    }

    /***
     * 回缩动画
     */
    public void animationTop() {
        // 开启移动动画
        ta = new TranslateAnimation(0, 0, child.getTop(),
                mView.top);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ta.setDuration(200);
        child.startAnimation(ta);
        // 设置回到正常的布局位置
        child.layout(mView.left, mView.top, mView.right, mView.bottom);
        mView.setEmpty();
    }

    /***
     * 回缩动画
     */
    public void animationBottom() {
        // 开启移动动画
        mAnimatorTranslateY = ObjectAnimator.
                ofFloat(child, "translationY", mEnd, getHeight());
        mAnimatorTranslateY.setDuration(500);
        mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
        mAnimatorTranslateY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                if (listener != null && child != null)
                    listener.scroll(mStart, animatedValue, mTop, getHeight());

                // 设置回到正常的布局位置
                if (animatedValue == getHeight()) {
                    animationRunning = false;
                    child.layout(mView.left, (int) animatedValue, mView.right, mView.bottom);
                    mView.setEmpty();
                }
//                child.setTranslationY(animatedValue);
            }
        });
        mAnimatorTranslateY.start();

        // 开启移动动画
    }

    /***
     * 是否需要移动布局 child.getMeasuredHeight():获取的是控件的总高度
     *
     * getHeight()：获取的是屏幕的高度
     *
     * @return
     */
    public boolean isNeedMove() {
        int offset = child.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        // 0是顶部，后面那个是底部
        if (scrollY == 0) {//|| scrollY == offset
            return true;
        }
        return false;
    }

    public interface ScrollListener {
        void scroll(float start, float end, float top, int height);
    }

    public void close() {
        animationBottom();
    }

    public void scrollTop() {
        if (mAnimatorTranslateY != null)
            mAnimatorTranslateY.cancel();
        if (ta != null)
            ta.cancel();
        if (child != null)
            child.setTranslationY(0);
    }

    public void scrollBottom() {
        if (mAnimatorTranslateY != null)
            mAnimatorTranslateY.cancel();
        if (ta != null)
            ta.cancel();
        if (child != null)
            smoothScrollTo(0, child.getMeasuredHeight());
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mCallback != null) {
            if (measureView != null) {
                int h = measureView.getHeight() / 3;
                if (measureView instanceof ViewGroup) {
                    ViewGroup mv = (ViewGroup) this.measureView;
                    if (mv.getChildCount() > 0)
                        h = ((ViewGroup) this.measureView).getChildAt(0).getHeight() / 2;
                }
                if ((measureView.getTop() + h) <= getScrollY() + getHeight()) {
                    mCallback.callback(true);
                    measureView = null;
                }

            }
        }
    }

    public void isShowInParent(View view, Callback callback) {
        mCallback = callback;
        measureView = view;
    }

    //是否允许弹性滑动
    public void setAllowScroll(boolean allowScroll) {
        isAllowScroll = allowScroll;
    }

    public ScrollListener listener;

    public void setListener(ScrollListener listener) {
        this.listener = listener;
    }

    Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }
}
