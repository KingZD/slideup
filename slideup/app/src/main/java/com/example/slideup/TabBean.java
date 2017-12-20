package com.example.slideup;

import com.example.slideup.model.TabModel;

/**
 * @author zd
 * @package com.example.slideup
 * @fileName TabBean
 * @date on 2017/12/20 0020 15:38
 * @org 湖北博娱天成科技有限公司
 * @describe TODO
 * @email 1053834336@qq.com
 */

public class TabBean implements TabModel {
    private String tabName;

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    @Override
    public String getTabText() {
        return tabName;
    }
}
