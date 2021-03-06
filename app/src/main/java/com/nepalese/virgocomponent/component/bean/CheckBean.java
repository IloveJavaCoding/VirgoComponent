package com.nepalese.virgocomponent.component.bean;

/**
 * @author nepalese on 2020/11/2 09:20
 * @usage 记录listview 内 CheckBox 选中情况
 */
public class CheckBean {
    private int id;
    private boolean isChecked;

    public CheckBean(int id, boolean isChecked) {
        this.id = id;
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}