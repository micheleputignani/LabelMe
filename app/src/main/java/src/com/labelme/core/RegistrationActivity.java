package src.com.labelme.core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import src.com.labelme.R;
import src.com.labelme.helper.JSONParser;

/**
 * Created by Mirko Putignani on 25/01/2016.
 */
public class RegistrationActivity extends Activity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnRegister;
    private TextView btnLinkToLogin;
    private ProgressDialog pDialog;

    // key
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NAME = "name";

    private static final String url_register = "http://androidlabelme.altervista.org/registration.php";

    // JSON node names
    private static final String TAG_SUCCESS = "success";

    JSONParser jsonParser = new JSONParser();

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
        pDialog = new ProgressDialog(this);
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
        if (validate()) {
            new loadUser().execute();
            return;
        }
    }

    private boolean validate() {
        boolean valid = true;

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String name = inputName.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
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
            pDialog = new ProgressDialog(RegistrationActivity.this);
            pDialog.setMessage("Loggin in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(KEY_EMAIL, email));
            params.add(new BasicNameValuePair(KEY_PASSWORD, password));
            params.add(new BasicNameValuePair(KEY_NAME, name));
            // getting JSON Object
            // POST method
            JSONObject json = jsonParser.makeHttpRequest(url_register, "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    //intent alla Home page
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    // closing this screen
                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //i dati inseriti non sono validi per il login
                            Toast.makeText(RegistrationActivity.this, "Please enter again your data", Toast.LENGTH_LONG).show();
                        }
                    });
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
        }
    }
}