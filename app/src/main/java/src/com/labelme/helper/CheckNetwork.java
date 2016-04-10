package src.com.labelme.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Classe di supporto per il controllo della connessione alla rete.
 */

public class CheckNetwork extends Activity {

    /**
     * Metodo che controlla lo stato della connessione alla rete
     * @param context = il contesto dal quale arriva la richiesta di controllo, cioè l'activity chiamante
     * @return = 1 se la connessione è presente, 0 se la connessione è assente, -1 se è attiva la modalità aereo.
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

