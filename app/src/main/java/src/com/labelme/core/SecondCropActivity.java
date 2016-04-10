package src.com.labelme.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import src.com.labelme.R;
import src.com.labelme.helper.AppConfig;
import src.com.labelme.helper.Base64Helper;
import src.com.labelme.helper.CheckNetwork;
import src.com.labelme.helper.JSONParser;
import src.com.labelme.helper.SessionManager;

/**
 * Classe relativa alla seconda fase di annotazione di una forma.
 * In questa classe sono richiesti all'utente la parola chiave e il grado di certezza.
 * Se i dati sono completi, l'annotazione viene caricata nella sorgente dati.
 */

public class SecondCropActivity extends AppCompatActivity {

    // context
    private Context context = SecondCropActivity.this;

    // network variables
    private JSONParser jsonParser = new JSONParser();

    // view
    private ImageView croppedImageResult;
    private EditText inputLabel;
    private RatingBar ratingBar;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private ProgressDialog progressDialog;
    private String cropped_image;
    private String cropped_label;
    private int position;
    private float rating;
    private SessionManager session;
    private int success;

    public SecondCropActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_secondcrop);
        session = new SessionManager(getApplicationContext());
        findViews();
        // get intent data
        Intent intent = getIntent();

        // selected cropped image
        cropped_image = intent.getExtras().getString(AppConfig.TAG_CROPPED_IMAGE);
        Bitmap cropped_bitmap = Base64Helper.decodeBase64(cropped_image);
        croppedImageResult.setImageBitmap(cropped_bitmap);
        position = (intent.getExtras().getInt(AppConfig.TAG_IMAGE_ID) + 1);
    }

    private void findViews() {
        croppedImageResult = (ImageView) findViewById(R.id.crop_image);
        inputLabel = (EditText) findViewById(R.id.input_label);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        rating = ratingBar.getMax();

        // set a Toolbar to replace the ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(context.getResources().getString(R.string.title_crop));
        getSupportActionBar().setSubtitle(context.getResources().getString(R.string.title_second_subtitle));
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        floatingActionButton.setOnClickListener(btnListener);
    }

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    cropped_label = inputLabel.getText().toString();
                    if (cropped_label.isEmpty()) {
                        inputLabel.setError(context.getResources().getString(R.string.label_error));
                    } else {
                        if (ratingBar.getRating() == 0) {
                            rating = ratingBar.getMax();
                        } else {
                            rating = ((ratingBar.getRating()) * 2);
                        }
                        uploadImage();
                    }
                    break;
            }
        }
    };

    private void uploadImage() {
        int result = CheckNetwork.isInternetAvailable(context);
        if (result == 1) {
            new uploadImage().execute();
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
        Intent i = new Intent(SecondCropActivity.this, AccountActivity.class);
        finish();
        startActivity(i);
    }

    private void logout() {
        SessionManager session = new SessionManager(SecondCropActivity.this);
        session.setLogout(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(SecondCropActivity.this, R.style.AppTheme_Dialog);
        //set title
        builder.setMessage(getResources().getString(R.string.logout_question));
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_logout);
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if this button is clicked, close current activity and clear user session
                ProgressDialog progressDialog = new ProgressDialog(SecondCropActivity.this, R.style.AppTheme_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getResources().getString(R.string.logout));
                progressDialog.show();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SecondCropActivity.this, LoginActivity.class);
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

    /**
     * Richiesta asincrona per l'upload di una nuova etichetta
     */
    class uploadImage extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progress dialog
            progressDialog = new ProgressDialog(context, R.style.AppTheme_Dialog);
            progressDialog.setMessage(context.getResources().getString(R.string.upload_label));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... args) {
            // building parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(AppConfig.TAG_CROPPED_IMAGE, cropped_image));
            params.add(new BasicNameValuePair(AppConfig.TAG_LABEL, cropped_label));
            params.add(new BasicNameValuePair(AppConfig.TAG_ORIGINAL_IMAGE, String.valueOf(position)));
            params.add(new BasicNameValuePair(AppConfig.TAG_RATING, String.valueOf(rating)));
            params.add(new BasicNameValuePair(AppConfig.TAG_AUTHOR, session.getUser_id()));
            // getting json object
            // POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.url_uploadImage, "POST", params);

            // check log cat for response
            Log.e("Create response", json.toString());

            // check for success tag
            try {
                success = json.getInt(AppConfig.TAG_SUCCESS);
                if (success == 1) {
                    Intent i = new Intent(context, MainActivity.class);
                    startActivity(i);
                    // closing this screen
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task, dismiss the progress dialog
         */
        protected void onPostExecute(String file) {
            String toast_message = "";
            // dismiss the dialog after upload
            progressDialog.dismiss();
            switch (success) {
                case 0:
                    toast_message = context.getResources().getString(R.string.upload_failed);
                    break;
                case 1:
                    toast_message = context.getResources().getString(R.string.upload_success);
                    break;
                case -1:
                    toast_message = context.getResources().getString(R.string.required_fields);
            }
            Toast.makeText(context, toast_message, Toast.LENGTH_SHORT).show();
        }
    }
}
