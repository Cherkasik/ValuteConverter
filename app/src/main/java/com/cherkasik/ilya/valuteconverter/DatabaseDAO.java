package com.cherkasik.ilya.valuteconverter;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseDAO extends SQLiteOpenHelper {
    private static final String LOG_TAG = DatabaseDAO.class.getSimpleName();
    private static final String databaseName = "historyDatabase";
    private static final String tableName = "history";
    private static final int databaseVersion = 1;
    private static DatabaseDAO sInstance;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createHistoryTable = "CREATE TABLE IF NOT EXISTS " + tableName + "(`_id` INTEGER PRIMARY KEY, `conv_from` TEXT, `conv_to` TEXT, `num` FLOAT, `res` FLOAT, `date` DATE)";
        db.execSQL(createHistoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
        onCreate(db);
    }

    private DatabaseDAO(Context context){
        super(context, databaseName, null, databaseVersion);
    }

    static synchronized DatabaseDAO getsInstance(Context context){
        if (sInstance == null){
            sInstance = new DatabaseDAO(context.getApplicationContext());
        }
        return sInstance;
    }

    void addHistory(HistoryObject historyObject){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        if (getHistory().size() == 10){
            rewriteHistory(historyObject, db);
            db.setTransactionSuccessful();
        }
        else{
            addToHistory(historyObject, db);
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }

    private void addToHistory(HistoryObject historyObject, SQLiteDatabase db){
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put("conv_from", historyObject.conv_from);
            contentValues.put("conv_to", historyObject.conv_to);
            contentValues.put("num", historyObject.num);
            contentValues.put("res", historyObject.res);
            contentValues.put("date", historyObject.date);
            db.insertOrThrow(tableName, null, contentValues);
        }
        catch(Exception e){
            Log.d(LOG_TAG, "Something wrong with updating history" + e);
        }
    }

    private void rewriteHistory(HistoryObject historyObject, SQLiteDatabase db){
        List<HistoryObject> historyObjects = getHistory();
        deleteHistory();
        for (int i = 1; i < historyObjects.size(); i++){
            addToHistory(historyObjects.get(i), db);
        }
        addToHistory(historyObject, db);
    }

    void deleteHistory() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(tableName, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error while deleting history");
        } finally {
            db.endTransaction();
        }
    }

    List<HistoryObject> getHistory(){
        List<HistoryObject> historyObjects;
        historyObjects = new ArrayList<>();
        String historySelect = "SELECT * FROM " + tableName;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(historySelect, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    HistoryObject newHistoryObject = new HistoryObject();
                    newHistoryObject.conv_from = cursor.getString(cursor.getColumnIndex("conv_from"));
                    newHistoryObject.conv_to = cursor.getString(cursor.getColumnIndex("conv_to"));
                    newHistoryObject.num = cursor.getFloat(cursor.getColumnIndex("num"));
                    newHistoryObject.res = cursor.getFloat(cursor.getColumnIndex("res"));
                    newHistoryObject.date = cursor.getString(cursor.getColumnIndex("date"));
                    historyObjects.add(newHistoryObject);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error getting history");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return historyObjects;
    }
}
