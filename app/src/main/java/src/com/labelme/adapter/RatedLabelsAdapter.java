package src.com.labelme.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import src.com.labelme.R;
import src.com.labelme.helper.Base64Helper;
import src.com.labelme.model.RatingsItem;

public class RatedLabelsAdapter extends RecyclerView.Adapter<RatedLabelsAdapter.ViewHolder> {
    private List<RatingsItem> ratingsItemList;
    private Context context;

    public RatedLabelsAdapter(Context context, List<RatingsItem> ratingsItemList) {
        this.context = context;
        this.ratingsItemList = ratingsItemList;
    }

    @Override
    public RatedLabelsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ratings_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RatedLabelsAdapter.ViewHolder holder, int position) {
        RatingsItem ratingsItem = ratingsItemList.get(position);
        Bitmap thumbnail = Base64Helper.decodeBase64(ratingsItem.getThumbnail());
        // setting items
        holder.thumbnail.setImageBitmap(thumbnail);
        holder.label.setText(ratingsItem.getLabel());
        holder.author.setText(ratingsItem.getAuthor());
        holder.average.setText(ratingsItem.getAverage());
        holder.personal_rating.setText(ratingsItem.getPersonal_rating());
    }

    @Override
    public int getItemCount() {
        return ratingsItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView label;
        public TextView author;
        public TextView average;
        public TextView personal_rating;

        public ViewHolder(View view) {
            super(view);
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            this.label = (TextView) view.findViewById(R.id.label);
            this.author = (TextView) view.findViewById(R.id.author);
            this.average = (TextView) view.findViewById(R.id.average);
            this.personal_rating = (TextView) view.findViewById(R.id.personal_rating);
        }
    }
}
