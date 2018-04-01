package com.example.shruthinarayan.lab2.databases;

/**
 * Created by shruthinarayan on 3/27/18.
 */

public final class MortgageInformation {

    private MortgageInformation() {}

    /* Inner class that defines the table contents */
    private static final String _ID = "NO";
    public static final String TABLE_NAME = "HOMEINFO";
    public static final String COLUMN1_NAME_TITLE = "TYPE";
    public static final String COLUMN2_NAME_TITLE = "ADDRESS";
    public static final String COLUMN3_NAME_TITLE = "CITY";
    public static final String COLUMN4_NAME_TITLE = "STATE";
    public static final String COLUMN5_NAME_TITLE = "ZIP";
    public static final String COLUMN6_NAME_TITLE = "PRICE";
    public static final String COLUMN7_NAME_TITLE = "DOWNPAYMENT";
    public static final String COLUMN8_NAME_TITLE = "RATE";
    public static final String COLUMN9_NAME_TITLE = "TERM";
    public static final String COLUMN10_NAME_TITLE = "MONTHLY";


    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + MortgageInformation.TABLE_NAME + " (" +
                    com.example.shruthinarayan.lab2.databases.MortgageInformation._ID + " INTEGER PRIMARY KEY," +
                    MortgageInformation.COLUMN1_NAME_TITLE + " TEXT," +
                    MortgageInformation.COLUMN2_NAME_TITLE + " TEXT," +
                    MortgageInformation.COLUMN3_NAME_TITLE + " TEXT," +
                    MortgageInformation.COLUMN4_NAME_TITLE + " TEXT," +
                    MortgageInformation.COLUMN5_NAME_TITLE + " TEXT," +
                    MortgageInformation.COLUMN6_NAME_TITLE + " TEXT," +
                    MortgageInformation.COLUMN7_NAME_TITLE + " TEXT," +
                    MortgageInformation.COLUMN8_NAME_TITLE + " TEXT," +
                    MortgageInformation.COLUMN9_NAME_TITLE + " TEXT," +
                    MortgageInformation.COLUMN10_NAME_TITLE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DELETE FROM " + MortgageInformation.TABLE_NAME + ";\n" +
                    "DELETE FROM sqlite_sequence where name=" + MortgageInformation.TABLE_NAME;
}
