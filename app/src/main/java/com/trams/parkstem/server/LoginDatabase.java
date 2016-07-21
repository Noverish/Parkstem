package com.trams.parkstem.server;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Created by Noverish on 2016-07-21.
 */
public class LoginDatabase {
    private Context context;

    private static final String DATABASE_FILE_NAME = "login_database";

    private static final String ID_COLUMN = "id";
    private static final String PW_COLUMN = "pw";
    private HashMap<String, String> database = new HashMap<>();

    private static LoginDatabase loginDatabase;
    public static LoginDatabase getInstance(Context context) {
        if(loginDatabase == null)
            loginDatabase = new LoginDatabase(context);

        return loginDatabase;
    }

    private LoginDatabase(Context context) {
        this.context = context;

        database = readFromInternalStorage();
        if(database == null)
            database = new HashMap<>();
    }


    public void setData(String id) {
        setData(id, "");
    }

    public void setData(String id, String pw) {
        Log.e("setData",id + " " + pw);

        database.clear();

        database.put(ID_COLUMN, id);
        if(!pw.equals(""))
            database.put(PW_COLUMN, pw);

        saveToInternalStorage(database);
    }

    public String getId() {
        return database.get(ID_COLUMN);
    }

    public String getPw() {
        if(database.size() == 2)
            return database.get(PW_COLUMN);
        else
            return "";
    }

    public boolean isDatabaseClear() {
        return (database == null ||database.size() == 0);
    }

    private void saveToInternalStorage(HashMap<String, String> map) {
        context.deleteFile(DATABASE_FILE_NAME);
        try {
            FileOutputStream fos = context.openFileOutput(DATABASE_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream of = new ObjectOutputStream(fos);
            of.writeObject(map);
            of.flush();
            of.close();
            fos.close();
        }
        catch (Exception e) {
            Log.e("InternalStorage", e.getMessage());
        }
    }

    private HashMap<String, String> readFromInternalStorage() {
        HashMap<String, String> toReturn = null;

        try {
            FileInputStream fis;
            fis = context.openFileInput(DATABASE_FILE_NAME);
            ObjectInputStream oi = new ObjectInputStream(fis);
            toReturn = (HashMap<String, String>) oi.readObject();
            oi.close();
        } catch (Exception e) {
            Log.e("InternalStorage", e.getMessage());
        }
        return toReturn;
    }

    public void clearDatabase() {
        context.deleteFile(DATABASE_FILE_NAME);
    }
}
