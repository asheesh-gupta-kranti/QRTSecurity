package poc.android.com.qrtsecurity.utils;

/**
 * Created by ashutoshmishra on 07/09/18.
 */

public class Constants {

    public static final String PHONE_NUMBER_PATTERN = "[0-9]{10}";
    public static final String baseUrl = "http://ec2-13-232-185-241.ap-south-1.compute.amazonaws.com:3000";

    // API End Points
    public static final String responserAPIEndPoint = "/api/responders";
    public static final String responserLoginEndPoint = "/api/responders/login";

    // Preferences keys
    public static final String parentPreferenceKey = "USER_PREFERENCES";
    public static final String isLogin = "isLogin";
    public static final String phoneNumber = "phoneNumber";
}
