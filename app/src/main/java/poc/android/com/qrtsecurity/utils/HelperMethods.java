package poc.android.com.qrtsecurity.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.regex.Pattern;

/**
 * Created by ashutoshmishra on 07/09/18.
 */

public class HelperMethods {

    /**
     * method to valid the mobile number
     * @param phoneNumber
     * @return
     */
    public static boolean isValidMobileNumber(String phoneNumber) {
        if (phoneNumber == null)
            return false;
        phoneNumber = phoneNumber.trim().replaceAll(" ", "");

        Pattern p = Pattern.compile(Constants.PHONE_NUMBER_PATTERN);
        if (phoneNumber.isEmpty() || !p.matcher(phoneNumber).matches())
            return false;
        return true;
    }

    /**
     * This method check if internet is working in device or not
     *
     * @param context
     * @return the internet state
     */

    public static boolean isNetWorkAvailable(Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (connMgr == null) {
                return false;
            } else return connMgr.getActiveNetworkInfo() != null
                    && connMgr.getActiveNetworkInfo().isAvailable()
                    && connMgr.getActiveNetworkInfo().isConnected();
        } catch (Exception e) {
            return false;
        }
    }
}
