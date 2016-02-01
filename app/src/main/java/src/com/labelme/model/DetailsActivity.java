package src.com.labelme.model;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import src.com.labelme.R;
import src.com.labelme.adapter.GridViewAdapter;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_image);

        //get intent data
        Intent intent = getIntent();

        //selected image id
        int position = intent.getExtras().getInt("id");
        GridViewAdapter gridViewAdapter = new GridViewAdapter(this);

        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        imageView.setImageResource(gridViewAdapter.thumbs_ids[position]);
    }
}
