package src.com.labelme.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import src.com.labelme.R;

/**
 * Created by Mirko Putignani on 31/01/2016.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    public Integer[] thumbs_ids = {R.drawable.img1, R.drawable.img2, R.drawable.img3};

    public GridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return thumbs_ids.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(thumbs_ids[position]);
        return imageView;
    }
}