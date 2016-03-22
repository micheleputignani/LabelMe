package src.com.labelme.core;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class SettingsActivity extends AppCompatActivity {

    // Context
    private Context context = SettingsActivity.this;
    // session manager
    private SessionManager session;
    private JSONParser jsonParser = new JSONParser();
    private Toolbar toolbar;
    private TextView settings_email;
    private TextView settings_name;
    private TextView change_password;
    private String password_from_server = "";
    private String current_psw = "";
    private String new_psw = "";
    private String toast_message;
    private EditText current_password;
    private EditText new_password;
    private int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        // session manager
        session = new SessionManager(context);

        // set a Toolbar to replace the ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_settings));
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        findViews();
        // download user password
        getUserPassword();
    }

    private void findViews() {
        settings_email = (TextView) findViewById(R.id.settings_email);
        settings_name = (TextView) findViewById(R.id.settings_name);
        settings_email.setText(session.getUser_email());
        settings_name.setText(session.getUser_name());
        change_password = (TextView) findViewById(R.id.change_password);
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });
    }

    private void showInputDialog() {
        // get xml view
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme_Dialog));
        builder.setView(alertView);

        current_password = (EditText) alertView.findViewById(R.id.current_password);
        new_password = (EditText) alertView.findViewById(R.id.new_password);
        // setup a dialog window
        builder.setCancelable(false);
        builder.setTitle(context.getResources().getString(R.string.title_password_dialog));
        builder.setPositiveButton(context.getResources().getString(R.string.change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = CheckNetwork.isInternetAvailable(context);
                if (result == 1) {
                    if (validate()) {
                        changePassword();
                        Intent i = getIntent();
                        finish();
                        startActivity(i);
                    } else {
                        Toast.makeText(context, toast_message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showSnackBar(result);
                }
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button negative_button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negative_button.setTextColor(getResources().getColor(R.color.primary));
        Button positive_button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive_button.setTextColor(getResources().getColor(R.color.primary));
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
        LinearLayout root_layout = (LinearLayout) findViewById(R.id.root_layout);
        Snackbar snackbar = Snackbar.make(root_layout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private boolean validate() {
        boolean valid = true;

        current_psw = current_password.getText().toString();
        new_psw = new_password.getText().toString();

        if (current_psw.isEmpty()) {
            valid = false;
        }
        if (new_psw.isEmpty() || new_psw.length() < 8 || new_psw.length() > 15) {
            valid = false;
        }
        password_from_server = Base64Helper.decodePassword(password_from_server);
        if (!password_from_server.equals(current_psw)) {
            valid = false;
        }
        if (!valid) {
            toast_message = context.getResources().getString(R.string.valid_password);
        }
        return valid;
    }

    private void getUserPassword() {
        new getUserPassword().execute();
    }

    private void changePassword() {
        new changePassword().execute();
    }

    class getUserPassword extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            // building params
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(AppConfig.TAG_EMAIL, session.getUser_email()));

            JSONObject json = jsonParser.makeHttpRequest(AppConfig.url_get_user_password, "POST", params);

            // check log for response
            Log.e("Create response: ", json.toString());
            try {
                // check for success tag
                success = json.getInt(AppConfig.TAG_SUCCESS);
                if (success == 1) {
                    password_from_server = json.getString(AppConfig.TAG_PASSWORD);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file) {
            switch (success) {
                case 0:
                    toast_message = context.getResources().getString(R.string.no_user_found);
                    break;
                case -1:
                    toast_message = context.getResources().getString(R.string.required_fields);
                    break;
            }
        }
    }

    class changePassword extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            // building parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(AppConfig.TAG_PASSWORD, Base64Helper.encodePassword(new_psw)));
            params.add(new BasicNameValuePair(AppConfig.TAG_EMAIL, session.getUser_email()));
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.url_change_password, "POST", params);

            // check for response
            Log.e("Create response: ", json.toString());

            try {
                // check for success tag
                success = json.getInt(AppConfig.TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            switch (success) {
                case 0:
                    toast_message = context.getResources().getString(R.string.change_password_failed);
                    break;
                case 1:
                    toast_message = context.getResources().getString(R.string.change_password_success);
                    break;
                case -1:
                    toast_message = context.getResources().getString(R.string.required_fields);
            }
            Toast.makeText(context, toast_message, Toast.LENGTH_LONG).show();
        }
    }
}
