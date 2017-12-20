package com.example.slideup;

import android.view.View;

/**
 * Created by ZD on 2017/8/7.
 */

public class Callback<T> {
    public void callback(float x,float y){}
    public void callback(boolean bool){}
    public void callback(int i){}
    public void callback(){}
    public void callback(int position, View v){}
    public void remove(int position, View v){}
    public void callback(int position,boolean bool, T data){}
    public void callback(Object obj){}
    public void refresh(){}
    public void synchronizationData(boolean bool){}
    public void loadMore(){}
    public void onItemClick(int i, View v){}
    public void onItemClick(int i){}
    public void swap(int[] swap){}
}
