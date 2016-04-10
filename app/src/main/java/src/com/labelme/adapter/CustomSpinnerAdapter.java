package src.com.labelme.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import src.com.labelme.R;

/**
 * Classe adapter per la visualizzazione dello spinner
 * relativo alle modalità di ritaglio implementate.
 */

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> list;
    public Resources res;
    private LayoutInflater inflater;

    public CustomSpinnerAdapter(Context context, ArrayList<String> list) {
        super(context, R.layout.spinner_row, list);
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.spinner_row, parent, false);
        TextView tvCropMode = (TextView) rowView.findViewById(R.id.tvCropMode);
        tvCropMode.setText(list.get(position).toString());
        return rowView;
    }
}
