package com.example.slideup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.slideup.model.TabModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View v) {
        ArrayList<TabModel> allTab = new ArrayList<>();
        ArrayList<TabModel> focus = new ArrayList<>();
        HomeTabPopUpWindow p = new HomeTabPopUpWindow(this);
        p.setBindData(allTab, focus);
        p.showAsDropDown(v);
        //模拟异步
        for (int i = 0; i < 8; i++) {
            TabBean tabModel = new TabBean();
            tabModel.setTabName("FOCUSTAB" + (i + 1));
            focus.add(tabModel);
        }
        for (int i = 0; i < 30; i++) {
            TabBean tabModel = new TabBean();
            tabModel.setTabName("ALLTAB" + (i + 1));
            allTab.add(tabModel);
        }
        p.update();
    }
}
