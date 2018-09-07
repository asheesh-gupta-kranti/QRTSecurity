package poc.android.com.qrtsecurity.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ashutoshmishra on 07/09/18.
 */

public class AppPreferencesHandler {

    /*
    This method is use set the login status for user
     */
    public static void setLoginStatus(Context context, boolean loginStatus) {
        SharedPreferences pref = context.getSharedPreferences(Constants.parentPreferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.isLogin, loginStatus);
        editor.apply();
    }

    /*
    This method id use to get the login status of user
     */
    public static boolean isLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        return prefs.getBoolean(Constants.isLogin, false);
    }

    public static void setUserPhoneNumber(Context context, String phoneNUmber) {

        SharedPreferences pref = context.getSharedPreferences(Constants.parentPreferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.phoneNumber, phoneNUmber);
        editor.apply();

    }

    public static String getUserPhoneNumber(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        return prefs.getString(Constants.phoneNumber, "");
    }
}
