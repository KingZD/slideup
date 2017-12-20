package com.example.slideup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by zd on 16/6/21.
 */
public abstract class BasePopupWindow extends PopupWindow {

    WindowManager mWindowManager;

    protected SlideUpView mView;
    Context mContext;
    private View inflate;

    public Context getContext() {
        return mContext;
    }

    public BasePopupWindow(Context context) {
        super(context);
        this.mContext = context;
        this.mWindowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
    }

    public int getScreenWidth() {
        return mWindowManager.getDefaultDisplay().getWidth();
    }

    public int getScreenHeight() {
        return mWindowManager.getDefaultDisplay().getHeight();
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return (T) mView.findViewById(id);
    }

    protected void setContentView(@LayoutRes int resource) {
        //制作容器
        mView = new SlideUpView(getContext());
        mView.setId(R.id.Pop_Container);
        //设置容器获得点击事件 否则容器下层视图会被触发
        mView.setClickable(true);
        mView.setFocusable(true);
        //装载视图
        inflate = LayoutInflater.from(mContext).inflate(resource, null);
        mView.addView(inflate);
//        inflate.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        inflate.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        //设置SelectPicPopupWindow的View
        setContentView(mView);
//        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(setContainerHeight());
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimBottom);
        ColorDrawable dw;
        if (allowBackgroundTranslucent()) {
            //实例化一个ColorDrawable颜色为半透明
            dw = new ColorDrawable(0x90000000);
        } else {
            dw = new ColorDrawable(0x00000000);
        }
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //虚拟按键
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        initPopWindowFinish();
    }

    public void postDelay(final Callback callback) {
        if (inflate != null) {
            int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            inflate.measure(spec, spec);
            callback.callback(inflate.getMeasuredHeight());
        }
    }

    protected void initPopWindowFinish() {

    }

    //true有背景色 false无背景色
    protected boolean allowBackgroundTranslucent() {
        return true;
    }

    protected int setContainerHeight() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    public SlideUpView getScrollView() {
        return mView;
    }

    public View getView() {
        return inflate;
    }

    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    protected void setAlpha(int alpha) {
        if (mView != null)
            mView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        if (mView != null)
            mView.scrollTop();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        if (mView != null)
            mView.scrollTop();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        if (mView != null)
            mView.scrollTop();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (mView != null)
            mView.scrollTop();
    }
}
