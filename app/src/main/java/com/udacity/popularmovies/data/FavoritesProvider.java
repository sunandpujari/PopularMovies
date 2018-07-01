package com.udacity.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by sunand on 6/24/18.
 */

public class FavoritesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoritesDBHelper mOpenHelper;

    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITES_WITH_ID = 101;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoritesContract.FavoritesEntry.TABLE_NAME, CODE_FAVORITES);
        matcher.addURI(authority, FavoritesContract.FavoritesEntry.TABLE_NAME + "/#", CODE_FAVORITES_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoritesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri))
        {
            case CODE_FAVORITES_WITH_ID: {
                String _ID = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{_ID};

                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        FavoritesContract.FavoritesEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_FAVORITES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        throw new RuntimeException("not implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri newUri;

        switch (sUriMatcher.match(uri)) {

            case CODE_FAVORITES:
                db.beginTransaction();
                long _id = 0;
                try {
                    _id = db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, contentValues);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                // notify all listeners of changes:
                if (_id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);

                return FavoritesContract.FavoritesEntry.buildUriWithId(_id);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_FAVORITES:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new RuntimeException("not implemented");
    }
}
