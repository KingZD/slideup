package com.example.slideup.adapter.holder;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.slideup.R;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by ZD on 2017/8/16.
 */

public class PopUpTabHolder extends BaseHolder {

    @BindView(R.id.tv_tab)
    TextView mTvTab;
    @BindView(R.id.iv_del)
    ImageView ivDel;

    public PopUpTabHolder(View itemView) {
        super(itemView);
    }

    public void init(String tab, boolean showEdit, String tag) {
        itemView.setTag(tag);
        mTvTab.setTag(tag);
        ivDel.setVisibility(View.GONE);
        if (mPosition > 0)
            ivDel.setVisibility(showEdit ? View.VISIBLE : View.GONE);
        if ("focusTab".equals(tag)) {
            if (mPosition > 0) {
                mTvTab.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else {
                mTvTab.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            }
        }
        mTvTab.setText(tab);
    }

    @OnLongClick(R.id.tv_tab)
    boolean longClick() {
        if (mCallback != null)
            mCallback.callback(PopUpTabHolder.this);
        return false;
    }

    @OnClick(R.id.tv_tab)
    void click(View v) {
        if (mCallback != null)
            mCallback.onItemClick(mPosition, itemView);
    }

    @OnClick(R.id.iv_del)
    void del(View view) {
        if (mCallback != null)
            mCallback.remove(mPosition, itemView);
    }

}
