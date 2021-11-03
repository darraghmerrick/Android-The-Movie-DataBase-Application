package ie.pegasus.popularmovies2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pegasus in May 2017.
 * This is the dbHelper for the Favourite Movies database in the Popular Movies Part 2 Application
 */

class dbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "favourite_movies.db";

    dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    // Create a table to store movie data
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + dbContract.MovieEntry.TABLE_NAME + " (" +
                dbContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                dbContract.MovieEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                dbContract.MovieEntry.TITLE + " TEXT NOT NULL, " +
                dbContract.MovieEntry.POSTER + " TEXT, " +
                dbContract.MovieEntry.BACKDROP + " TEXT, " +
                dbContract.MovieEntry.SYNOPSIS + " TEXT, " +
                dbContract.MovieEntry.RATING + " INTEGER, " +
                dbContract.MovieEntry.RELEASE + " TEXT);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + dbContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
