package ie.pegasus.popularmovies2.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Pegasus in May 2017.
 * Review Model for Parcelable - to pass data to Detail Activity
 */

public class ReviewModel {
    private String id;
    private String author;
    private String content;

    public ReviewModel() {

    }

    public ReviewModel(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.author = trailer.getString("author");
        this.content = trailer.getString("content");
    }

    public String getId() { return id; }

    public String getAuthor() { return author; }

    public String getContent() { return content; }
}
