package src.com.labelme.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.isseiaoki.simplecropview.CropImageView;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import src.com.labelme.R;
import src.com.labelme.adapter.CustomSpinnerAdapter;
import src.com.labelme.adapter.GridViewAdapter;
import src.com.labelme.helper.AppConfig;
import src.com.labelme.helper.Base64Helper;
import src.com.labelme.helper.SessionManager;

/**
 * Created by Mirko Putignani on 25/02/2016.
 */
public class FirstCropActivity extends AppCompatActivity {

    // Context
    private Context context = FirstCropActivity.this;

    private CropImageView mCropView;
    private Toolbar toolbar;
    private Spinner spinner;
    private FloatingActionButton floatingActionButton;
    private int position;

    public FirstCropActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_firstcrop);
        findViews();
        addItemsToSpinner();
        // get intent data
        Intent intent = getIntent();

        // selected image id
        position = intent.getExtras().getInt(AppConfig.TAG_IMAGE_ID);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(context);
        String url_img = gridViewAdapter.getItem(position).toString();
        Picasso.with(context).load(url_img).into(mCropView);
    }

    private void findViews() {
        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        spinner = (Spinner) findViewById(R.id.spinner);
        // set a Toolbar to replace the ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(context.getResources().getString(R.string.title_crop));
        getSupportActionBar().setSubtitle(context.getResources().getString(R.string.title_first_subtitle));
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(btnListener);
    }

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    Bitmap cropped_bitmap = mCropView.getCroppedBitmap();
                    String cropped_image = Base64Helper.encodeToBase64(cropped_bitmap);
                    Intent i = new Intent(context, SecondCropActivity.class);
                    i.putExtra(AppConfig.TAG_CROPPED_IMAGE, cropped_image);
                    i.putExtra(AppConfig.TAG_IMAGE_ID, position);
                    startActivity(i);
                    break;
            }
        }
    };

    private void addItemsToSpinner() {
        String[] list = getResources().getStringArray(R.array.cropMode_array);
        ArrayList<String> crop_mode_list = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            crop_mode_list.add(list[i]);
        }
        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(context, crop_mode_list);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // on selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                switch (item) {
                    case "Fit image":
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_FIT_IMAGE);
                        break;
                    case "1:1":
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_1_1);
                        break;
                    case "3:4":
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                        break;
                    case "4:3":
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                        break;
                    case "9:16":
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                        break;
                    case "16:9":
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                        break;
                    case "Custom":
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_CUSTOM);
                        break;
                    case "Free":
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_FREE);
                        break;
                    case "Circle":
                        mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                settings();
                break;
            case R.id.action_help:
                break;
            case R.id.action_info:
                break;
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void settings() {
        Intent i = new Intent(FirstCropActivity.this, SettingsActivity.class);
        startActivity(i);
        finish();
    }

    private void logout() {
        SessionManager session = new SessionManager(FirstCropActivity.this);
        session.setLogout(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstCropActivity.this, R.style.AppTheme_Dialog);
        //set title
        builder.setMessage(getResources().getString(R.string.logout_question));
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_logout);
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if this button is clicked, close current activity and clear user session
                ProgressDialog progressDialog = new ProgressDialog(FirstCropActivity.this, R.style.AppTheme_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getResources().getString(R.string.logout));
                progressDialog.show();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(FirstCropActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 1500);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if this button is clicked, just close the dialog box and do nothing
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = builder.create();
        // show it
        alertDialog.show();
    }
}
