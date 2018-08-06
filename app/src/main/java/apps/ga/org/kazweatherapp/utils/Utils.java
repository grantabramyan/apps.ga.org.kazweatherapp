package apps.ga.org.kazweatherapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by grant on 03,August,2018
 */
public class Utils {
    enum EConnectionType {
        TYPE_WIFI, TYPE_MOBILE, TYPE_NOT_CONNECTED
    }

    public static String getConnectionStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return EConnectionType.TYPE_WIFI.name();
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return EConnectionType.TYPE_MOBILE.name();
            }
        }
        return EConnectionType.TYPE_NOT_CONNECTED.name();
    }

    public static boolean isInternetAvailable(Context context) {
        return !getConnectionStatus(context).equals(EConnectionType.TYPE_NOT_CONNECTED.name());
    }
}
