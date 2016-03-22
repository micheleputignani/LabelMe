package src.com.labelme.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Created by Mirko Putignani on 26/02/2016.
 */
public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared preferences
    SharedPreferences pref;

    Editor editor;
    Context context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "LabelMeLogin";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_NAME = "name";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.e(TAG, "User login session modified!");
    }

    public void setLogout(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.e(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setUser_id(int id) {
        editor.putInt(KEY_USER_ID, id);
        editor.commit();
    }

    public void setUser_email(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.commit();
    }

    public void setUser_name(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.commit();
    }

    public String getUser_id() {
        int id = pref.getInt(KEY_USER_ID, 0);
        return String.valueOf(id);
    }

    public String getUser_email() {
        return pref.getString(KEY_USER_EMAIL, "email");
    }

    public String getUser_name() {
        return pref.getString(KEY_USER_NAME, "name");
    }
}
