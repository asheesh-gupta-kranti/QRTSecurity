package poc.android.com.qrtsecurity.utils;

/**
 * Created by ashutoshmishra on 07/09/18.
 */

public class Constants {

    public static final String PHONE_NUMBER_PATTERN = "[0-9]{10}";
    public static final String baseUrl = "http://ec2-13-232-185-241.ap-south-1.compute.amazonaws.com:3000";

    // API End Points
    public static final String responderAPIEndPoint = "/api/responders";
    public static final String responderLoginEndPoint = "/api/responders/login";
    public static final String responderProfileUpdateEndPoint = "/api/responders/update";
    public static final String responderScheduleEndPoint = "/api/responders/%d/schedules";

    // Preferences keys
    public static final String parentPreferenceKey = "USER_PREFERENCES";
    public static final String isLogin = "isLogin";
    public static final String phoneNumber = "phoneNumber";
    public static final String name = "name";
    public static final String vehicleRegNo = "vehicleRegNo";
    public static final String licenceNo = "licenceNo";
    public static final String gender = "gender";
    public static final String dob = "dob";
    public static final String token = "token";
    public static final String id = "id";
    public static final String dutyStartTime = "dutyStartTime";
    public static final String dutyState = "dutyState";
    public static final String scheduleId = "scheduleId";
;}
