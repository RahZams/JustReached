package com.justreached.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.justreached.Models.Contact;

import java.util.ArrayList;

/**
 * Created by SYED_ZAKRIYA on 3/8/2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "ContactsDB";
    private static final String TABLE_NAME = "Contacts";
    private static final String KEY_ID = "Id";
    private static final String KEY_NAME = "Name";
    private static final String KEY_PH_NO = "PhoneNumber";
    private static final String KEY_EMAIL_ID = "EmailId";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("MY_APP","onCreate method called");
        //Create the Database Table here
        final String CREATE_DB_STRING = "create table "+TABLE_NAME+"("+ KEY_ID +" integer primary key autoincrement, "+KEY_NAME + " TEXT," + KEY_PH_NO + " TEXT," + KEY_EMAIL_ID + " TEXT);";
        db.execSQL(CREATE_DB_STRING);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d("MY_APP","onUpgrade Called");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addContact(Contact contact) {
        Log.d("my_app","add contact");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getNumber()); // Contact Phone
        values.put(KEY_EMAIL_ID,contact.getEmailId());
        Log.d("my_app",contact.getName());
        Log.d("my_app",String.valueOf(values.get(KEY_PH_NO)));

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    public Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                        KEY_NAME, KEY_PH_NO,KEY_EMAIL_ID }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        return contact;
    }

    // code to get all contacts in a list view
    public ArrayList<Contact> getAllContacts() {
        Log.d("my_app","get all contacts");
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "Select * from " + TABLE_NAME;
        Log.d("my_app",selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.d("my_app",String.valueOf(cursor.getCount()));

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            Log.d("my_app","inside cursor");
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setNumber(cursor.getString(2));
                contact.setEmailId(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);

            } while (cursor.moveToNext());
        }
        // return contact list
        return contactList;
    }

    // code to update the single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getNumber());
        values.put(KEY_EMAIL_ID, contact.getEmailId());

        // updating row
        return db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
