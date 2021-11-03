package ie.pegasus.popularmovies2.ui;

import android.content.Context;
import android.database.Cursor;

import ie.pegasus.popularmovies2.database.dbContract;

/**
 * Created by Pegasus in May 2017.
 * Used to query database if Movie is in favourites before pulling down from internet
 */

class DetailActivityHelper {
    static int addedFavourite(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                dbContract.MovieEntry.CONTENT_URI,
                null,
                dbContract.MovieEntry.MOVIE_ID + " = ?",
                new String[] { Integer.toString(id) },
                null
        );
        int numRows;
        numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }

    static String buildImageUrl(int width, String fileName) {
        return "http://image.tmdb.org/t/p/w" + Integer.toString(width) + fileName;
    }
}
