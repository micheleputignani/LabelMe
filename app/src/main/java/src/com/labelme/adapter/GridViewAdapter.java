package src.com.labelme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import src.com.labelme.R;

/**
 * Classe adapter per la visualizzazione
 * della lista delle immagini presenti nel training set,
 * scaricandole dalla sorgente dati.
 */

public class GridViewAdapter extends BaseAdapter {

    // Context
    private Context context;

    // Array List that would contain the ids and the urls for the images
    public static ArrayList<String> ids;
    public static ArrayList<String> images;
    LayoutInflater inflater;

    // Gets the context so it can be used later
    public GridViewAdapter(Context context, ArrayList<String> ids, ArrayList<String> images) {
        // Getting all the values
        this.context = context;
        this.ids = ids;
        this.images = images;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public GridViewAdapter(Context context) {
        this.context = context;
    }

    // Total number of things contained within the adapter
    @Override
    public int getCount() {
        return images.size();
    }

    // Require for the structure. Can be used to get the id of an item in the adapter for manual control.
    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.grid_single, null);
        holder.imageView = (ImageView) rowView.findViewById(R.id.grid_image);

        Picasso.with(context).load(getItem(position).toString()).into(holder.imageView);

        return rowView;
    }

    public class Holder {
        ImageView imageView;
    }
}
