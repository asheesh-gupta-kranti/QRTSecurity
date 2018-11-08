package poc.android.com.qrtsecurity.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import poc.android.com.qrtsecurity.AppController;
import poc.android.com.qrtsecurity.CustomDialog.CompleteTripDialog;
import poc.android.com.qrtsecurity.CustomDialog.TripInfoCustomDialog;
import poc.android.com.qrtsecurity.Models.NotificationModel;
import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.MyFirebaseMessagingService;
import poc.android.com.qrtsecurity.utils.AppPreferencesHandler;
import poc.android.com.qrtsecurity.utils.Constants;
import poc.android.com.qrtsecurity.utils.DirectionsJSONParser;
import poc.android.com.qrtsecurity.volleyWrapperClasses.UTF8JsonObjectRequest;

public class MapActivity  extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    private LatLng myLocationLatLng;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient googleApiClient;
    private boolean firstTimeRedirected = false;
    LatLng origin, dest ;
    private NotificationModel data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        findViewById(R.id.btn_direction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (origin != null && dest != null) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + origin.latitude + "," + origin.longitude + "&daddr=" + dest.latitude + "," + dest.longitude));
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.btn_complete_trip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openTripCancelDialog(data);
            }
        });


        googleApiClient = new GoogleApiClient.Builder(MapActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        System.out.print("GoogleApiClient onConnected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        System.out.print("GoogleApiClient onConnectionSuspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        System.out.print("GoogleApiClient onConnectionFailed");
                    }
                }).build();
        googleApiClient.connect();

        if (myLocationLatLng == null)
            checkLocation();
    }

    private void checkLocation() {
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            if (mMap != null)
                mMap.setMyLocationEnabled(true);
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            } else {
                fetchMyLocation();
            }
        }
    }

    /**
     * This method is use to move the map to particular location
     *
     * @param latLng: new latlng of the map center
     */
    private void gotoLocation(LatLng latLng, float zoomLevel)
    {
        try {
            CameraUpdate cameraLocation = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            mMap.animateCamera(cameraLocation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fetchMyLocation() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (MapActivity.this != null && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(MapActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (MapActivity.this == null) {
                                    return;
                                }
                                System.out.print("initCurrentLocation OnSuccessListener : " + (location == null ? "null" : location.toString()));
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    myLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    if (mMap != null) {
                                        gotoLocation(myLocationLatLng, mMap.getCameraPosition().zoom);
                                    }
                                } else {
                                    System.out.print("initCurrentLocation LocationManager");
                                    final LocationManager locationManager = (LocationManager) MapActivity.this.getSystemService(Context.LOCATION_SERVICE);

                                    //Location Listener is an interface. It will be called every time when the location manager reacted.
                                    LocationListener locationListener = new LocationListener() {
                                        public void onLocationChanged(Location location) {
                                            System.out.print("initCurrentLocation LocationManager onLocationChanged");
                                            // This method is called when a new location is found by the network location provider or Gps provider.
                                            locationManager.removeUpdates(this);
                                            myLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            if (mMap != null) {
                                                gotoLocation(myLocationLatLng, mMap.getCameraPosition().zoom);
                                            }
                                        }

                                        public void onStatusChanged(String provider, int status, Bundle extras) {
                                            System.out.print("initCurrentLocation LocationManager onStatusChnaged");
                                        }

                                        public void onProviderEnabled(String provider) {
                                            System.out.print("initCurrentLocation LocationManager onProviderEnabled");
                                        }

                                        public void onProviderDisabled(String provider) {
                                           System.out.print("initCurrentLocation LocationManager onProviderDisabled");
                                        }
                                    };

                                    // Register the listener with Location Manager's network provider
                                    if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    assert locationManager != null;
                                    if (locationManager.getAllProviders() != null) {
                                        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                                            try {
                                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                                            } catch (Exception e) {
                                            }
                                        }
                                        //Or  Register the listener with Location Manager's gps provider
                                        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                                            try {
                                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("error", ""+ e.getLocalizedMessage());
                            }
                        });
                return null;
            }
        }.execute();
    }



    private void selectCameraLocation() {
        if (mMap != null)
            if (myLocationLatLng != null) {
                CameraUpdate cameraLocation = CameraUpdateFactory.newLatLngZoom(myLocationLatLng, 18);
                mMap.animateCamera(cameraLocation);
            }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Add a marker in Sydney and move the camera
        if (myLocationLatLng != null) {
            selectCameraLocation();
        } else {
            // Add a marker in NCR, India,
            // and move the map's camera to the same location.
            //28.632832, 77.219450
            LatLng ncrIndiaLatLng = new LatLng(28.632832, 77.219450);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ncrIndiaLatLng, 18));
        }

        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        if (getIntent().getStringExtra(MyFirebaseMessagingService.dataKey) != null){
            data = new Gson().fromJson(getIntent().getStringExtra(MyFirebaseMessagingService.dataKey), NotificationModel.class);
            origin = AppPreferencesHandler.getUserLocation(this);
            dest = new LatLng(data.getLat(), data.getLng());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 18));

            mMap.addMarker(new MarkerOptions().position(origin).title("Origin"));
            mMap.addMarker(new MarkerOptions().position(dest).title("Destination"));

            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }else{
            finish();
        }
    }

        private class DownloadTask extends AsyncTask<String,Void, String> {

            @Override
            protected String doInBackground(String... url) {

                String data = "";

                try {
                    data = downloadUrl(url[0]);
                    Log.d("data", ""+data);
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                ParserTask parserTask = new ParserTask();


                parserTask.execute(result);

            }
        }

        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }

            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                ArrayList points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();

                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    int color = ContextCompat.getColor(MapActivity.this, R.color.route_color);
                    lineOptions.color(color);
                    lineOptions.geodesic(true);

                }

// Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null)
                    mMap.addPolyline(lineOptions);
            }
        }

        private String getDirectionsUrl (LatLng origin, LatLng dest){

            // Origin of route
            String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

            // Destination of route
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

            // Sensor enabled
            String sensor = "sensor=false";
            String mode = "mode=driving";

            String key = "key=" + getString(R.string.google_maps_key);

            // Building the parameters to the web service
            String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&"+ key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


            return url;
        }

        private String downloadUrl (String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

    private void openTripCancelDialog(NotificationModel data){

        if (data != null) {
            final CompleteTripDialog tripDialog = new CompleteTripDialog(this, data);
            tripDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            tripDialog.setCancelable(false);
            tripDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                    if (tripDialog.isCancelTrip) {
                        postStatus("RESOLVED");
                    }

                }
            });

            tripDialog.show();
        }
    }

    private void postStatus(String status) {

        try {

            JSONObject params = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONObject obj1 = new JSONObject();
            obj1.put("tripId", data.getTripId());

            JSONObject obj2 = new JSONObject();
            obj2.put("responderId", AppPreferencesHandler.getUserId(MapActivity.this));

            jsonArray.put(obj1);
            jsonArray.put(obj2);

            params.put("and", jsonArray);


            String url = Constants.baseUrl + Constants.requestStatusUpdateEndPoint
                    + "?where=" + params.toString();
            Log.d("url", url);
            JSONObject payload = new JSONObject();


            payload.put("responderStatus", status);


            Log.d("payload", payload.toString());
            UTF8JsonObjectRequest request = new UTF8JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Location response", response.toString());
                    AppPreferencesHandler.setTripData(MapActivity.this, "");
                    Toast.makeText(MapActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    MapActivity.this.finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", "" + error);

                    Toast.makeText(MapActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();

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
            ex.printStackTrace();
        }
    }
    }
