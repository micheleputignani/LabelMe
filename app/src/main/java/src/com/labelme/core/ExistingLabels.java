package src.com.labelme.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import src.com.labelme.R;
import src.com.labelme.adapter.ExistingLabelsAdapter;
import src.com.labelme.helper.AppConfig;
import src.com.labelme.helper.CheckNetwork;
import src.com.labelme.helper.JSONParser;
import src.com.labelme.helper.SessionManager;
import src.com.labelme.model.LabelsItem;

public class ExistingLabels extends AppCompatActivity {

    // Context
    Context context;

    // views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    private String position;
    private boolean answer;

    // json existing labels JSONArray
    JSONArray json_labels = null;
    List<LabelsItem> labelsList;

    // JSONParser object
    JSONParser jsonParser = new JSONParser();
    private int success;

    public ExistingLabels() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.existing_labels);
        context = ExistingLabels.this;

        // get intent
        Intent i = getIntent();
        position = String.valueOf((i.getExtras().getInt(AppConfig.TAG_ORIGINAL_IMAGE)));

        // set a Toolbar to replace the ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(context.getResources().getString(R.string.title_existing_labels));
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        findViews();

        // loading existing labels in background thread
        loadExistingLabels();
    }

    private void findViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        labelsList = new ArrayList<>();
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
        Intent i = new Intent(ExistingLabels.this, SettingsActivity.class);
        startActivity(i);
        finish();
    }

    private void logout() {
        SessionManager session = new SessionManager(ExistingLabels.this);
        session.setLogout(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(ExistingLabels.this, R.style.AppTheme_Dialog);
        //set title
        builder.setMessage(getResources().getString(R.string.logout_question));
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_logout);
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if this button is clicked, close current activity and clear user session
                ProgressDialog progressDialog = new ProgressDialog(ExistingLabels.this, R.style.AppTheme_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getResources().getString(R.string.logout));
                progressDialog.show();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(ExistingLabels.this, LoginActivity.class);
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

    private void loadExistingLabels() {
        int result = CheckNetwork.isInternetAvailable(context);
        if (result == 1) {
            new loadExistingLabels().execute();
        } else {
            showSnackBar(result);
        }
    }

    private void showSnackBar(int type) {
        String message = "";
        switch (type) {
            case -1:
                message = getResources().getString(R.string.airplane_mode_on);
                break;
            case 0:
                message = getResources().getString(R.string.connection_off);
                break;
        }
        RelativeLayout root_layout = (RelativeLayout) findViewById(R.id.root_layout);
        Snackbar snackbar = Snackbar.make(root_layout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void insertRating(String imageID, String author, float rating_value) {
        new insertRating(imageID, author, rating_value).execute();
    }

    public int getResultInsertRating() {
        while (!answer) {

        }
        return success;
    }

    public class insertRating extends AsyncTask<String, String, String> {

        String imageID;
        String author;
        String rating;

        public insertRating(String imageID, String author, float rating_value) {
            this.imageID = imageID;
            this.author = author;
            this.rating = String.valueOf(rating_value);
            answer = false;
        }

        @Override
        protected String doInBackground(String... args) {
            //building params
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(AppConfig.TAG_IMAGE_ID, imageID));
            params.add(new BasicNameValuePair(AppConfig.TAG_AUTHOR, author));
            params.add(new BasicNameValuePair(AppConfig.TAG_PEOPLE_RATING, rating));
            // getting json object
            // POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.url_ratings, "POST", params);

            // check log for response
            Log.e("Create response: ", json.toString());

            // check for success tag
            try {
                success = json.getInt(AppConfig.TAG_SUCCESS);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            answer = true;
            return null;
        }
    }

    /**
     * Background Async Task to load all existing labels by making HTTP request
     */
    class loadExistingLabels extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread show progress dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context, R.style.AppTheme_Dialog);
            progressDialog.setMessage(context.getResources().getString(R.string.loading_labels));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * Getting all existing labels from url
         */
        protected String doInBackground(String... args) {
            // building params
            SessionManager session = new SessionManager(context);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(AppConfig.TAG_ORIGINAL_IMAGE, position));
            params.add(new BasicNameValuePair(AppConfig.TAG_AUTHOR, session.getUser_id()));
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.url_all_existing_labels, "POST", params);

            // check log cat for JSON response
            Log.d("Create response: ", json.toString());

            try {
                int success = json.getInt(AppConfig.TAG_SUCCESS);
                if (success == 1) {
                    // json label found
                    // getting array of labels
                    json_labels = json.getJSONArray(AppConfig.TAG_EXISTING_LABELS_ARRAY);

                    // looping through all labels
                    for (int i = 0; i < json_labels.length(); i++) {
                        JSONObject post = json_labels.optJSONObject(i);
                        LabelsItem item = new LabelsItem();
                        item.setImageID(post.optString(AppConfig.TAG_IMAGE_ID));
                        item.setThumbnail(post.optString(AppConfig.TAG_CROPPED_IMAGE));
                        item.setLabel(post.optString(AppConfig.TAG_LABEL));
                        item.setAuthor(post.optString(AppConfig.TAG_AUTHOR));
                        item.setRating(post.optString(AppConfig.TAG_RATING));

                        labelsList.add(item);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task dismiss the progress dialog
         */
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all labels
            progressDialog.dismiss();

            //updating UI from background thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // updating parsed JSON data into CardView

                    // creating ExistingLabelsAdapter object
                    ExistingLabelsAdapter adapter = new ExistingLabelsAdapter(context, labelsList);
                    // adding adapter to CardView
                    recyclerView.setAdapter(adapter);
                }
            });
        }
    }

}
