package poc.android.com.qrtsecurity.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import poc.android.com.qrtsecurity.R;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class ResponderLocationService extends Service {
    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1; // 1 min
    private static final float LOCATION_DISTANCE = 2f; // 10 meters
    public static String CHANNEL_ONE_ID = "com.qrtservices";
    public static final int foregroundServiceId = 100;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            Toast.makeText(ResponderLocationService.this, "Location:" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    /*
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    */

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");

        initializeLocationManager();

        if (mLocationManager.getAllProviders() != null ) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                // permission not granted
                return;
            }
            if (mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                try {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[0]);
                    showNotification();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Or  Register the listener with Location Manager's gps provider
            if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                try {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[0]);
                    showNotification();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    LOCATION_INTERVAL,
//                    LOCATION_DISTANCE,
//                    mLocationListeners[0]
//            );
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//        }

        /*try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[1]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }*/
    }

    private void showNotification(){
        Notification notification = null;
        NotificationChannel notificationChannel = null;
       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
           notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                        "Location service", IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            notification = new Notification.Builder(getApplicationContext())
                    .setChannelId(CHANNEL_ONE_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("We are monitoring your location.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
        }else{
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("We are monitoring your location.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

        }

        // Start foreground service
        startForeground(foregroundServiceId, notification);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
