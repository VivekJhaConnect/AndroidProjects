package com.exmple.sdcard.jsonuseasdatabaseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ContactViewModel contactViewModel;
    private ContactAdapter contactAdapter;
    private List<Contact> contacts;
    private String filename = "contact_list.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        contactAdapter = new ContactAdapter();
        recyclerView.setAdapter(contactAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                return;
            }
        }

        loadDataAndView();
    }

    private void loadDataAndView() {
        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        contactViewModel.loadData(filename);
        loadAllContain();

        Button addBtn = findViewById(R.id.btn_add);
        addBtn.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }

            try {

                Contact contact = new Contact("Vivek K Jha", "6787765667");
                JSONArray addNewObject = ContactDatabase.addNewContact(filename, contact);
                ContactDatabase.saveAllContact(filename, addNewObject);
                JSONArray jsonArrayResult = ContactDatabase.readJSONFileData(filename);
                for (int i = 0; i < jsonArrayResult.length(); i++) {
                    JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                    String contactName = jsonObject.getString("name");
                    String contactPhone = jsonObject.getString("phone");
                    Log.d("eclipse", "contactName: " + contactName + " contactPhone: " + contactPhone);
                }
                contactViewModel.insert(contact);
                loadAllContain();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        Button deleteBtn = findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
            }

            try {
                String filename = "contact_list.json";
                JSONArray jsonArray = ContactDatabase.readJSONFileData(filename);
                int index = jsonArray.length() - 1;
                JSONArray addNewObject = ContactDatabase.deleteNewContact(filename, index);
                ContactDatabase.saveAllContact(filename, addNewObject);
                JSONArray jsonArrayResult = ContactDatabase.readJSONFileData(filename);
                for (int i = 0; i < jsonArrayResult.length(); i++) {
                    JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                    String contactName = jsonObject.getString("name");
                    String contactPhone = jsonObject.getString("phone");
                    Log.d("eclipse", "contactName: " + contactName + " contactPhone: " + contactPhone);
                }
                contactViewModel.delete(MainActivity.this.contacts.get(index));
                loadAllContain();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadAllContain() {
        contactViewModel.getAllContacts().observe(this, contacts -> {
            MainActivity.this.contacts = contacts;
            contactAdapter.setContacts(contacts);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        loadDataAndView();
    }
}