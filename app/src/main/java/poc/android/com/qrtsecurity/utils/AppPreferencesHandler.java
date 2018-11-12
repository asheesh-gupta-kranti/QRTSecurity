package poc.android.com.qrtsecurity.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import poc.android.com.qrtsecurity.Models.ResponderModel;

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

    public static void setUserToken(Context context, String token) {

        SharedPreferences pref = context.getSharedPreferences(Constants.parentPreferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.token, token);
        editor.apply();

    }

    public static String getUserToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        return prefs.getString(Constants.token, "");
    }

    public static void setUserId(Context context, int id) {

        SharedPreferences pref = context.getSharedPreferences(Constants.parentPreferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Constants.id, id);
        editor.apply();

    }

    public static int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.id, 0);
    }

    public static void setDutyStartTime(Context context, long startTime){
        SharedPreferences pref = context.getSharedPreferences(Constants.parentPreferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(Constants.dutyStartTime, startTime);
        editor.apply();
    }

    public static long getDutyStartTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        return prefs.getLong(Constants.dutyStartTime, Calendar.getInstance().getTimeInMillis());
    }

    public static void setDutyState(Context context, boolean state){
        SharedPreferences pref = context.getSharedPreferences(Constants.parentPreferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.dutyState, state);
        editor.apply();
    }

    public static boolean getDutyState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        return prefs.getBoolean(Constants.dutyState, false);
    }

    public static void saveUserDetails(Context context, ResponderModel user){
        SharedPreferences pref = context.getSharedPreferences(Constants.parentPreferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.name, user.getResponderName());
        editor.putString(Constants.phoneNumber, user.getResponderPhone());
        editor.putInt(Constants.id, user.getResponderId());
        editor.putString(Constants.dob, user.getDob());
        editor.putString(Constants.vehicleRegNo, user.getVehicleRegNo());
        editor.putString(Constants.licenceNo, user.getLicenceNo());
        editor.putString(Constants.gender, user.getGender());
        editor.putString(Constants.profilePic, user.getPhoto());
        editor.putString(Constants.licencePic, user.getLicencePic());

        editor.apply();

    }

    public static ResponderModel getUserDetails(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        ResponderModel user = new ResponderModel();
        user.setResponderName(prefs.getString(Constants.name, ""));
        user.setResponderPhone(prefs.getString(Constants.phoneNumber, ""));
        user.setResponderId(prefs.getInt(Constants.id, 0));
        user.setDob(prefs.getString(Constants.dob, ""));
        user.setVehicleRegNo(prefs.getString(Constants.vehicleRegNo, ""));
        user.setLicenceNo(prefs.getString(Constants.licenceNo, ""));
        user.setGender(prefs.getString(Constants.gender, "MALE"));
        user.setPhoto(prefs.getString(Constants.profilePic, ""));
        user.setLicencePic(prefs.getString(Constants.licencePic, ""));
        return user;
    }

    public static void setScheduleId(Context context, int id) {

        SharedPreferences pref = context.getSharedPreferences(Constants.parentPreferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Constants.scheduleId, id);
        editor.apply();

    }

    public static int getScheduleId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.scheduleId, -1);
    }

    public static void clearData(Context context){
        SharedPreferences pref = context.getSharedPreferences(Constants.parentPreferenceKey,
                Context.MODE_PRIVATE);
        pref.edit().clear().commit();
    }

    public static void setFCMId(Context context, String fcmId){
        SharedPreferences pref = context.getSharedPreferences(Constants.devicePreferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.fcmId, fcmId);
        editor.apply();
    }

    public static String getFCMId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.devicePreferenceKey, Context.MODE_PRIVATE);
        return prefs.getString(Constants.fcmId, "");
    }

    /**
     * Method to save the user current location to user preference
     * @param location
     */
    public static void setUserLocation(Context context, Location location){
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        prefs.edit().putFloat(Constants.lat, (float) location.getLatitude()).commit();
        prefs.edit().putFloat(Constants.lng, (float) location.getLongitude()).commit();


    }

    /**
     * method to get the user current location from user preferences
     * @return
     */
    public static LatLng getUserLocation(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        LatLng latLng = new LatLng(prefs.getFloat(Constants.lat, 0), prefs.getFloat(Constants.lng, 0));

        return latLng;
    }

    public static void setTripData(Context context, String tripData){
        SharedPreferences pref = context.getSharedPreferences(Constants.parentPreferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.tripData, tripData);
        editor.apply();
    }

    public static String getTripData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.parentPreferenceKey, Context.MODE_PRIVATE);
        return prefs.getString(Constants.tripData, "");
    }

}
