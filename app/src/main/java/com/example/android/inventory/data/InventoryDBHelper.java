package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME ="library.db";
    private static final int DATABASE_VERSION = 1 ;
    public InventoryDBHelper(Context context){
        super(context,DATABASE_NAME, null ,DATABASE_VERSION
        );
    }

    @Override
    public void onCreate(SQLiteDatabase librarydb) {
        String SQL_CREATE_BOOK_TABLE =  "CREATE TABLE " + InventoryContract.BookEntry.TABLE_NAME + "("
                + InventoryContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + InventoryContract.BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryContract.BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT, "
                + InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + " TEXT);";
        librarydb.execSQL(SQL_CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase librarydb, int oldVersion, int newVersion) {

    }
}
