package src.com.labelme.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import src.com.labelme.R;
import src.com.labelme.core.ExistingLabels;
import src.com.labelme.helper.Base64Helper;
import src.com.labelme.helper.SessionManager;
import src.com.labelme.model.LabelsItem;

/**
 * Classe adapter per la visualizzazione
 * delle etichette esistenti per una immagine selezionata
 * dalla home dell'applicazione.
 */

public class ExistingLabelsAdapter extends RecyclerView.Adapter<ExistingLabelsAdapter.ViewHolder> {

    Context context;
    private List<LabelsItem> labels;
    ExistingLabels el;
    SessionManager session;
    LayoutInflater layoutInflater;

    public ExistingLabelsAdapter(Context context, List<LabelsItem> labels) {
        this.context = context;
        this.labels = labels;
        el = new ExistingLabels();
        session = new SessionManager(context);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LabelsItem label = labels.get(position);
        Bitmap thumbnail = Base64Helper.decodeBase64(label.getThumbnail());
        // setting items
        holder.thumbnail.setImageBitmap(thumbnail);
        holder.label.setText(label.getLabel());
        holder.author.setText(label.getAuthor());
        holder.rating.setText(label.getRating());
        // setting onClickListener
        holder.vote.setOnClickListener(clickListener);
        holder.vote.setTag(holder);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            int position = holder.getPosition();
            final LabelsItem item = labels.get(position);
            // Dialog for vote
            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
            final View view_dialog = layoutInflater.inflate(R.layout.rating_dialog, null);
            builder.setView(view_dialog);
            final AlertDialog dialog;
            final RatingBar ratingBar = (RatingBar) view_dialog.findViewById(R.id.ratingBar);
            builder.setPositiveButton(context.getResources().getString(R.string.confirm_rate), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String imageID = item.getImageID();
                    String author = session.getUser_id();
                    float rating = ratingBar.getRating();
                    el.insertRating(imageID, author, rating);
                    int result = el.getResultInsertRating();
                    String toast_message = "";
                    switch (result) {
                        case -1:
                            toast_message = context.getResources().getString(R.string.required_fields);
                            break;
                        case 0:
                            toast_message = context.getResources().getString(R.string.insert_rating_failed);
                            break;
                        case 1:
                            toast_message = context.getResources().getString(R.string.insert_rating_success);
                            break;
                        case 2:
                            toast_message = context.getResources().getString(R.string.insert_rating_already_exist);
                            break;
                    }
                    Toast.makeText(context, toast_message, Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton(context.getResources().getString(R.string.dismiss_rate), null);
            dialog = builder.create();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.show();
            dialog.getWindow().setAttributes(lp);

            // change default color button of alert dialog
            Button rate_button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button cancel_button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (rate_button != null) {
                rate_button.setTextColor(context.getResources().getColor(R.color.primary));
            }
            if (cancel_button != null) {
                cancel_button.setTextColor(context.getResources().getColor(R.color.primary));
            }

//            final Dialog dialog = new Dialog(context);
//            // setting custom layout to dialog
//            dialog.setContentView(R.layout.rating_dialog);
//            dialog.setTitle(context.getResources().getString(R.string.title_rating_dialog));
//
//            final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
//            // adding button click event
//            Button confirmButton = (Button) dialog.findViewById(R.id.confirm_button);
//            confirmButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String imageID = item.getImageID();
//                    String author = session.getUser_id();
//                    float rating = ratingBar.getRating();
//                    el.insertRating(imageID, author, rating);
//                    dialog.dismiss();
//                    int result = el.getResultInsertRating();
//                    String toast_message = "";
//                    switch (result) {
//                        case -1:
//                            toast_message = context.getResources().getString(R.string.required_fields);
//                            break;
//                        case 0:
//                            toast_message = context.getResources().getString(R.string.insert_rating_failed);
//                            break;
//                        case 1:
//                            toast_message = context.getResources().getString(R.string.insert_rating_success);
//                            break;
//                        case 2:
//                            toast_message = context.getResources().getString(R.string.insert_rating_already_exist);
//                            break;
//                    }
//                    Toast.makeText(context, toast_message, Toast.LENGTH_LONG).show();
//                }
//            });
//            Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
//            cancelButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//            dialog.show();
        }
    };

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView label;
        public TextView author;
        public TextView rating;
        public Button vote;

        public ViewHolder(View view) {
            super(view);
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            this.label = (TextView) view.findViewById(R.id.label);
            this.author = (TextView) view.findViewById(R.id.author);
            this.rating = (TextView) view.findViewById(R.id.rating);
            this.vote = (Button) view.findViewById(R.id.vote);
        }
    }
}

