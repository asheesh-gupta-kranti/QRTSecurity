package poc.android.com.qrtsecurity.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import poc.android.com.qrtsecurity.utils.AppPreferencesHandler;
import poc.android.com.qrtsecurity.utils.WebServices;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM ID:", "Refreshed token: " + refreshedToken);

        AppPreferencesHandler.setFCMId(this, refreshedToken);
        if (AppPreferencesHandler.isLogin(this)){
            WebServices.postFCMId(this);
        }

    }
}
