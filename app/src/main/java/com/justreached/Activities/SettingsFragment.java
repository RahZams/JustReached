package com.justreached.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.justreached.Adapters.SettingsAdapter;
import com.justreached.Models.ItemProvider;
import com.justreached.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    String[] items;
    ListView settingslist;
    SettingsAdapter settingsAdapter;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Log.d("my_app", "in oncreate view");
        settingslist = (ListView) view.findViewById(R.id.settingslist);

        items = getResources().getStringArray(R.array.view_items);


        settingsAdapter = new SettingsAdapter(getActivity(), R.layout.settings_items);
        settingslist.setAdapter(settingsAdapter);
        int i = 0;
        for (String item : items) {
            ItemProvider itemProvider = new ItemProvider(item);
            settingsAdapter.add(itemProvider);
            i++;
        }
        settingslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("my_app", String.valueOf(id));
                Log.d("my_app", String.valueOf(position));
                switch (position) {
                    case 0:
                        showNetwork(getActivity());
                        break;

                    case 1:
                        showLocationScreen(getContext());
                        break;

                    case 2:
//                        Intent intent = new Intent(getActivity(),FeedbackMail.class);
//                        startActivity(intent);
                          sendMail();
                        break;
                }

            }
        });

        return view;
    }

    public static void showNetwork(final Context context) {
        Log.d("my_app", "showNetDisabledAlertToUser");
        Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogIntent);
    }

    public void showLocationScreen(Context context){
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        startActivity(new Intent(action));
    }

    public void sendMail()
    {
        String Sub = "Feedback about the App";
        String From = "syed.zakriya.2008@gmail.com";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{From});
        intent.putExtra(Intent.EXTRA_SUBJECT, Sub);
//        intent.putExtra(Intent.EXTRA_TEXT, Message);

        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Select Email Sending App :"));
    }
}