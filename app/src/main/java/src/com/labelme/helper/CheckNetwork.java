package src.com.labelme.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Created by Mirko Putignani on 15/03/2016.
 */
public class CheckNetwork extends Activity {

    /**
     * Metodo che controlla lo stato della connesione
     *
     * @return
     */
    public static int isInternetAvailable(Context context) {
        int result;
        // check airplane mode
        boolean airplane_mode = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        if (airplane_mode) {
            result = -1;
        } else {
            NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (info == null) {
                result = 0;
            } else {
                if (info.isConnected()) {
                    result = 1;
                } else {
                    result = 0;
                }
            }
        }
        return result;
    }
}

