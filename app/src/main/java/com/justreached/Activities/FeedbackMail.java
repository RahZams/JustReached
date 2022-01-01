package com.justreached.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.justreached.R;

public class FeedbackMail extends AppCompatActivity {

    EditText emailid,feedback,subject;
//    String possibleEmail="";
    String From,Message,Sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_mail);
        emailid = (EditText) findViewById(R.id.emailaddr);
        feedback = (EditText) findViewById(R.id.feedback);
        subject = (EditText) findViewById(R.id.subject);

        From = emailid.getText().toString();
        Message = feedback.getText().toString();
        Sub = subject.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{From});
        intent.putExtra(Intent.EXTRA_SUBJECT, Sub);
        intent.putExtra(Intent.EXTRA_TEXT, Message);

        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Select Email Sending App :"));
//        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
//       Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
//        for (Account account : accounts)
//        {
//            Log.d("my_app","in for loop");
//            // TODO: Check possibleEmail against an email regex or treat
//
//            // account.name as an email address only for certain account.type values.
//            possibleEmail = account.name;
//            Log.d("my_app",possibleEmail);
//
//        }
//        emailid.setText(possibleEmail);



    }
}
