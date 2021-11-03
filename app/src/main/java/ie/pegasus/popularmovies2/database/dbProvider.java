package ie.pegasus.popularmovies2.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Pegasus May 2017.
 * This is the ContentProvider for the Favourite Movies database in the Popular Movies Part 2 Application
 */

    public class dbProvider extends ContentProvider {

        private static final UriMatcher sUriMatcher = buildUriMatcher();
        private dbHelper mOpenHelper;

        static final int MOVIE = 100;

        static UriMatcher buildUriMatcher() {
            final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
            final String authority = dbContract.CONTENT_AUTHORITY;

            // For each type of URI you want to add, create a corresponding code.
            matcher.addURI(authority, dbContract.URI_MOVIE, MOVIE);

            return matcher;
        }

        @Override
        public boolean onCreate() {
            mOpenHelper = new dbHelper(getContext());
            return true;
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
            Cursor retCursor;
            switch (sUriMatcher.match(uri)) {
                case MOVIE: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            dbContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder);
                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
            return retCursor;
        }

        @Override
        public String getType(Uri uri) {
            final int match = sUriMatcher.match(uri);

            switch (match) {
                case MOVIE:
                    return dbContract.MovieEntry.CONTENT_TYPE;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        @Override
        public Uri insert(Uri uri, ContentValues values) {

            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            Uri returnUri;

            switch (sUriMatcher.match(uri)) {
                case MOVIE: {
                    long _id = db.insert( dbContract.MovieEntry.TABLE_NAME, null, values);
                    if (_id > 0) {
                        returnUri = dbContract.MovieEntry.buildMovieUri(_id);
                        Log.d(TAG, returnUri.toString());
                    }
                    else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return returnUri;
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {

            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            int rowsDeleted;
            // this makes delete all rows return the number of rows deleted
            if (null == selection) selection = "1";
            switch (sUriMatcher.match(uri)) {
                case MOVIE:
                    rowsDeleted = db.delete(
                            dbContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            // Because a null deletes all rows
            if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsDeleted;
        }

        @Override
        public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            int rowsUpdated;

            switch (sUriMatcher.match(uri)) {
                case MOVIE:
                    rowsUpdated = db.update( dbContract.MovieEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            return rowsUpdated;
        }
    }



