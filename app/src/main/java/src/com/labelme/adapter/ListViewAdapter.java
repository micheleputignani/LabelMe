package src.com.labelme.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import src.com.labelme.R;
import src.com.labelme.helper.Base64Helper;

public class ListViewAdapter extends BaseAdapter {

    // Context
    private Context context;

    // array list that would contain the images and the titles for the list items
    public static ArrayList<String> images;
    public static ArrayList<String> titles;
    LayoutInflater inflater;

    public ListViewAdapter(Context context, ArrayList<String> images, ArrayList<String> titles) {
        this.context = context;
        this.images = images;
        this.titles = titles;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

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
        rowView = inflater.inflate(R.layout.list_single, null);
        holder.list_image = (ImageView) rowView.findViewById(R.id.list_image);
        String image = images.get(position);
        Bitmap image_bitmap = Base64Helper.decodeBase64(image);
        holder.list_image.setImageBitmap(image_bitmap);
        holder.list_title = (TextView) rowView.findViewById(R.id.label);
        holder.list_title.setText(titles.get(position));
        return rowView;
    }

    public class Holder {
        ImageView list_image;
        TextView list_title;
    }
}
