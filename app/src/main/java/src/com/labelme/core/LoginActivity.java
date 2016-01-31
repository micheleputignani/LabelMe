package src.com.labelme.core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import src.com.labelme.R;

/**
 * Created by Mirko Putignani on 25/01/2016.
 */
public class LoginActivity extends Activity {

    private Button btnLogin;
    private TextView btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
//    private SessionManager session;
//    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLinkToRegister = (TextView) findViewById(R.id.link_signup);

        //Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //TODO: SQLite database handler

        //Login button Click event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        //Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRegister = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(toRegister);
                finish();
            }
        });
    }

    public void login() {
        if (!checkLogin()) {
            onLoginFailed();
            return;
        }

        btnLogin.setEnabled(false);
        pDialog = new ProgressDialog(LoginActivity.this, R.style.MyMaterialTheme_Dialog);
        pDialog.setIndeterminate(true);
        pDialog.setMessage("Authenticating...");
        pDialog.show();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoginSuccess();
                pDialog.dismiss();
            }
        }, 3000);
    }

    /**
     * function to verify login details
     */
    private boolean checkLogin() {
        boolean valid = true;
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        //Check for empty data in the form
        if (!email.isEmpty() && !password.isEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmail.setError("enter a valid email address");
                valid = false;
            } else {
                inputEmail.setError(null);
            }
            if (password.length() < 4 || password.length() > 10) {
                inputPassword.setError("betweeen 4 and 10 alphanumeric characters");
                valid = false;
            } else {
                inputPassword.setError(null);
            }
        } else {
            valid = false;
        }
        return valid;
    }

    private void onLoginSuccess() {
        btnLogin.setEnabled(true);
        Intent toHomepage = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(toHomepage);
        finish();
    }

    private void onLoginFailed() {
//        String msg da file di errori
        String errorMsg = "Login error.";
        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }

}