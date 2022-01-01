package com.justreached.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.justreached.Models.ItemProvider;
import com.justreached.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zaks on 3/25/2017.
 */

public class SettingsAdapter extends ArrayAdapter {

    List list  = new ArrayList();
    DataHandler handler;
    String[] items;

    public SettingsAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    static class DataHandler {
        TextView item_name;
    }

    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("my_app","in getview");
        View view;
        view = convertView;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.settings_items,parent,false);

            handler = new DataHandler();
            handler.item_name = (TextView) view.findViewById(R.id.textview);
            view.setTag(handler);
        }
        else
        {
           handler = (DataHandler) view.getTag();
        }
        ItemProvider itemProvider = (ItemProvider) this.getItem(position);
        handler.item_name.setText(itemProvider.getItem_name());

        return view;
    }
}
