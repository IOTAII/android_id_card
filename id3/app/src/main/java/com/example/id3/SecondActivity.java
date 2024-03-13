package com.example.id3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SecondActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private MapView mapView;
    private final String mapId = "streets-v2";
    private final String apiKey = "O8hzf5l378NIwtGVcvEF";
    private LatLng currentLocation = new LatLng(22.5726, 88.3639);
    private TextView showTextView;
    private String userId;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this);
        setContentView(R.layout.activity_second);

        userId = getIntent().getStringExtra("userId");

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_history) {
                    // Handle history item click
                    Intent historyIntent = new Intent(SecondActivity.this, History.class);
                    startActivity(historyIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_product) {
                    // Handle product item click
                    Intent productIntent = new Intent(SecondActivity.this, product.class);
                    startActivity(productIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_info) {
                    // Handle info item click
                    Intent infoIntent = new Intent(SecondActivity.this, info.class);
                    // Pass user ID and device ID to the info class
                    infoIntent.putExtra("userId", userId);
                    Button deviceButton = findViewById(R.id.Device);
                    String deviceId = (String) deviceButton.getTag();
                    infoIntent.putExtra("deviceId", deviceId);
                    startActivity(infoIntent);
                } else if (item.getItemId() == R.id.nav_sub) {
                    // Handle settings item click
                    Intent subscriptionIntent = new Intent(SecondActivity.this, sub.class);
                    startActivity(subscriptionIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_Contact) {
                    // Handle settings item click
                    String url = "https://iotaii.com/";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        Button deviceButton = findViewById(R.id.Device);
        Button hisbutton = findViewById(R.id.history);
        showTextView = findViewById(R.id.show);



        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDevicesForUser(userId);
            }
        });

        hisbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, History.class);
                intent.putExtra("userId", userId);
                Button deviceButton = findViewById(R.id.Device);
                String deviceId = (String) deviceButton.getTag();
                intent.putExtra("deviceId", deviceId);
                startActivity(intent);
            }
        });


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.setStyle(new Style.Builder().fromUri("https://api.maptiler.com/maps/" + mapId + "/style.json?key=" + apiKey));
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(currentLocation).zoom(10.0).build()));
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchDevicesForUser(String userId) {
        String apiUrl = "http://3.109.34.34:8080/devices/" + userId;

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        return stringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String devicesJson) {
                if (devicesJson != null) {
                    try {
                        JSONArray devicesArray = new JSONArray(devicesJson);
                        if (devicesArray.length() > 0) {
                            JSONObject device = devicesArray.getJSONObject(0);
                            String deviceId = device.getString("device_id");
                            Button deviceButton = findViewById(R.id.Device);
                            deviceButton.setText(deviceId);
                            deviceButton.setTag(deviceId); // Set tag to identify the selected device
                            deviceButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String selectedDeviceId = (String) v.getTag();
                                    fetchLocationForDevice(userId, selectedDeviceId);
                                }
                            });
                        } else {
                            Toast.makeText(SecondActivity.this, "No devices found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SecondActivity.this, "Error fetching devices", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(apiUrl);
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchLocationForDevice(String userId, String deviceId) {
        String apiUrl = "http://3.109.34.34:8080/live-location-summary/" + userId + "/" + deviceId;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        return stringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String liveLocationJson) {
                if (liveLocationJson != null) {
                    try {
                        JSONObject locationObject = new JSONObject(liveLocationJson);
                        String latitudeString = locationObject.getString("latitude");
                        String longitudeString = locationObject.getString("longitude");
                        double latitude = parseLatitude(latitudeString);
                        double longitude = parseLongitude(longitudeString);
                        LatLng newLocation = new LatLng(latitude, longitude);
                        updateMarkerPosition(newLocation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SecondActivity.this, "Error fetching live location", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(apiUrl);
    }

    private double parseLatitude(String latitudeString) {
        return Double.parseDouble(latitudeString.substring(0, latitudeString.length() - 1));
    }

    private double parseLongitude(String longitudeString) {
        return Double.parseDouble(longitudeString.substring(0, longitudeString.length() - 1));
    }

    private void updateMarkerPosition(LatLng newLocation) {
        currentLocation = newLocation;
        if (mapView != null) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    mapboxMap.clear();
                    mapboxMap.addMarker(new MarkerOptions().position(currentLocation));
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
