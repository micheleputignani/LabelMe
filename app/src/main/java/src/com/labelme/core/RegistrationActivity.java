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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import src.com.labelme.R;
import src.com.labelme.helper.AppConfig;
import src.com.labelme.helper.Base64Helper;
import src.com.labelme.helper.CheckNetwork;
import src.com.labelme.helper.JSONParser;

/**
 * Classe relativa alla registrazione di un nuovo utente all'applicazione.
 * In questa classe sono richieste le informazioni base all'utente, come:
 * email per l'accesso all'applicazione;
 * password per proteggere l'account;
 * nominativo per la visualizzazione del nome e cognome nelle varie schermate.
 */

public class RegistrationActivity extends Activity {

    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnRegister;
    private TextView btnLinkToLogin;
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    private int success;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLinkToLogin = (TextView) findViewById(R.id.link_login);

        // Progress dialog
        pDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        pDialog.setCancelable(false);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(toLogin);
                finish();
            }
        });
    }

    private void registerUser() {
        int result = CheckNetwork.isInternetAvailable(this);
        if (result == 1) { // network available
            if (validate()) {
                new loadUser().execute();
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
        String name = inputName.getText().toString();

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

        if (name.isEmpty()) {
            inputName.setError(getResources().getString(R.string.name_error));
            valid = false;
        } else {
            inputName.setError(null);
        }
        return valid;
    }

    /**
     * Richiesta asincrona per l'inserimento di un utente
     */
    class loadUser extends AsyncTask<String, String, String> {
        //prende il nome e cognome inseriti dall'utente
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String name = inputName.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress dialog
            pDialog = new ProgressDialog(RegistrationActivity.this, R.style.AppTheme_Dialog);
            pDialog.setMessage(getResources().getString(R.string.signup));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(AppConfig.TAG_EMAIL, email));
            params.add(new BasicNameValuePair(AppConfig.TAG_PASSWORD, Base64Helper.encodePassword(password)));
            params.add(new BasicNameValuePair(AppConfig.TAG_NAME, name));
            // getting JSON Object
            // POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.url_register, "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                success = json.getInt(AppConfig.TAG_SUCCESS);

                if (success == 1) {
                    //intent alla Home page
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
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
         * After completing background task Dismiss the progress dialog
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after registration
            pDialog.dismiss();
            String toast_message = "";
            switch (success) {
                case 0:
                    toast_message = getResources().getString(R.string.registration_failed);
                    break;
                case 1:
                    toast_message = getResources().getString(R.string.registration_success);
                    break;
                case 2:
                    toast_message = getResources().getString(R.string.email_already_exist);
                    break;
                case -1:
                    toast_message = getResources().getString(R.string.required_fields);
                    break;
            }
            Toast.makeText(RegistrationActivity.this, toast_message, Toast.LENGTH_SHORT).show();
        }
    }
}