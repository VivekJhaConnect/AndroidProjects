package com.exmple.sdcard.jsonuseasdatabaseproject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactDatabase extends RoomDatabase {
    private static ContactDatabase instance;
    private static Context activity;
    private static String filename;

    public abstract ContactDao contactDao();

    public static synchronized ContactDatabase getInstance(Context context, String _filename) {
        activity = context.getApplicationContext();
        filename = _filename;
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ContactDatabase.class, "contact_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance, filename).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        public ContactDao contactDao;
        public String filename;

        public PopulateDBAsyncTask(ContactDatabase db, String filename) {
            contactDao = db.contactDao();
            this.filename = filename;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fillWithStartingData(activity, filename);
            return null;
        }
    }

    public static void fillWithStartingData(Context context, String filename) {
        ContactDao dao = getInstance(context, filename).contactDao();
        JSONArray contacts = readJSONFileData(filename);
        Log.d("eclipse", "fillWithStartingData: " + contacts.length());
        try {
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject jsonObject = contacts.getJSONObject(i);
                String contactName = jsonObject.getString("name");
                String contactPhone = jsonObject.getString("phone");
                Log.d("eclipse", "contactName: " + contactName + " contactPhone: " + contactPhone);
                dao.insert(new Contact(contactName, contactPhone));
            }
        } catch (JSONException e) {
            Log.d("eclipse", "Error 2: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static JSONArray deleteNewContact(String filename, int index) {
        JSONArray contacts = readJSONFileData(filename);
        final List<JSONObject> objs = getList(contacts);
        objs.remove(index);

        final JSONArray jarray = new JSONArray();
        for (final JSONObject obj : objs) {
            jarray.put(obj);
        }

        return jarray;
    }

    public static List<JSONObject> getList(final JSONArray jarray) {
        final int len = jarray.length();
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = jarray.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }

    public static JSONArray addNewContact(String filename, Contact contact) {
        JSONArray contacts = readJSONFileData(filename);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", contact.getName());
            jsonObject.put("phone", contact.getNumber());
            contacts.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public static void saveAllContact(String filename, JSONArray contacts) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("contacts", contacts);
            File sdcard = Environment.getExternalStorageDirectory();
            File dir = new File(sdcard.getAbsolutePath() + "/DemoTest/");
            dir.mkdir();
            File file = new File(dir, filename);


            // get external storage file reference
            FileWriter writer = new FileWriter(file);
            // Writes the content to the file
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray readJSONFileData(String fileName) {

        // Get the dir of SD Card
        File sdcard = Environment.getExternalStorageDirectory();
        File dir = new File(sdcard.getAbsolutePath(), "DemoTest");
        if (!dir.exists())
            dir.mkdir();
        // Get The Text file
        File txtFile = new File(dir, fileName);
        boolean isFileCreated = dir.exists();
        // Read the file Contents in a StringBuilder Object
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(txtFile));

            String line;

            while ((line = reader.readLine()) != null) {
                text.append(line + '\n');
            }
            reader.close();
        } catch (IOException e) {
            Log.e("C2c", "Error occured while reading text file!!");
        }
//        return text.toString();
        JSONObject json = null;
        try {
            json = new JSONObject(text.toString());
            return json.getJSONArray("contacts");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
