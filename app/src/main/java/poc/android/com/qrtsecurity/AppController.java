package poc.android.com.qrtsecurity;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ashutoshmishra on 07/09/18.
 */

public class AppController extends Application {

    public static final int VOLLEY_TIMEOUT = 30000;
    public static final int VOLLEY_MAX_RETRIES = 0;
    public static final float VOLLEY_BACKUP_MULT = 2;

    private RequestQueue mRequestQueue;
    private static AppController mInstance;

    private static final String TAG = AppController.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        notificationChannels();

    }

    /*
	This method return the instance of App controller
	 */
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /*
    This method return the volley request queue
     */
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /*
    This is use to add new request to the volley queue
    @param req : request object
    @param tag: string tag for the request
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    /*
    This is use to add new request to the volley queue
    @param req : request object
     */
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /*
    This method is use cancel all the pending request in volley request queue
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void cancelPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    private void notificationChannels(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_QRT, CHANNEL_ID_QRT, importance);
            notificationManager.createNotificationChannel(notificationChannel);



            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("Background", "Background"));
        }
    }

    public static final String CHANNEL_ID_QRT = "channel_id_qrt";
}
