package src.com.labelme.core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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
 * Classe relativa all'accesso all'applicazione.
 * In questa classe sono richiesti all'utente le credenziali d'accesso (email e password)
 * inserite in fase di registrazione.
 */

public class LoginActivity extends Activity {

    private String toast_message = "";

    private Button btnLogin;
    private TextView btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog progressDialog;

    // sessione manager
    private SessionManager session;

    JSONParser jsonParser = new JSONParser();
    private int success;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // session manager
        session = new SessionManager(getApplicationContext());
        // check if user is already logged in or not
        if (session.isLoggedIn()) {
            // user is already logged in. Take him to main activity
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLinkToRegister = (TextView) findViewById(R.id.link_signup);

        //Login button Click event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        //Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRegister = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(toRegister);
                finish();
            }
        });
    }

    private void loginUser() {
        int result = CheckNetwork.isInternetAvailable(this);
        if (result == 1) { // network available
            if (validate()) {
                new getUser().execute();
            }
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

    private boolean validate() {
        boolean valid = true;
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError(getResources().getString(R.string.email_error));
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 15) {
            inputPassword.setError(getResources().getString(R.string.password_error));
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        return valid;
    }

    class getUser extends AsyncTask<String, String, String> {
        // prende email e password inseriti dall'utente
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress dialog
            progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dialog);
            progressDialog.setMessage(getResources().getString(R.string.login));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(AppConfig.TAG_EMAIL, email));
            // getting JSON Object
            // POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.url_login, "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                success = json.getInt(AppConfig.TAG_SUCCESS);
                if (success == 1) {
                    JSONArray jsonArray = json.getJSONArray(AppConfig.TAG_USER_ARRAY);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String user_password = jsonObject.getString(AppConfig.TAG_PASSWORD);
                    String decoded_password = Base64Helper.decodePassword(user_password);
                    if (password.equals(decoded_password)) {
                        // user successfully logged in
                        // create login session
                        session.setLogin(true);

                        // store the user in session manager
                        int id = jsonObject.getInt(AppConfig.TAG_USER_ID);
                        String email = jsonObject.getString(AppConfig.TAG_EMAIL);
                        String name = jsonObject.getString(AppConfig.TAG_NAME);
                        // store user info
                        session.setUser_id(id);
                        session.setUser_email(email);
                        session.setUser_name(name);

                        //intent alla Home page
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        // closing this screen
                        finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after loggin in
            progressDialog.dismiss();
            switch (success) {
                case 0:
                    toast_message = getResources().getString(R.string.login_failed);
                    break;
                case 1:
                    toast_message = getResources().getString(R.string.login_success);
                    break;
                case -1:
                    toast_message = getResources().getString(R.string.required_fields);
                    break;
            }
            Toast.makeText(LoginActivity.this, toast_message, Toast.LENGTH_SHORT).show();
        }
    }
}




