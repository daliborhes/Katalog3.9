package com.example.dalibor.katalog;

/**
 * Created by Employee on 27.11.2017..
 */
import java.util.ArrayList;
import java.util.Collections;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static android.Manifest.permission.READ_CONTACTS;

public class ContactsActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 444;
    private ListView lvContacts;
    private ProgressDialog progressDialog;
    private Handler updateBarHandler;
    private ArrayList<String> contactList;
    private ArrayAdapter <String> adapter;
    Cursor cursor;
    int counter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        progressDialog = new ProgressDialog(ContactsActivity.this);
        progressDialog.setMessage("Reading contacts...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        lvContacts = (ListView) findViewById(R.id.lvContacts);
        updateBarHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getContacts();
            }
        }).start();

        lvContacts.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intentContact = new Intent(ContactsActivity.this, SelectedContact.class);
                intentContact.putExtra("ContactData", adapter.getItem(position));
                startActivity(intentContact);
            }

        });
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            }
        }
    }
    public void getContacts() {
        if (!mayRequestContacts()) {
            return;
        }
        contactList = new ArrayList<String>();
        String phoneNumber = null;
        String email = null;

        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor.getCount() > 0) {
            //counter = 0;
            while (cursor.moveToNext()) {
                StringBuffer output = new StringBuffer();
                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    output.append("Name: " + name);

                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contact_id},
                            ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC");

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        output.append("\nPhone number: " + phoneNumber);
                    }
                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{contact_id},
                            null);

                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        output.append("\nEmail: " + email);
                    }
                    emailCursor.close();
                }
                contactList.add(output.toString());
                Collections.sort(contactList, String.CASE_INSENSITIVE_ORDER);

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.row_contact,
                            R.id.displayTxt,
                            contactList);
                    lvContacts.setAdapter(adapter);
                }
            });

            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            }, 500);
        }
    }
}