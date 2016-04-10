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
import src.com.labelme.model.ScheduleItem;

/**
 * Classe adapter per la visualizzazione
 * della lista delle annotazioni presenti sul database,
 * create dagli utenti registrati all'applicazione.
 */

public class ScheduleLabelsAdapter extends RecyclerView.Adapter<ScheduleLabelsAdapter.ViewHolder> {
    private List<ScheduleItem> scheduleItemList;
    private Context context;

    public ScheduleLabelsAdapter(Context context, List<ScheduleItem> scheduleItemList) {
        this.context = context;
        this.scheduleItemList = scheduleItemList;
    }

    @Override
    public ScheduleLabelsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_single_schedule, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ScheduleLabelsAdapter.ViewHolder holder, int position) {
        ScheduleItem scheduleItem = scheduleItemList.get(position);
        Bitmap thumbnail = Base64Helper.decodeBase64(scheduleItem.getThumbnail());
        // setting items
        holder.thumbnail.setImageBitmap(thumbnail);
        holder.label.setText(scheduleItem.getLabel());
        holder.author.setText(scheduleItem.getAuthor());
    }

    @Override
    public int getItemCount() {
        return scheduleItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView label;
        public TextView author;

        public ViewHolder(View view) {
            super(view);
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            this.label = (TextView) view.findViewById(R.id.label);
            this.author = (TextView) view.findViewById(R.id.author);
        }
    }
}
