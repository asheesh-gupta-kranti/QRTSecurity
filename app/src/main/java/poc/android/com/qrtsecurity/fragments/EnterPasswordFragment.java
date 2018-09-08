package poc.android.com.qrtsecurity.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import poc.android.com.qrtsecurity.AppController;
import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.activities.HomeActivity;
import poc.android.com.qrtsecurity.utils.AppPreferencesHandler;
import poc.android.com.qrtsecurity.utils.Constants;
import poc.android.com.qrtsecurity.utils.HelperMethods;
import poc.android.com.qrtsecurity.volleyWrapperClasses.UTF8JsonObjectRequest;

public class EnterPasswordFragment extends Fragment implements View.OnClickListener{

    private TextInputLayout tilPassword;
    private EditText etPassword;
    private ProgressBar progressBar;
    private Button btnSubmit, btnForgotPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_password, container, false);
        // set font for layout

        tilPassword = view.findViewById(R.id.til_password);
        etPassword = view.findViewById(R.id.et_password);
        progressBar = view.findViewById(R.id.progressBar);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnForgotPassword = view.findViewById(R.id.btn_forgot_password);

        btnSubmit.setOnClickListener(this);
        return view;
    }

    /**
     * method to check if the password is valid or not
     * @return
     */
    private boolean isPasswordsValid(){

        if (etPassword.getText().toString().isEmpty()){
            tilPassword.setErrorEnabled(true);
            tilPassword.setError(getString(R.string.invalid_password));
            return false;
        }else if (etPassword.getText().toString().contains(" ")){
            tilPassword.setErrorEnabled(true);
            tilPassword.setError(getString(R.string.invalid_password));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                if (isPasswordsValid()){
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(AppPreferencesHandler.getUserPhoneNumber(getActivity()), etPassword.getText().toString());
                }
                break;
        }
    }

    /**
     * Method to login the user using api call
     * @param phoneNumber
     * @param password
     */
    private void loginUser(String phoneNumber, String password) {

        String url = Constants.baseUrl + Constants.responserLoginEndPoint ;
        JSONObject payload = new JSONObject();
        try{

            payload.put("responderPhone", phoneNumber);
            payload.put("password", password);
        }catch (Exception ex){
            ex.printStackTrace();
            return;
        }

        if (HelperMethods.isNetWorkAvailable(getActivity())) {

            UTF8JsonObjectRequest request = new UTF8JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("registerUser response===", response.toString());
                    progressBar.setVisibility(View.GONE);
                    AppPreferencesHandler.setLoginStatus(getActivity(), true);
                    Toast.makeText(getActivity(), getString(R.string.success_login), Toast.LENGTH_SHORT).show();
                    openHomeActivity();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);

                    if(error.networkResponse.statusCode == 401){
                        Toast.makeText(getActivity(), getString(R.string.invalid_password_login), Toast.LENGTH_SHORT)
                                .show();
                    }else {
                        Toast.makeText(getActivity(), getString(R.string.general_error), Toast.LENGTH_SHORT)
                                .show();
                    }
                    progressBar.setVisibility(View.GONE);

                }
            }){

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

    /**
     * Method to open the home activity after login
     */
    private void openHomeActivity(){
        getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
        getActivity().finish();
    }
}
