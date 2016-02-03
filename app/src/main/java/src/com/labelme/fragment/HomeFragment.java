package src.com.labelme.fragment;

/**
 * Created by Ravi on 29/07/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import src.com.labelme.R;
import src.com.labelme.adapter.GridViewAdapter;
import src.com.labelme.model.DetailsActivity;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewAdapter(view.getContext()));
        gridView.setOnItemClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        //passing array index
        intent.putExtra("id", position);
        startActivity(intent);
    }
}
