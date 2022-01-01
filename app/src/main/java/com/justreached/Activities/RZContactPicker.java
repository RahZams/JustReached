package com.justreached.Activities;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.justreached.Adapters.MyCustomAdapter;
import com.justreached.Others.HelperMethods;
import com.justreached.Models.Contact;
import com.justreached.R;
import com.justreached.Sqlite.DBHandler;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RZContactPicker extends Fragment {

    public final int PICK_CONTACT = 500;
    public DBHandler dbHandler=null;
    public MyCustomAdapter adapter=null;
    public ArrayList<Contact> contactsInSqlite =null;
    public String Name = null;
    public String PhoneNumber =null;
    public String email = null;
    public String mSate;
    Toolbar toolbar;
    public RZContactPicker() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rzcontact_picker, container, false);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        mSate = "HIDE_MENU";
//        getActivity().invalidateOptionsMenu();

        ListView listView = (ListView) view.findViewById(R.id.userContactsList);

        //Initialize the Sqlite DBHandler
        dbHandler = new DBHandler(getActivity());

        // Create an empty array list of strings
        contactsInSqlite = new ArrayList<Contact>();
        contactsInSqlite = dbHandler.getAllContacts();
        if(contactsInSqlite.size() == 0){
            mSate = "hide";
            Log.d("my_app","size is zero");
            getActivity().invalidateOptionsMenu();
        }
        else{
            mSate = "show";
            Log.d("my_app","size is not zero");
            getActivity().invalidateOptionsMenu();
        }
        //items.add(new Contact("Syed","8123412756"));
        //items.add(new Contact("Zakriya","9916642316"));
        //items.add(new Contact("Rahath","9972378396"));
        //items.add(new Contact("Zama","4561237890"));
        //items.add(new Contact("R","9876541230"));

//        if (contactsInSqlite !=null){
//            Log.d("my_app","not null");
//
//            mSate = "SHOW_MENU";
//           getActivity().invalidateOptionsMenu();
//        }

        final FloatingActionButton myFloatBtn = (FloatingActionButton) view.findViewById(R.id.contactsFAB);
        myFloatBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF69B4")));
        myFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity(),"Floating button clicked",Toast.LENGTH_LONG).show();
                launchContactPicker();
            }
        });

        myFloatBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    myFloatBtn.setImageResource(R.drawable.float_action_btn_pressed);
                    myFloatBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7f7fff")));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    myFloatBtn.setImageResource(R.drawable.float_action_btn);
                    myFloatBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF69B4")));
                }
                return false;
            }
        });

//        // Set the adapter
//        adapter = new MyCustomAdapter(getActivity(),contactsInSqlite);
//       listView.setAdapter(adapter);
//       mSate = "SHOW_MENU";
//       getActivity().invalidateOptionsMenu();
//        // Set the emptyView to the ListView
//        listView.setEmptyView(view.findViewById(R.id.textView5));
//       mSate = "HIDE_MENU";
//        getActivity().invalidateOptionsMenu();

      listView.setEmptyView(view.findViewById(R.id.textView5));
//     mSate = "HIDE_MENU";
//        getActivity().invalidateOptionsMenu();
//
        adapter = new MyCustomAdapter(getActivity(),contactsInSqlite);
        listView.setAdapter(adapter);
