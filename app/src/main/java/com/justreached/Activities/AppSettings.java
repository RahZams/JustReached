package com.justreached.Activities;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.justreached.Others.HelperMethods;
import com.justreached.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppSettings extends Fragment{

//    public static Switch whatsappSwitchBtn = null;
    public static Switch smsSwitchBtn = null;
    public static Switch phoneSwitchBtn = null;
    public static EditText messageEditText = null;
    String message = "";
    public static TextView text_limit = null;
    Toolbar toolbar;
    //public static Button saveBtn = null;

//    public static final String WHATSAPP_KEY ="IsWhatsAppEnabled";
    public static final String SMS_KEY ="IsSMSEnabled";
    public static final String PHONE_KEY ="IsPhoneCallEnabled";
    public static final String SAVED_MSG_KEY ="MyMessage";
   // public static final String WHATSAPP_PKG_NAME = "com.whatsapp";

    public AppSettings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_settings, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        //Get reference to all the buttons in the view
//        whatsappSwitchBtn = (Switch) view.findViewById(R.id.whatsappBtn);
        smsSwitchBtn = (Switch) view.findViewById(R.id.smsBtn);
        phoneSwitchBtn = (Switch) view.findViewById(R.id.callSwitchBtn);
        messageEditText = (EditText) view.findViewById(R.id.msgEditText);
        text_limit = (TextView) view.findViewById(R.id.char_limit);


       final AdView adView1 = (AdView) view.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.loadAd(adRequest);

        AdView adView2 = (AdView) view.findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        adView2.loadAd(adRequest2);


        // to get the done button in keyboard
        //messageEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        messageEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        messageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                adView1.setVisibility(View.INVISIBLE);
            }
        });

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                text_limit.setText(140 - editable.toString().length() + "/140 characters");
            }
        });
        //saveBtn = (Button) view.findViewById(R.id.saveSettingsBtn);

        //From the Shared Preference, fetch the corresponding values for the above controls
//        boolean whatsappEnabled = HelperMethods.fetchBoolFromSharedPreferences((MainActivity)getActivity(),WHATSAPP_KEY);
        boolean smsEnabled = HelperMethods.fetchBoolFromSharedPreferences((MainActivity)getActivity(),SMS_KEY);
        boolean phoneEnabled = HelperMethods.fetchBoolFromSharedPreferences((MainActivity)getActivity(),PHONE_KEY);
        message = HelperMethods.fetchStringFromSharedPreferences((MainActivity)getActivity(),SAVED_MSG_KEY);
        //Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();

        //Set the above fetched valued from Shared Prefs in the corresponding controls
//        whatsappSwitchBtn.setChecked(whatsappEnabled);
        smsSwitchBtn.setChecked(smsEnabled);
        phoneSwitchBtn.setChecked(phoneEnabled);

        //disabling the message box if sms is not checked
        if(String.valueOf(smsSwitchBtn.isChecked()) == "false" && (message == null || message.equals("null") || message.isEmpty() || message.length() == 0 || message.equals(""))) {
            // disabling the message box
            Log.d("my_app","not checked sms" + smsEnabled);
            //Toast.makeText(getActivity(),String.valueOf(smsSwitchBtn.isChecked()),Toast.LENGTH_LONG).show();
            messageEditText.setText(message);
            messageEditText.setFocusableInTouchMode(false);
            messageEditText.setEnabled(false);

            //messageEditText.setFocusable(false);
            getActivity().invalidateOptionsMenu();
            //messageEditText.setFocusable(true);
        }
        else {
            messageEditText.setText(message);
            messageEditText.setFocusableInTouchMode(true);
            messageEditText.setEnabled(true);
            getActivity().invalidateOptionsMenu();
        }


        // setting the message text fetched from shared preferences
//        if(message!=null && !message.equals("null") && !message.isEmpty() && message.length() != 0 && !message.equals("")) {
//            Toast.makeText(getActivity(),"message is not null",Toast.LENGTH_LONG).show();
//            messageEditText.setText(message);
//            messageEditText.setFocusableInTouchMode(true);
//            messageEditText.setEnabled(true);
////            messageEditText.setFocusable(true);
//        }

        //HelperMethods.saveToSharedPreferences((MainActivity) getActivity(), SAVED_MSG_KEY, message);
        //Set on checked listener for all the above fetched control
