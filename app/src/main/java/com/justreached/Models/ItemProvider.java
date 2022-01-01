package com.justreached.Models;

/**
 * Created by Zaks on 3/25/2017.
 */

public class ItemProvider {
    private String item_name;

    public ItemProvider(String item){
        this.item_name = item;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
}
