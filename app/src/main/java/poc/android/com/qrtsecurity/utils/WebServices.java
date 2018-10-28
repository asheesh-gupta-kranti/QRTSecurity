package poc.android.com.qrtsecurity.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import poc.android.com.qrtsecurity.AppController;
import poc.android.com.qrtsecurity.Models.ResponderModel;
import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.activities.CompleteProfileActivity;
import poc.android.com.qrtsecurity.volleyWrapperClasses.UTF8JsonObjectRequest;

//{
//        "appId": "QRT-push-app",
//        "deviceToken": "1234new",
//        "deviceType": "android",
//        "status": "Active",
//        "userId": "5" // responderId
//        }

public class WebServices {

    public static void postFCMId(final Context context) {

        try {
            JSONObject payload = new JSONObject();

            payload.put("appId", "QRT-push-app");
            payload.put("deviceToken", AppPreferencesHandler.getFCMId(context));
            payload.put("deviceType", "android");
            payload.put("status", "Active");
            payload.put("userId", AppPreferencesHandler.getUserId(context));
            String url = Constants.baseUrl + Constants.fcmRegisterEndPoint;

            UTF8JsonObjectRequest request = new UTF8JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("FCM response", response.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", "" + error);


                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type",
                            "application/json");

                    return header;
                }
            };

            RetryPolicy retryPolicy = new DefaultRetryPolicy(
                    AppController.VOLLEY_TIMEOUT,
                    AppController.VOLLEY_MAX_RETRIES,
                    AppController.VOLLEY_BACKUP_MULT);
            request.setRetryPolicy(retryPolicy);
            AppController.getInstance().addToRequestQueue(request);
        } catch (Exception ex) {
            Log.d("error", ex.getMessage());
        }

    }

}
