package com.justreached.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.justreached.Models.Contact;
import com.justreached.Others.HelperMethods;
import com.justreached.R;
import com.justreached.Sqlite.DBHandler;

import java.util.ArrayList;
import java.util.Locale;

import static com.justreached.Activities.HomeFragment.PHONE_KEY;

/**
 * Created by Syed.Zakriya on 04-07-2017.
 */

public class DestinationReachedActivity extends Activity implements
        TextToSpeech.OnInitListener
{

    public ArrayList<Contact> contactsInSqlite = null;
    public DBHandler dbHandler = null;
    String selectedContact = null;

    private TextToSpeech tts;

    public Vibrator vibration() {

        Vibrator v = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);

        long[] pattern = {0, 3000, 0};

        v.vibrate(pattern, -1);
        return v;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Process.killProcess(Process.myPid());
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize the Sqlite DBHandler
        dbHandler = new DBHandler(getBaseContext());

        // get all contacts data from database
        contactsInSqlite = new ArrayList<Contact>();
        contactsInSqlite = dbHandler.getAllContacts();

        String[] phNum = new String[contactsInSqlite.size()];

        boolean phoneEnabled = HelperMethods.fetchBoolFromSharedPreferences(getBaseContext(), PHONE_KEY);

        vibration();
        Log.d("my_app", "phone enabled");
        Log.d("my_app", String.valueOf(contactsInSqlite.size()));

        for (int i = 0; i < contactsInSqlite.size(); i++) {
            Log.d("my_app", "inside loop");
            Contact contact = contactsInSqlite.get(i);
            String phoneNumber = contact.getName() + " " + "-" + " " + contact.getNumber();
            Log.d("my_app", phoneNumber);

            phNum[i] = phoneNumber;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_destination_reachd);

        tts = new TextToSpeech(this, this);

/*
        TtsProviderFactory ttsProviderImpl = TtsProviderFactory.getInstance();
        if (ttsProviderImpl != null) {
            ttsProviderImpl.init(getApplicationContext());
            ttsProviderImpl.say("You will reach your destination in some time");
        }
*/
        AppCompatRadioButton[] rb = new AppCompatRadioButton[phNum.length];
        RadioGroup rgp = (RadioGroup) findViewById(R.id.myRadioGrp);
        rgp.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < phNum.length; i++) {
            RadioButton rbn = new RadioButton(this);
            rbn.setId(i + 1000);
            if(i==0)
                rbn.setChecked(true);
            rbn.setText(phNum[i]);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBarOverlayLayout.LayoutParams.MATCH_PARENT, ActionBarOverlayLayout.LayoutParams.WRAP_CONTENT, 1f);
            rbn.setLayoutParams(params);
            rgp.addView(rbn);
        }

        //Set  button click listener
        Button myDialogOkBtn= (Button) findViewById(R.id.myOkBtn);
        Button myCancelBtn = (Button) findViewById(R.id.cancelBtn);

        myDialogOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radGrp = (RadioGroup) findViewById(R.id.myRadioGrp);
                //Phone Number to be called
                String radiovalue = ((RadioButton)findViewById(radGrp.getCheckedRadioButtonId())).getText().toString();

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                //Toast.makeText(getBaseContext(), "Selected phone number: " + radiovalue, Toast.LENGTH_SHORT).show();
                String selectedPhoneNum[] = radiovalue.split("-");
                callIntent.setData(Uri.parse("tel:" + selectedPhoneNum[1]));
                String uri = "tel:" + selectedPhoneNum[1];
                Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                exitApp();
                startActivity(dialIntent);
            }
        });
        myCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitApp();
            }
        });


    }

    public void exitApp()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            DestinationReachedActivity.this.finishAndRemoveTask();
        } else{
            // do something for phones running an SDK before lollipop
            DestinationReachedActivity.this.finishAffinity();
        }
    }

    public void textToSpeech()
    {
        tts.speak("You will reach your destination in some time", TextToSpeech.QUEUE_FLUSH, null);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak("You will reach your destination in some time", TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak("You will reach your destination in some time", TextToSpeech.QUEUE_FLUSH, null);
        }*/
    }



    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                textToSpeech();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}
