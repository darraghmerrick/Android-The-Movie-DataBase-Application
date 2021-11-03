package ie.pegasus.popularmovies2.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import ie.pegasus.popularmovies2.ui.MainActivityFragment;

/**
 * Created by Pegasus in May 2017.
 * Movie Model for Parcelable - to pass data to Detail Activity
 */

public class MovieModel implements Parcelable {
    private int id;
    private String title; // original_title
    private String poster; // poster_path
    private String backdrop; // backdrop_path
    private String synopsis; //synopsis
    private int rating; // vote_average
    private String date; // release_date

    public MovieModel() {

    }

    public MovieModel(JSONObject movie) throws JSONException {
        this.id = movie.getInt("id");
        this.title = movie.getString("original_title");
        this.poster = movie.getString("poster_path");
        this.backdrop = movie.getString("backdrop_path");
        this.synopsis = movie.getString("overview");
        this.rating = movie.getInt("vote_average");
        this.date = movie.getString("release_date");
    }

    public MovieModel(Cursor cursor) {
        this.id = cursor.getInt(MainActivityFragment.MOVIE_ID );
        this.title = cursor.getString(MainActivityFragment.TITLE );
        this.poster = cursor.getString(MainActivityFragment.POSTER );
        this.backdrop = cursor.getString(MainActivityFragment.BACKDROP );
        this.synopsis = cursor.getString(MainActivityFragment.SYNOPSIS );
        this.rating = cursor.getInt(MainActivityFragment.RATING );
        this.date = cursor.getString(MainActivityFragment.RELEASE );
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public int getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString( poster );
        dest.writeString( backdrop );
        dest.writeString( synopsis );
        dest.writeInt(rating);
        dest.writeString(date);
    }

    public static final Parcelable.Creator<MovieModel> CREATOR
            = new Parcelable.Creator<MovieModel>() {
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    private MovieModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        poster = in.readString();
        backdrop = in.readString();
        synopsis = in.readString();
        rating = in.readInt();
        date = in.readString();
    }
}

