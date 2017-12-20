package com.example.slideup.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.slideup.Callback;
import com.example.slideup.adapter.holder.BaseHolder;



/**
 * Created by ZD on 2017/7/29.
 */

public abstract class BaseAdapter<T extends BaseHolder> extends RecyclerView.Adapter<T> {

    /**
     * 如果需要实现item动画需要在
     * public void onBindViewHolder(MatchHolder holder, int position) 调用
     * protected void viewBindAnimation(T holder);
     * 然后重写
     * protected Animator[] getAnimators(View view)
     **/
    protected Callback mCallback;
    protected String TAG = this.getClass().getName();
    protected Context mContext;


    public BaseAdapter(Context mContext) {
        this.mContext = mContext;
    }

    protected View inflate(int resId, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(resId, parent, false);
    }


    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

}
