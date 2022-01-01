package com.justreached.Models;

/**
 * Created by SYED_ZAKRIYA on 3/19/2017.
 */

public class ListViewItem {
    int type;
    Object object;

    public ListViewItem(int type, Object object) {
        this.setType(type);
        this.setObject(object);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