//        whatsappSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                HelperMethods.saveToSharedPreferences((MainActivity)getActivity(),WHATSAPP_KEY,b);
//            }
//        });

        smsSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(smsSwitchBtn.isChecked()){
                    Log.d("my_app","is chcked");
                    messageEditText.setFocusableInTouchMode(true);
                    messageEditText.setEnabled(true);
                    getActivity().invalidateOptionsMenu();
                }
                else{
                    message = "";
                    Log.d("my_app","is unchecked");
                    messageEditText.setText("");
                    messageEditText.setFocusableInTouchMode(false);
                    messageEditText.setEnabled(false);
                    getActivity().invalidateOptionsMenu();
                }

                HelperMethods.saveToSharedPreferences((MainActivity)getActivity(),SMS_KEY,b);
                 HelperMethods.saveToSharedPreferences((MainActivity) getActivity(), SAVED_MSG_KEY, message);

            }
        });

//        if(smsSwitchBtn.isChecked()) {
//            Log.d("my_app","is chcked");
//            messageEditText.setFocusableInTouchMode(true);
////            messageEditText.setEnabled(true);
////            messageEditText.setFocusable(true);
//        }

        phoneSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                HelperMethods.saveToSharedPreferences((MainActivity)getActivity(),PHONE_KEY,b);
            }
        });


        // once the done button in keyboard is clicked, save the text and hide the keyboard
        messageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if (messageEditText.getText().toString() != "") {
                        message = messageEditText.getText().toString();
                        HelperMethods.saveToSharedPreferences((MainActivity) getActivity(), SAVED_MSG_KEY, message);
                    }
                    else
                        message = "";

                    HelperMethods.saveToSharedPreferences((MainActivity) getActivity(), SAVED_MSG_KEY, message);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                return true;
            }
        });

//        saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String msg = messageEditText.getText().toString();
//                HelperMethods.saveToSharedPreferences((MainActivity)getActivity(),SAVED_MSG_KEY,msg);
//                //HelperMethods.sendWhatsappMsg((MainActivity)getActivity(),WHATSAPP_PKG_NAME,msg);
//                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.main_container,new RZContactPicker()).commit();
//                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Contacts");
//
//            }
//        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d("my_app","create");
        inflater.inflate(R.menu.settings_save_menu,menu);
        MenuItem item = menu.findItem(R.id.save);
        item.setVisible(false);
        if(smsSwitchBtn.isChecked()){
            MenuItem done = menu.findItem(R.id.done);
            done.setVisible(false);
            MenuItem save = menu.findItem(R.id.save);
            save.setVisible(true);
        }

//       if(message!=null && message!= "") {
//          Log.d("my_app", "message is" + message);
//           MenuItem done = menu.findItem(R.id.done);
//           done.setVisible(false);
//           MenuItem save = menu.findItem(R.id.save);
//           save.setVisible(true);
//       }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.done){
            if(!smsSwitchBtn.isChecked() && !phoneSwitchBtn.isChecked()){
                Toast.makeText(getContext(),"Select SMS or Call option for notification",Toast.LENGTH_LONG).show();
            }
            if(phoneSwitchBtn.isChecked() && !smsSwitchBtn.isChecked()){
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container,new RZContactPicker()).addToBackStack("contacts").commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Contacts");
            }
//            if(smsSwitchBtn.isChecked() && (messageEditText.getText().toString() == "" || messageEditText.getText().toString() == null)){
//                HelperMethods.saveToSharedPreferences((MainActivity) getActivity(), SAVED_MSG_KEY, "destination reached");
//            }
        }
        if(item.getItemId() == R.id.save){
            String msgText = messageEditText.getText().toString();
            //messageEditText.getText().toString() != " " || messageEditText.getText().toString() != null
            //TextUtils.isEmpty(messageEditText.getText().toString())
            if (msgText == null || msgText.equals("null") || msgText.isEmpty() || msgText.length() == 0 || msgText.equals("")) {
                Toast.makeText(getContext(),"Enter the notification message",Toast.LENGTH_LONG).show();

            }
            else {
                Log.d("my_app","message not null" + messageEditText.getText().toString());
                AlertDialog.Builder alertDlgBuilder = HelperMethods.createAlertDialog(getActivity(),0,false,"Would you like to save this message text for notification ","Confirmation!!!");
                alertDlgBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        message = messageEditText.getText().toString();
                        //messageEditText.setFocusable(false);
                        HelperMethods.saveToSharedPreferences((MainActivity) getActivity(), SAVED_MSG_KEY, message);
                        dialog.dismiss();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new RZContactPicker()).addToBackStack(null).commit();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Contacts");

                    }
                });
                alertDlgBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //messageEditText.setText(message);
                        //messageEditText.setFocusable(false);
                        dialog.dismiss();
                        /*FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new RZContactPicker()).commit();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Contacts");*/
                    }
                });
                AlertDialog alertDialog = alertDlgBuilder.create();
                alertDialog.show();
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        toolbar.setTitle("Notification");
        super.onResume();
    }
}