//       mSate = "SHOW_MENU";
//       getActivity().invalidateOptionsMenu();


        listView.setLongClickable(true);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                showDeleteContactDialog(index);
                return true;
            }
        });




        return view;
    }

    private void contactPicked(Intent data) {
        //Getting details like name, number and email from the picked contact
        ContentResolver cr = getActivity().getContentResolver();
        try {
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            Cursor cur = cr.query(uri, null, null, null, null);
            cur.moveToFirst();
            // column index of the contact ID
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            // column index of the contact name
            Name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                  //print data
            // column index of the phone number
            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                    new String[]{id}, null);
            while (pCur.moveToNext()) {
                PhoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //print data
            }
            pCur.close();
            // column index of the email
            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[]{id}, null);
            while (emailCur.moveToNext()) {
                // This would allow you get several email addresses
                // if the email addresses were stored in an array
                email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                //print data
            }
            emailCur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK)
        {
            contactPicked(data);
            /*
            Uri contactUri = data.getData();
            Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();

            Name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            int phoneNumberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            PhoneNumber =cursor.getString(phoneNumberColumn);*/

            /*
            Uri contactUri = data.getData();
            Cursor nameNumberCursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
            nameNumberCursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =nameNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =nameNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            PhoneNumber = nameNumberCursor.getString(phoneIndex);
            Name = nameNumberCursor.getString(nameIndex);

            if(nameNumberCursor!=null)
                nameNumberCursor.close();

            Uri result = data.getData();
            Log.v("MY_APP", "Got a result: "+ result.toString());

            // get the contact id from the Uri
            String id = result.getLastPathSegment();


            // query for everything email
            Cursor emailCursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                    new String[]{id}, null);

            int emailIdx = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

            // let's just get the first email
            if (emailCursor.moveToFirst()) {
                email = emailCursor.getString(emailIdx);
                Log.v("MY_APP", "Got email: " + email);
            } else {
                Log.w("MY_APP", "No results");
            }

            if (emailCursor != null) {
                emailCursor.close();
            }*/

            Log.d("MY_APP","Name: "+Name);
            Log.d("MY_APP","phone number: "+PhoneNumber);
            Log.d("MY_APP","Email: "+email);
            showAddContactDialog();
        }
    }

    public void launchContactPicker()
    {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT);
    }

    public void showAddContactDialog()
    {
        AlertDialog.Builder alertDlgBuilder = HelperMethods.createAlertDialog(getActivity(),0,false,"Would you like to add the Contact to the application?\n\nName: "+Name+"\nPhone Number: "+PhoneNumber+"\nEmail Id: "+email,"Confirmation!!!");
        alertDlgBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                //Check if this contact is already in our contact list
                ArrayList<Contact> myContacts = dbHandler.getAllContacts();
                Contact contactToBeAdded = new Contact(Name,PhoneNumber,email);
                boolean contactExists = false;
                for(Contact c:myContacts)
                {
                    if((c.getNumber().equals(PhoneNumber)))
                    {
                        contactExists = true;
                        break;
                    }
                }
                if(!contactExists) {
                    dbHandler.addContact(new Contact(Name, PhoneNumber, email));
                    contactsInSqlite.add(new Contact(Name, PhoneNumber, email));
                    refreshListView();
                    mSate = "show";
                    getActivity().invalidateOptionsMenu();
                    dialog.dismiss();
                }
                else{
                    dialog.dismiss();
                    AlertDialog.Builder bldr = HelperMethods.createAlertDialog(getActivity(),0,false,"The contact number: "+PhoneNumber+" already exists in the list.","Cannot add Contact!!!");
                    bldr.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog myAlrtDialog = bldr.create();
                    myAlrtDialog.show();
                }

            }
        });
        alertDlgBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDlgBuilder.create();
        alertDialog.show();
    }

    public void showDeleteContactDialog(final int index)
    {
        final Contact contact = contactsInSqlite.get(index);
        AlertDialog.Builder alertDlgBuilder = HelperMethods.createAlertDialog(getActivity(),0,false,"Would you like to delete this Contact from the application?\n\nName: "+contact.getName()+"\nPhone Number: "+contact.getNumber(),"Confirmation!!!");
        alertDlgBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dbHandler.deleteContact(contact);
                contactsInSqlite.remove(index);
                refreshListView();
                if(contactsInSqlite.size() == 0){
                    Log.d("my_app","size is zero after delete");
                    mSate = "hide";
                    getActivity().invalidateOptionsMenu();
                }
                dialog.dismiss();
            }
        });
        alertDlgBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDlgBuilder.create();
        alertDialog.show();


    }

    public void refreshListView()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d("my_app","options menu" + mSate);
        ((AppCompatActivity) getActivity()).getSupportActionBar();
        inflater.inflate(R.menu.contact_toolbar_menu,menu);
        if(mSate == "hide"){
            Log.d("my_app",mSate);
            menu.findItem(R.id.done).setVisible(false);
        }
        else if(mSate == "show"){
            menu.findItem(R.id.done).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.done){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new HomeFragment()).addToBackStack(null).commit();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Maps");

        }
        return true;
    }

    @Override
    public void onResume() {
        toolbar.setTitle("Contacts");
        super.onResume();
    }

}
