package ie.pegasus.popularmovies2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import ie.pegasus.popularmovies2.R;
import ie.pegasus.popularmovies2.model.MovieModel;

/**
 * Created by Pegasus in May 2017.
 * This is the adapter for the main screen UI
 */

public class MainScreenAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mInflater;

    private final MovieModel mLock = new MovieModel();

    private List<MovieModel> mObjects;

    public MainScreenAdapter(Context context, List<MovieModel> objects) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObjects = objects;
    }

    public Context getContext() {
        return mContext;
    }

    public void add(MovieModel object) {
        synchronized (mLock) {
            mObjects.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mObjects.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<MovieModel> data) {
        clear();
        for (MovieModel movieModel : data) {
            add( movieModel );
        }
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public MovieModel getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate( R.layout.movie_grid, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final MovieModel movieModel = getItem(position);

        String image_url = "http://image.tmdb.org/t/p/w185" + movieModel.getPoster();

        viewHolder = (ViewHolder) view.getTag();

        Glide.with(getContext()).load(image_url).into(viewHolder.imageView);


        return view;
    }

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.gridImage );

        }
    }
}
