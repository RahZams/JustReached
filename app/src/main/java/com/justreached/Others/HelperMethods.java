package com.justreached.Others;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.justreached.Activities.AppSettings;
import com.justreached.Models.Contact;
import com.justreached.Sqlite.DBHandler;

import java.util.ArrayList;

/**
 * Created by SYED_ZAKRIYA on 3/9/2017.
 */

public class HelperMethods {

    public static Context context=null;
    public static final String PREFS_NAME = "MY_PREF_FILE";
    public static SharedPreferences sharedPreferences=null;

    public static AlertDialog.Builder createAlertDialog(Context c,int iconId, boolean isCancelable, String msg, String title)
    {
        context = c;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        if (iconId!=0)
            alertDialogBuilder.setIcon(iconId);

        alertDialogBuilder.setCancelable(isCancelable);

        alertDialogBuilder.setMessage(msg);

        alertDialogBuilder.setTitle(title);

        return alertDialogBuilder;

    }

    public static void saveToSharedPreferences(Context c, String key, boolean value)
    {
        if(context==null)
            context =c;
        if(sharedPreferences==null)
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void saveToSharedPreferences(Context c,String key, String value)
    {
        if(context==null)
            context =c;
        if(sharedPreferences==null)
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean fetchBoolFromSharedPreferences(Context c, String key)
    {
        if(context==null)
            context =c;
        if(sharedPreferences==null)
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        boolean value = sharedPreferences.getBoolean(key,false);//False is the default value if there is not entry in shared preference with the given key
        return value;
    }

    public static String fetchStringFromSharedPreferences(Context c, String key)
    {
        if(context==null)
            context =c;
        if(sharedPreferences==null)
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String value = sharedPreferences.getString(key,null);//null is the default value if there is not entry in shared preference with the given key
        return value;
    }

    public static boolean appInstalledOrNot(Context c, String pkg)
    {
        if(context==null)
            context =c;

        PackageManager pm = context.getPackageManager();
        boolean app_installed =false;

        try {
            pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }

    public static void sendSMS(Context c)
    {
        Log.d("my_app","sendsms");
        if(context==null)
            context =c;

        String msgToSend = fetchStringFromSharedPreferences(context, AppSettings.SAVED_MSG_KEY);
        if(msgToSend==null || msgToSend=="")
            msgToSend = "Destination reached...";

        //Initialize the Sqlite DBHandler
        DBHandler dbHandler = new DBHandler(context);

        ArrayList<Contact> allContacts = new ArrayList<Contact>();
        allContacts = dbHandler.getAllContacts();
        try
        {
            SmsManager smsManager = SmsManager.getDefault();
            for (Contact contact:allContacts)
            {
                Log.d("my_app","contact in send SMS");
                smsManager.sendTextMessage(contact.getNumber(), null, msgToSend, null, null);
                Toast.makeText(context, "Message Sent to: "+contact.getNumber(),Toast.LENGTH_SHORT).show();

            }
        }
        catch (Exception ex)
        {
            //Toast.makeText(context,ex.getMessage().toString(),Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public static void sendWhatsappMsg(Context c, String pkgName, String message)
    {
        if(context==null)
            context =c;

        if(appInstalledOrNot(c,pkgName)) {

            //Initialize the Sqlite DBHandler
            DBHandler dbHandler = new DBHandler(context);

            ArrayList<Contact> allContacts = new ArrayList<Contact>();
            allContacts = dbHandler.getAllContacts();

            for (Contact contact: allContacts)
            {
                String phNum = contact.getNumber();
                if(phNum.contains("+"))
                {
                    String myNum = phNum.subSequence(1,phNum.length()).toString();
                    Log.d("MY_APP","myNumber:"+myNum);
                    /*
                    Uri uri = Uri.parse("smsto:" + phNum);
                    //Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage(pkgName);
                    context.startActivity(sendIntent);*/

                    /*Intent sendIntent = new Intent("android.intent.action.SEND");
                    //File f=new File("path to the file");
                    //Uri uri = Uri.fromFile(f);
                    sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.ContactPicker"));
                    sendIntent.setType("text/plain");
                    //sendIntent.putExtra(Intent.EXTRA_STREAM,uri);
                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(phNum)+"@s.whatsapp.net");
                    sendIntent.putExtra(Intent.EXTRA_TEXT,message);
                    context.startActivity(sendIntent);*/

                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sendIntent.putExtra("jid", myNum.trim() + "@s.whatsapp.net"); //phone number without "+" prefix
                    sendIntent.setPackage("com.whatsapp");
                    context.startActivity(sendIntent);
                }
                else
                {
                    Toast.makeText(context,"Contact doesn't contain country code:"+phNum,Toast.LENGTH_LONG).show();
                }
            }
        }
        else
            Toast.makeText(context,"WhatsApp not detected",Toast.LENGTH_LONG).show();
    }
}
