package com.justreached.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.justreached.Models.Contact;
import com.justreached.R;

import java.util.ArrayList;

/**
 * Created by SYED_ZAKRIYA on 3/7/2017.
 */

public class MyCustomAdapter extends BaseAdapter {

    private ArrayList<Contact> mListItems;
    private LayoutInflater mLayoutInflater;

    public MyCustomAdapter(Context context, ArrayList<Contact> arrayList) {

        mListItems = arrayList;

        //get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        //getCount() represents how many items are in the list
        return mListItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mListItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mListItems.get(i).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // create a ViewHolder reference
        ViewHolder holder;

        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();

            view = mLayoutInflater.inflate(R.layout.list_item, null);
            holder.nameTextView = (TextView) view.findViewById(R.id.nameTv);
            holder.numberTextView = (TextView) view.findViewById(R.id.numberTv);


            // the setTag is used to store the data within this view
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) view.getTag();
        }

        //get the string item from the position "position" from array list to put it on the TextView
        Contact contact = mListItems.get(position);

        if (contact != null) {
            if (holder.nameTextView != null) {
                //set the item name on the TextView
                holder.nameTextView.setText(contact.getName());
            }
            if (holder.numberTextView != null) {
                //set the item name on the TextView
                holder.numberTextView.setText(contact.getNumber());
            }
        }

        //this method must return the view corresponding to the data at the specified position.
        return view;

    }
    /**
     * Static class used to avoid the calling of "findViewById" every time the getView() method is called,
     * because this can impact to your application performance when your list is too big. The class is static so it
     * cache all the things inside once it's created.
     */
    private static class ViewHolder {

        protected TextView nameTextView;
        protected TextView numberTextView;

    }
}
