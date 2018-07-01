package com.udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunand on 6/22/18.
 */

public class FavoritesDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_FAVORITES_TABLE =

                "CREATE TABLE " + FavoritesContract.FavoritesEntry.TABLE_NAME + " (" +

                        FavoritesContract.FavoritesEntry._ID               + " INTEGER PRIMARY KEY, " +
                        FavoritesContract.FavoritesEntry.COLUMN_TITLE       + " TEXT NOT NULL, "                 +
                        FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT, "                 +
                        FavoritesContract.FavoritesEntry.COLUMN_VOTE_AVERAGE   + " REAL, "                    +
                        FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW   + " TEXT, "                    +
                        FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH    + " TEXT" + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ FavoritesContract.FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
