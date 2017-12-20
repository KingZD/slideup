package com.example.slideup.adapter.holder;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.example.slideup.Callback;

import butterknife.ButterKnife;

/**
 * Created by ZD on 2017/7/27.
 */

public class BaseHolder extends RecyclerView.ViewHolder {
    protected Context mContext;
    protected String TAG = this.getClass().getName();
    //回调
    protected Callback mCallback;
    //item下标
    protected int mPosition = -10086;

    public BaseHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null && mPosition != -10086) {
                    mCallback.onItemClick(mPosition);
                    mCallback.onItemClick(mPosition, v);
                }
            }
        });
    }

    protected <T> T getView(@IdRes int id) {
        return (T) itemView.findViewById(id);
    }

    /**
     * 默认只提供回调事件如果同时调用了setPosition（int mPosition）
     * 则提供类似于
     * {@link BaseHolder#addItemClickCallback}
     * 方法的效果
     * 不需要默认添加每个item的点击事件 所以可以单独设置
     *
     * @param callback
     */
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    /**
     * 该方法可以实现item单击事件类似于ListView的OnItemClickListener
     *
     * @param callback
     * @param mPosition
     */
    public void addItemClickCallback(Callback callback, int mPosition) {
        this.mCallback = callback;
        this.mPosition = mPosition;
    }

    /**
     * 设置当前holder的下标
     *
     * @param mPosition
     */
    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public int getItemIndex() {
        return mPosition;
    }

    public Context getContext() {
        if (itemView == null) return null;
        return itemView.getContext();
    }

    public String getString(int resid) {
        return getContext().getString(resid);
    }

    public String getString(int resid, Object... formatArgs) {
        return getContext().getString(resid, formatArgs);
    }

    public void recycle() {
        mCallback = null;
        mContext = null;
    }
}
