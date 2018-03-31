package com.example.shruthinarayan.lab2.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.shruthinarayan.lab2.Data;

import java.util.ArrayList;

/**
 * Created by shruthinarayan on 3/27/18.
 */

public class HomeDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HomeDatabase.db";

    public HomeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HomeDatabase.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(HomeDatabase.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void truncateTable(SQLiteDatabase db)
    {
        db.execSQL(HomeDatabase.SQL_DELETE_ENTRIES);
    }

    public void insertHome(Data data) throws Exception {

        SQLiteDatabase db = this.getWritableDatabase();
        this.onCreate(db);

        ContentValues values = new ContentValues();
        values.put(HomeDatabase.COLUMN1_NAME_TITLE, data.getType());
        values.put(HomeDatabase.COLUMN2_NAME_TITLE, data.getAddress());
        values.put(HomeDatabase.COLUMN3_NAME_TITLE, data.getCity());
        values.put(HomeDatabase.COLUMN4_NAME_TITLE, data.getState());
        values.put(HomeDatabase.COLUMN5_NAME_TITLE, data.getZip());
        values.put(HomeDatabase.COLUMN6_NAME_TITLE, data.getPropertyprice());
        values.put(HomeDatabase.COLUMN7_NAME_TITLE, data.getDownpayment());
        values.put(HomeDatabase.COLUMN8_NAME_TITLE, data.getRate());
        values.put(HomeDatabase.COLUMN9_NAME_TITLE, data.getTerms());
        values.put(HomeDatabase.COLUMN10_NAME_TITLE, data.getMonthlyPayments());

        long newRowId = db.insert(HomeDatabase.TABLE_NAME, null, values);

        db.close();
    }

    public ArrayList<Data> retrieveHomes(){

        ArrayList<Data> homelist = new ArrayList<Data>();
        String temp[] = new String[10];
        SQLiteDatabase db = this.getReadableDatabase();
        this.onCreate(db);

        try {
            Cursor c = db.rawQuery("SELECT * FROM " + HomeDatabase.TABLE_NAME, null);

            if (c.moveToFirst()) {
                do {


                    Data home = new Data();
                    // Assigning row fields to variables ...
                    temp[0] = c.getString(1);
                    temp[1] = c.getString(2);
                    temp[2] = c.getString(3);
                    temp[3] = c.getString(4);
                    temp[4] = c.getString(5);
                    temp[5] = c.getString(6);
                    temp[6] = c.getString(7);
                    temp[7] = c.getString(8);
                    temp[8] = c.getString(9);
                    temp[9] = c.getString(10);

                    home.setFields(temp);
                    homelist.add(home);
                    System.out.println("Address when reading" + home.getAddress());
                } while (c.moveToNext());
            }
            c.close();

            db.close();
        }catch (Exception e){
            Log.e("Unable to read","solve the issue");
        }
        return homelist;
    }
    public void deleteHome(String address){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            this.onCreate(db);
            String[] whereArgs = new String[] { String.valueOf(address) };
            db.delete(HomeDatabase.TABLE_NAME, HomeDatabase.COLUMN2_NAME_TITLE +"=?", whereArgs);
            db.close();
        }catch (Exception e){
            Log.e("Unable to delete","solve the issue");
        }
    }
    public void updateHome(String[] val,String address){
        try {
            System.out.println("Entered");
            SQLiteDatabase db = this.getReadableDatabase();
            this.onCreate(db);
            ContentValues values = new ContentValues();
            values.put(HomeDatabase.COLUMN1_NAME_TITLE, val[0]);
            values.put(HomeDatabase.COLUMN3_NAME_TITLE,val[2]);
            values.put(HomeDatabase.COLUMN4_NAME_TITLE,val[3]);
            values.put(HomeDatabase.COLUMN5_NAME_TITLE,val[4]);
            values.put(HomeDatabase.COLUMN6_NAME_TITLE,val[5]);
            values.put(HomeDatabase.COLUMN7_NAME_TITLE, val[6]);
            values.put(HomeDatabase.COLUMN8_NAME_TITLE, val[7]);
            values.put(HomeDatabase.COLUMN9_NAME_TITLE,val[8] );
            values.put(HomeDatabase.COLUMN10_NAME_TITLE,val[9]);
            String[] whereArgs = new String[] { String.valueOf(address) };
            db.update(HomeDatabase.TABLE_NAME, values, HomeDatabase.COLUMN2_NAME_TITLE +"=?", whereArgs);
            db.close();
        }catch (Exception e){
            Log.e("Unable to delete","solve the issue");
        }
    }





}
