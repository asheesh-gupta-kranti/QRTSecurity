package poc.android.com.qrtsecurity.fragments;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import poc.android.com.qrtsecurity.AppController;
import poc.android.com.qrtsecurity.Models.ResponderModel;
import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.activities.CompleteProfileActivity;
import poc.android.com.qrtsecurity.activities.HomeActivity;
import poc.android.com.qrtsecurity.utils.AppPreferencesHandler;
import poc.android.com.qrtsecurity.utils.Constants;
import poc.android.com.qrtsecurity.utils.HelperMethods;
import poc.android.com.qrtsecurity.utils.WebServices;
import poc.android.com.qrtsecurity.volleyWrapperClasses.UTF8JsonObjectRequest;
import poc.android.com.qrtsecurity.volleyWrapperClasses.UTF8StringRequest;

public class CreatePasswordFragment extends Fragment implements View.OnClickListener{

    private TextInputLayout tilPassword, tilConfirmPassword;
    private EditText etPassword, etConfirmPassword;
    private Button btnSubmit;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_password, container, false);
        // set font for layout

        tilPassword = view.findViewById(R.id.til_password);
        tilConfirmPassword = view.findViewById(R.id.til_confirm_password);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnSubmit = view.findViewById(R.id.btn_submit);
        progressBar = view.findViewById(R.id.progressBar);

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
        }else if (etConfirmPassword.getText().toString().isEmpty()){
            tilConfirmPassword.setErrorEnabled(true);
            tilConfirmPassword.setError(getString(R.string.invalid_confirm_password));
            return false;
        }else if (etPassword.getText().toString().contains(" ") || etPassword.getText().toString().length() < 6){
            tilPassword.setErrorEnabled(true);
            tilPassword.setError(getString(R.string.invalid_password));
            return false;
        }else if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
            tilConfirmPassword.setErrorEnabled(true);
            tilConfirmPassword.setError(getString(R.string.invalid_confirm_password));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (isPasswordsValid()){
            progressBar.setVisibility(View.VISIBLE);
            registerUser(AppPreferencesHandler.getUserPhoneNumber(getActivity()), etPassword.getText().toString());
        }
    }

    /**
     * Method to register the user using api call
     * @param phoneNumber
     * @param password
     */
    private void registerUser(String phoneNumber, String password) {

        String url = Constants.baseUrl + Constants.responderAPIEndPoint ;
        JSONObject payload = new JSONObject();
        try{

            payload.put("responderPhone", phoneNumber);
            payload.put("password", password);
        }catch (Exception ex){
            ex.printStackTrace();
            return;
        }
        Log.d("register params", payload.toString());
        if (HelperMethods.isNetWorkAvailable(getActivity())) {

            UTF8JsonObjectRequest request = new UTF8JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("registerUser response", response.toString());
                    loginUser(AppPreferencesHandler.getUserPhoneNumber(getActivity()), etPassword.getText().toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                                        Log.e("error", ""+error);
                    Toast.makeText(getActivity(), getString(R.string.general_error), Toast.LENGTH_SHORT)
                            .show();
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
     * Method to login the user using api call
     * @param phoneNumber
     * @param password
     */
    private void loginUser(String phoneNumber, String password) {

        String url = Constants.baseUrl + Constants.responderLoginEndPoint ;
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
                    Log.d("login response", response.toString());

                    try{
                        String id = response.getString("id");
                        int userId = response.getInt("userId");
                        AppPreferencesHandler.setUserToken(getActivity(), id);
                        AppPreferencesHandler.setUserId(getActivity(), userId);
                        AppPreferencesHandler.setLoginStatus(getActivity(), true);
                        getUserDetails(userId);

                        if (!AppPreferencesHandler.getFCMId(CreatePasswordFragment.this.getContext()).isEmpty()){
                            WebServices.postFCMId(CreatePasswordFragment.this.getContext());
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.general_error), Toast.LENGTH_SHORT)
                                .show();

                    }

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
     * Method to open the complete profile activity after login
     */
    private void openCompleteProfileActivity(){
        getActivity().startActivity(new Intent(getActivity(), CompleteProfileActivity.class));
        getActivity().finish();
    }

    /**
     * method to get the user details
     *
     * @param id
     */
    private void getUserDetails(int id) {

        String url = Constants.baseUrl + Constants.responderAPIEndPoint + "/" + id;
        Log.d("url", url);
        if (HelperMethods.isNetWorkAvailable(getActivity())) {
            UTF8StringRequest request = new UTF8StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    Log.d("getUserDetails response", response.toString());
                    ResponderModel user = new Gson().fromJson(response.toString(), ResponderModel.class);
                    AppPreferencesHandler.saveUserDetails(CreatePasswordFragment.this.getActivity(), user);
                    Toast.makeText(getActivity(), getString(R.string.success_login), Toast.LENGTH_SHORT).show();
                    openCompleteProfileActivity();
                    progressBar.setVisibility(View.GONE);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("error", "" + error);
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
