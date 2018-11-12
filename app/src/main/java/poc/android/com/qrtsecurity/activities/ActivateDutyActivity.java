package poc.android.com.qrtsecurity.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import poc.android.com.qrtsecurity.AppController;
import poc.android.com.qrtsecurity.CustomDialog.TripInfoCustomDialog;
import poc.android.com.qrtsecurity.Models.NotificationModel;
import poc.android.com.qrtsecurity.Models.ResponderModel;
import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.MyFirebaseMessagingService;
import poc.android.com.qrtsecurity.services.ResponderLocationService;
import poc.android.com.qrtsecurity.utils.AppPreferencesHandler;
import poc.android.com.qrtsecurity.utils.Constants;
import poc.android.com.qrtsecurity.utils.HelperMethods;
import poc.android.com.qrtsecurity.utils.VolleySingleton;
import poc.android.com.qrtsecurity.volleyWrapperClasses.UTF8JsonObjectRequest;
import poc.android.com.qrtsecurity.volleyWrapperClasses.UTF8StringRequest;

public class ActivateDutyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int LOCATION_REQUEST = 1003;
    private static final int LOCATION_PERMISSION_CODE = 102;
    private static final int POST_DUTY_API = 101;
    private static final int PUT_DUTY_API = 102;
    private static final int GET_DUTY_API = 103;

    private ImageButton btnEditProfile;
    private NetworkImageView ivProfile;
    private Button btnDutySwitch;
    private TextView tvTimer, tvName, tvModel;
    ObjectAnimator objAnim;
    private int apiStatus = -1;

    private boolean isDutyOn = false;
    private Timer timer;
    final Handler handler = new Handler();
    private boolean isAPIError = false, isRequest = false;
    private ImageLoader mImageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_duty);

        setUI();
    }

    /**
     * method to set the UI elements
     */
    private void setUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnDutySwitch = findViewById(R.id.btn_duty_switch);
        tvTimer = findViewById(R.id.tv_timer);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        btnEditProfile = headerView.findViewById(R.id.btn_edit_profile);
        tvName = headerView.findViewById(R.id.tv_name);
        tvModel = headerView.findViewById(R.id.tv_model);
        ivProfile = headerView.findViewById(R.id.iv_profile);

        btnEditProfile.setOnClickListener(this);
        btnDutySwitch.setOnClickListener(this);
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        ivProfile.setDefaultImageResId(R.drawable.default_profile);

        updateUser();


    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void openTripInfoDialog(NotificationModel data){
        final TripInfoCustomDialog tripDialog = new TripInfoCustomDialog(this, data);
        tripDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tripDialog.setCancelable(false);
        tripDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                if (tripDialog.isAccepted){
                    String tripData = getIntent().getStringExtra(MyFirebaseMessagingService.dataKey);
                    AppPreferencesHandler.setTripData(ActivateDutyActivity.this, tripData);
                    Intent mapIntent = new Intent(ActivateDutyActivity.this, MapActivity.class);
                    mapIntent.putExtra(MyFirebaseMessagingService.dataKey, tripData);
                    ActivateDutyActivity.this.startActivity(mapIntent);
                }

            }
        });

        tripDialog.show();
    }

    private void updateUser(){
        ResponderModel user = AppPreferencesHandler.getUserDetails(this);
        tvName.setText(user.getResponderName());
        tvModel.setText(user.getVehicleRegNo());

        if (user.getPhoto() != null && !user.getPhoto().isEmpty()){
            ivProfile.setImageUrl(Constants.imageBaseUrl+user.getPhoto(), mImageLoader);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mav_map) {

            String tripData = AppPreferencesHandler.getTripData(ActivateDutyActivity.this);

            if (!tripData.isEmpty()) {
                Intent mapIntent = new Intent(ActivateDutyActivity.this, MapActivity.class);
                mapIntent.putExtra(MyFirebaseMessagingService.dataKey, tripData);
                ActivateDutyActivity.this.startActivity(mapIntent);
            }else{
                Toast.makeText(this, "No Trip available.", Toast.LENGTH_SHORT).show();
            }

        }  else if (id == R.id.nav_history) {

        }else if (id == R.id.nav_contacts) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            AppPreferencesHandler.clearData(this);
            stopLocationService();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_profile:
                startActivity(new Intent(this, CompleteProfileActivity.class));
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.btn_duty_switch:

                if (isDutyOn) {
                    AppPreferencesHandler.setDutyState(this, false);
                    Log.d("Duty","OFF");
                    putSchedule();

                } else {
                    AppPreferencesHandler.setDutyStartTime(this, Calendar.getInstance().getTimeInMillis());
                    AppPreferencesHandler.setDutyState(this, true);
                    Log.d("Duty","ON");
                    postSchedule();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               startLocationService();
            } else {
                Toast.makeText(this, "location permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * method to start the service which will monitor the responder location
     */
    private void startLocationService(){
//        Intent serviceIntent = new Intent(this, ResponderLocationService.class);
//        startService(serviceIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            try{
                Intent foregroundIntent = new Intent(this, ResponderLocationService.class);
                this.startForegroundService(foregroundIntent);
            }catch (Exception ex){
               ex.printStackTrace();
            }

        }else{
            Intent foregroundIntent = new Intent(this, ResponderLocationService.class);
            this.startService(foregroundIntent);

        }
    }

    /**
     * method to stop the service which will monitor the responder location
     */
    private void stopLocationService(){
        Intent serviceIntent = new Intent(this, ResponderLocationService.class);
        stopService(serviceIntent);
    }

    private void startDuty() {
        isDutyOn = true;
        btnDutySwitch.setBackgroundResource(R.drawable.duty_on_bg);
        btnDutySwitch.setText(getString(R.string.duty_on));
        tvTimer.setText(getDutyTimer());
        startTimer();
    }

    private void stopDuty() {
        isDutyOn = false;
        btnDutySwitch.setBackgroundResource(R.drawable.duty_off_bg);
        btnDutySwitch.setText(getString(R.string.duty_off));
        tvTimer.setText("00:00:00");
//        stopPulseAnimation();
        stopTimer();
    }

    @Override
    public Intent getIntent() {
        return super.getIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateUser();

        if (AppPreferencesHandler.getDutyState(this)){
            startDuty();
        }else{
            stopDuty();
        }



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 300ms
                if (getIntent().getStringExtra(MyFirebaseMessagingService.dataKey) != null && !isRequest){
                    isRequest = true;
                    NotificationModel data = new Gson().fromJson(getIntent().getStringExtra(MyFirebaseMessagingService.dataKey), NotificationModel.class);
                    openTripInfoDialog(data);


                }
            }
        }, 300);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    private void startTimer() {

        //set a new Timer
        Log.d("timer", "Strat Timer");
        timer = new Timer();

        //initialize the TimerTask's job

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
//                        Log.d("Duty","Set time");
                        tvTimer.setText(getDutyTimer());

                    }
                });
            }
        };


        //schedule the timer, after the first 0ms the TimerTask will run every 2000ms

        timer.schedule(timerTask, 0, 2000);

    }

    /**
     * method to get the duty duration in string
     *
     * @return
     */
    private String getDutyTimer() {

        long duration = getDutyDuration();

        if (duration < 0) {
            Log.d("Duty","zero duration");
            return "00:00:00";
        } else {
            Log.d("Duty"," duration::"+ duration);
            long mins = duration / 60000;
            return "" + String.format("%02d",(mins / 60)) + ":" +  String.format("%02d",(mins % 60)) + ":" +
                    String.format("%02d", ((duration % 60000) / 1000));
        }

    }

    private long getDutyDuration() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long startedTime = AppPreferencesHandler.getDutyStartTime(this);

        return currentTime - startedTime;
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("response", ""+response);
            switch (apiStatus){
                case POST_DUTY_API:
                    startDuty();


                    getSchedules();
                    Toast.makeText(ActivateDutyActivity.this, "Duty Started.", Toast.LENGTH_SHORT).show();
                    break;

                case GET_DUTY_API:
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0){
                            JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);
                            int sId = jsonObject.getInt("sId");
                            AppPreferencesHandler.setScheduleId(getApplicationContext(), sId);
                            Toast.makeText(ActivateDutyActivity.this, "Schedule ID:"+ sId, Toast.LENGTH_SHORT).show();
                        }

                        if(isAPIError){
                            putSchedule();
                        }else {
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                            != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_CODE);
                            } else {
                                startLocationService();
                            }
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    break;
                    
                case PUT_DUTY_API:

                    if(isAPIError){
                        isAPIError = false;
                        postSchedule();
                    }else {
                        stopDuty();
                        stopLocationService();
                        Toast.makeText(ActivateDutyActivity.this, "Duty Stoped", Toast.LENGTH_SHORT).show();
                    }


                    break;
            }

        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("error", ""+error.getLocalizedMessage());

            switch (apiStatus) {
                case POST_DUTY_API:
                    getSchedules();
                    isAPIError = true;
                    break;
                default:
                    Toast.makeText(ActivateDutyActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };


    private void postSchedule() {

        apiStatus = POST_DUTY_API;
        String url = Constants.baseUrl + String.format(Constants.responderScheduleEndPoint, AppPreferencesHandler.getUserId(this));
        Log.d("post url", url);
        JSONObject payload = new JSONObject();

        Log.d("payload", payload.toString());
        if (HelperMethods.isNetWorkAvailable(this)) {

            UTF8StringRequest request = new UTF8StringRequest(Request.Method.POST, url, responseListener, errorListener)
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type",
                            "application/json");
                    header.put("Authorization",  AppPreferencesHandler.getUserToken(ActivateDutyActivity.this));

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
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT)
                    .show();

        }
    }

    private void putSchedule() {

        apiStatus = PUT_DUTY_API;
        String url = Constants.baseUrl + String.format(Constants.responderScheduleEndPoint, AppPreferencesHandler.getUserId(this)) +
                "/"+ AppPreferencesHandler.getScheduleId(getApplicationContext());
        Log.d("put url", url);
        JSONObject payload = new JSONObject();

        try{
            payload.put("currAvail", false);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        Log.d("payload", payload.toString());
        if (HelperMethods.isNetWorkAvailable(this)) {

            UTF8StringRequest request = new UTF8StringRequest(Request.Method.PUT, url, responseListener, errorListener)
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type","application/x-www-form-urlencoded");
                    header.put("Authorization",  AppPreferencesHandler.getUserToken(ActivateDutyActivity.this));

                    return header;
                }

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("currAvail", "false");
                    return params;
                }
            };

            RetryPolicy retryPolicy = new DefaultRetryPolicy(
                    AppController.VOLLEY_TIMEOUT,
                    AppController.VOLLEY_MAX_RETRIES,
                    AppController.VOLLEY_BACKUP_MULT);
            request.setRetryPolicy(retryPolicy);
            AppController.getInstance().addToRequestQueue(request);

        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT)
                    .show();

        }
    }

    private void getSchedules() {

        apiStatus = GET_DUTY_API;
        String url = Constants.baseUrl + String.format(Constants.responderScheduleEndPoint, AppPreferencesHandler.getUserId(this));
        Log.d("get url", url);
        JSONObject payload = new JSONObject();

        Log.d("payload", payload.toString());
        if (HelperMethods.isNetWorkAvailable(this)) {

            UTF8StringRequest request = new UTF8StringRequest(Request.Method.GET, url, responseListener, errorListener)
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type",
                            "application/json");
                    header.put("Authorization",  AppPreferencesHandler.getUserToken(ActivateDutyActivity.this));

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
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT)
                    .show();

        }
    }

}
