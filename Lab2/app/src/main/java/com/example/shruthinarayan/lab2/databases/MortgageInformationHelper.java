package com.example.shruthinarayan.lab2.databases;

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

public class MortgageInformationHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MortgageInformation.db";
    private static final int DATABASE_VERSION = 1;

    public MortgageInformationHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MortgageInformation.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(MortgageInformation.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void truncateTable(SQLiteDatabase db) {
        db.execSQL(MortgageInformation.SQL_DELETE_ENTRIES);
    }

    public void insertHome(Data data) throws Exception {

        SQLiteDatabase db = this.getWritableDatabase();
        this.onCreate(db);

        ContentValues values = new ContentValues();
        values.put(MortgageInformation.COLUMN1_NAME_TITLE, data.getType());
        values.put(MortgageInformation.COLUMN2_NAME_TITLE, data.getAddress());
        values.put(MortgageInformation.COLUMN3_NAME_TITLE, data.getCity());
        values.put(MortgageInformation.COLUMN4_NAME_TITLE, data.getState());
        values.put(MortgageInformation.COLUMN5_NAME_TITLE, data.getZip());
        values.put(MortgageInformation.COLUMN6_NAME_TITLE, data.getPropertyprice());
        values.put(MortgageInformation.COLUMN7_NAME_TITLE, data.getDownpayment());
        values.put(MortgageInformation.COLUMN8_NAME_TITLE, data.getRate());
        values.put(MortgageInformation.COLUMN9_NAME_TITLE, data.getTerms());
        values.put(MortgageInformation.COLUMN10_NAME_TITLE, data.getEMI());

        long newRowId = db.insert(MortgageInformation.TABLE_NAME, null, values);

        db.close();
    }

    public ArrayList<Data> obtainMortgageDatabase() {

        ArrayList<Data> homelist = new ArrayList<Data>();
        String tempData[] = new String[10];
        SQLiteDatabase db = this.getReadableDatabase();
        this.onCreate(db);

        try {
            Cursor cursorObj = db.rawQuery("SELECT * FROM " + MortgageInformation.TABLE_NAME, null);

            if (cursorObj.moveToFirst()) {
                do {
                    Data mortgageInfoData = new Data();
                    // Assigning row fields to variables ...
                    tempData[0] = cursorObj.getString(1);
                    tempData[1] = cursorObj.getString(2);
                    tempData[2] = cursorObj.getString(3);
                    tempData[3] = cursorObj.getString(4);
                    tempData[4] = cursorObj.getString(5);
                    tempData[5] = cursorObj.getString(6);
                    tempData[6] = cursorObj.getString(7);
                    tempData[7] = cursorObj.getString(8);
                    tempData[8] = cursorObj.getString(9);
                    tempData[9] = cursorObj.getString(10);

                    mortgageInfoData.setData(tempData);
                    homelist.add(mortgageInfoData);
                    System.out.println("Address" + mortgageInfoData.getAddress());
                } while (cursorObj.moveToNext());
            }
            cursorObj.close();

            db.close();
        } catch (Exception e) {
            Log.e("Can not read", "Fix");
        }
        return homelist;
    }

    public void deleteHome(String address) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] whereArgs = new String[]{String.valueOf(address)};
            this.onCreate(db);
            db.delete(MortgageInformation.TABLE_NAME, MortgageInformation.COLUMN2_NAME_TITLE + "=?", whereArgs);
            db.close();
        } catch (Exception e) {
            Log.e("Unable to delete", "solve the issue");
        }
    }

    public void updateHome(String[] val, String address) {
        try {
            System.out.println("Entered");
            SQLiteDatabase db = this.getReadableDatabase();
            this.onCreate(db);
            ContentValues values = new ContentValues();
            values.put(MortgageInformation.COLUMN1_NAME_TITLE, val[0]);
            values.put(MortgageInformation.COLUMN3_NAME_TITLE, val[2]);
            values.put(MortgageInformation.COLUMN4_NAME_TITLE, val[3]);
            values.put(MortgageInformation.COLUMN5_NAME_TITLE, val[4]);
            values.put(MortgageInformation.COLUMN6_NAME_TITLE, val[5]);
            values.put(MortgageInformation.COLUMN7_NAME_TITLE, val[6]);
            values.put(MortgageInformation.COLUMN8_NAME_TITLE, val[7]);
            values.put(MortgageInformation.COLUMN9_NAME_TITLE, val[8]);
            values.put(MortgageInformation.COLUMN10_NAME_TITLE, val[9]);
            String[] whereArgs = new String[]{String.valueOf(address)};
            db.update(MortgageInformation.TABLE_NAME, values, MortgageInformation.COLUMN2_NAME_TITLE + "=?", whereArgs);
            db.close();
        } catch (Exception e) {
            Log.e("Unable to delete", "solve the issue");
        }
    }


}
