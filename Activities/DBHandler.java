package com.example.erikj.sensor_standoffapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static android.provider.Contacts.SettingsColumns.KEY;
import static android.provider.Telephony.Mms.Part.TEXT;

/**
 * Created by VaskSlem on 2018-02-21.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "dbScore";
    // Contacts table name
    private static final String TABLE_SCORE = "Score";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SCORE = "score";
    private static final String KEY_NAME = "name";

    public DBHandler(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SCORE + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SCORE + " TEXT,"
        + KEY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
// Creating tables again
        onCreate(db);
    }


    /**
     * -------------------------------------------------------------
     *
     * All CRUD methods.
     * (Create, Read, Update, Delete)
     *
     * -------------------------------------------------------------
     */

    public void addScore(Score score){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_SCORE, score.getScore());
        values.put(KEY_NAME, score.getName());

        db.insert(TABLE_SCORE, null, values);
        db.close();
    }

    public String getHighScore(){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT MAX(" + KEY_SCORE + "), " + KEY_NAME + " FROM " + TABLE_SCORE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        String score = cursor.getString(0) + "     " + cursor.getString(1);
        return score;
    }

    public Score getScore(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCORE, new String[] {KEY_ID, KEY_SCORE, KEY_NAME}, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        Score score = new Score(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2));
        return score;
    }

    public List<String> getAllScores(){
        List<String> scoreList = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + TABLE_SCORE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{

                scoreList.add(cursor.getString(1) + "    " + cursor.getString(2));
            } while(cursor.moveToNext());
        }

        return scoreList;
    }

    public int getNumOfScores(){
        String countQuery = "SELECT * FROM " + TABLE_SCORE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public void deleteScore(Score score){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCORE, KEY_ID + "=?", new String[] {String.valueOf(score.getId())});
        db.close();
    }
}
