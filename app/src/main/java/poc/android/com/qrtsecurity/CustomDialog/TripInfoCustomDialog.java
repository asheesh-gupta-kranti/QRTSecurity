package poc.android.com.qrtsecurity.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import poc.android.com.qrtsecurity.AppController;
import poc.android.com.qrtsecurity.Models.NotificationModel;
import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.services.ResponderLocationService;
import poc.android.com.qrtsecurity.utils.AppPreferencesHandler;
import poc.android.com.qrtsecurity.utils.Constants;
import poc.android.com.qrtsecurity.volleyWrapperClasses.UTF8JsonObjectRequest;

public class TripInfoCustomDialog extends Dialog implements View.OnClickListener {

    private TextView tvMessage;
    private Button btnAccept, btnReject;
    private ProgressBar progressBar;

    private NotificationModel data;
    public boolean isAccepted = false;

    public TripInfoCustomDialog(Context context, NotificationModel data) {
        super(context);

        this.data = data;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_trip_info);

        tvMessage = findViewById(R.id.tv_message);
        btnAccept = findViewById(R.id.btn_accept);
        btnReject = findViewById(R.id.btn_reject);
        progressBar = findViewById(R.id.progressBar);

        btnReject.setOnClickListener(this);
        btnAccept.setOnClickListener(this);

        tvMessage.setText(String.format(String.format(getContext().getString(R.string.notification_message), data.getPassengerName())));

    }

    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);

        if (v == btnAccept){
            isAccepted = true;
            postStatus("ACCEPTED");
        }else if (v == btnReject){
            postStatus("CANCELLED");
        }



    }

    private void postStatus(String status) {

        String url = Constants.baseUrl + Constants.requestStatusUpdateEndPoint
                +  "?where={\"and\":[{\"tripId\":\""+data.getTripId()+"\"},{\"responderId\":"+AppPreferencesHandler.getScheduleId(getContext())+"}]}";
        Log.d("url", url);
        JSONObject payload = new JSONObject();
        try {

            payload.put("responderStatus", status);
        }catch (Exception ex){
            ex.printStackTrace();
        }


        Log.d("payload", payload.toString());
        UTF8JsonObjectRequest request = new UTF8JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Location response", response.toString());

                Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                dismiss();
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error);

                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
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
    }
}