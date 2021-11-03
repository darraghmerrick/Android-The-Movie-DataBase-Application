package ie.pegasus.popularmovies2.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Pegasus in May 2017.
 * Trailer Model for Parcelable - to pass data to Detail Activity
 */

public class TrailerModel {
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;

    public TrailerModel() {

    }

    public TrailerModel(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.key = trailer.getString("key");
        this.name = trailer.getString("name");
        this.site = trailer.getString("site");
        this.type = trailer.getString("type");
    }

    public String getId() {
        return id;
    }

    public String getKey() { return key; }

    public String getName() { return name; }

    public String getSite() { return site; }

    public String getType() { return type; }
}

