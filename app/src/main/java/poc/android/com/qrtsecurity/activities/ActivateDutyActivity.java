package poc.android.com.qrtsecurity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import poc.android.com.qrtsecurity.Models.ResponderModel;
import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.services.ResponderLocationService;
import poc.android.com.qrtsecurity.utils.AppPreferencesHandler;

public class ActivateDutyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ImageButton btnEditProfile;
    private Button btnDutySwitch;
    private TextView tvTimer, tvName, tvModel;

    private boolean isDutyOn = false;
    private Timer timer;
    final Handler handler = new Handler();


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

        ResponderModel user = AppPreferencesHandler.getUserDetails(this);
        tvName.setText(user.getResponderName());
        tvModel.setText(user.getVehicleRegNo());

        btnEditProfile.setOnClickListener(this);
        btnDutySwitch.setOnClickListener(this);

        if (AppPreferencesHandler.getDutyState(this)) {
            startDuty();
        } else {
            stopDuty();
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

        if (id == R.id.nav_history) {

        } else if (id == R.id.nav_contacts) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            AppPreferencesHandler.setLoginStatus(this, false);
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
                break;

            case R.id.btn_duty_switch:

                if (isDutyOn) {
                    AppPreferencesHandler.setDutyState(this, false);
                    Log.d("Duty","OFF");
                    stopDuty();
                    stopLocationService();
                } else {
                    AppPreferencesHandler.setDutyStartTime(this, Calendar.getInstance().getTimeInMillis());
                    AppPreferencesHandler.setDutyState(this, true);
                    Log.d("Duty","ON");
                    startDuty();
                    startLocationService();
                }
                break;
        }
    }

    /**
     * method to start the service which will monitor the responder location
     */
    private void startLocationService(){
        Intent serviceIntent = new Intent(this, ResponderLocationService.class);
        startService(serviceIntent);
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
        Log.d("Duty","Start Timer");
        startTimer();
    }

    private void stopDuty() {
        isDutyOn = false;
        btnDutySwitch.setBackgroundResource(R.drawable.duty_off_bg);
        btnDutySwitch.setText(getString(R.string.duty_off));
        tvTimer.setText("00:00");

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    private void startTimer() {

        //set a new Timer

        timer = new Timer();

        //initialize the TimerTask's job

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        Log.d("Duty","Set time");
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

}
