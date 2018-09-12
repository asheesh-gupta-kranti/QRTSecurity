package poc.android.com.qrtsecurity.fragments;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import poc.android.com.qrtsecurity.AppController;
import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.activities.MainActivity;
import poc.android.com.qrtsecurity.utils.AppPreferencesHandler;
import poc.android.com.qrtsecurity.utils.Constants;
import poc.android.com.qrtsecurity.utils.HelperMethods;
import poc.android.com.qrtsecurity.volleyWrapperClasses.UTF8StringRequest;

public class SignInFragment extends Fragment implements View.OnClickListener{

    private EditText etPhoneNumber;
    private TextInputLayout tilPhoneNumber;
    private ProgressBar progressBar;
    private Button btnSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        // set font for layout

        btnSignIn = view.findViewById(R.id.btn_sign_in);
        etPhoneNumber = view.findViewById(R.id.et_phone_number);
        tilPhoneNumber = view.findViewById(R.id.til_phone_number);
        progressBar = view.findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(this);
        etPhoneNumber.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    btnSignIn.performClick();
                    return false;
                }

                return false;
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_sign_in:
                if (!HelperMethods.isValidMobileNumber(etPhoneNumber.getText().toString())){
                    // phone number not valid
                    tilPhoneNumber.setErrorEnabled(true);
                    tilPhoneNumber.setError(getString(R.string.invalid_phone_number));
                }else{
                    // API call to check the user is registered or not
                    progressBar.setVisibility(View.VISIBLE);
                    AppPreferencesHandler.setUserPhoneNumber(getActivity(), etPhoneNumber.getText().toString());
                    checkIsAlreadyRegistered(etPhoneNumber.getText().toString());
                }

                break;
        }

    }

    /**
     * Method to call the passwordFragment over the current fragment
     */
    private void callPasswordFragment(){
        try {
            MainActivity activity = (MainActivity) getActivity();
            activity.addPasswordFragment();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
    /**
     * Method to call the createPasswordFragment over the current fragment
     */
    private void callCreatePasswordFragment(){
        try {
            MainActivity activity = (MainActivity) getActivity();
            activity.addCreatePassswordFragment();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    /**
     * method to get the user details if user is already registered
     * @param phoneNumber
     */
    private void checkIsAlreadyRegistered(String phoneNumber) {

        String url = Constants.baseUrl + Constants.responderAPIEndPoint + "?filter={\"where\":{\"responderPhone\":\""+phoneNumber+"\"}}";
        Log.d("url", url);
        if (HelperMethods.isNetWorkAvailable(getActivity())) {
            UTF8StringRequest request = new UTF8StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    Log.d("checkIsAlready response", response.toString());
                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0){
                            // user is already registered


                            callPasswordFragment();
                        }else{
                            // not registered
                            callCreatePasswordFragment();
                        }
                    }catch (JSONException ex){
                        ex.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("error", ""+error);
                    Toast.makeText(getActivity(), getString(R.string.general_error), Toast.LENGTH_SHORT)
                            .show();
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

        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT)
                    .show();
            progressBar.setVisibility(View.GONE);
        }
    }
}
