package com.alp.ir.viradictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dictionary.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private final String databasePath;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // نیازی به پیاده‌سازی نیست
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // نیازی به پیاده‌سازی نیست
    }

    public void checkAndCopyDatabase() {
        File databaseFile = new File(databasePath);
        if (!databaseFile.exists()) {
            copyDatabase();
        }
    }

    private void copyDatabase() {
        try {
            InputStream input = context.getAssets().open(DATABASE_NAME);
            File outputDir = new File(databasePath).getParentFile();
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            OutputStream output = new FileOutputStream(databasePath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SQLiteDatabase openDatabase() {
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
    }
}
