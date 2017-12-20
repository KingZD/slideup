package com.example.slideup.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.example.slideup.R;
import com.example.slideup.adapter.holder.PopUpTabHolder;
import com.example.slideup.model.TabModel;

import java.util.List;


/**
 * Created by ZD on 2017/8/16.
 * 首页tab拖拽排序
 */

public class PopUpTabAdapter<T extends TabModel> extends BaseAdapter<PopUpTabHolder> {

    List<T> mTab;
    //区分tab区域
    String tag;
    boolean showEdit;

    public PopUpTabAdapter(Context mContext, List<T> tab, String tag) {
        super(mContext);
        this.mTab = tab;
        this.tag = tag;
    }

    @Override
    public void onBindViewHolder(PopUpTabHolder holder, int position) {
        holder.addItemClickCallback(mCallback, position);
        holder.init(mTab.get(position).getTabText(), showEdit,tag);
    }

    @Override
    public PopUpTabHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PopUpTabHolder(inflate(R.layout.pop_home_tab_item, parent));
    }

    @Override
    public int getItemCount() {
        return mTab.size();
    }

    public void showEdit(boolean showEdit) {
        this.showEdit = showEdit;
        notifyDataSetChanged();
    }
}
