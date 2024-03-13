package com.example.id3;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.android.material.navigation.NavigationView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Button optionsButton;

    // Mapbox variables
    private MapView mapView;
    private MapboxMap mapboxMap; // Make mapboxMap a class-level variable
    private final String mapId = "streets-v2"; // MapTiler map ID
    private final String apiKey = "O8hzf5l378NIwtGVcvEF"; // Replace with your MapTiler API key
    private LatLng currentLocation = new LatLng(22.5726, 88.3639); // Initial location
    private String userId;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Assuming userId and deviceId are obtained from Intent or somewhere else in your code
        userId = getIntent().getStringExtra("userId");
        deviceId = getIntent().getStringExtra("deviceId");

        // Log the userId and deviceId
        Log.d("History", "User ID: " + userId);
        Log.d("History", "Device ID: " + deviceId);

        // Setup the toolbar and navigation drawer toggle
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.syncState();

        // Setup navigation item click listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_history) {
                    // Handle history item click
                    Intent historyIntent = new Intent(History.this, History.class);
                    startActivity(historyIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_product) {
                    // Handle product item click
                    Intent productIntent = new Intent(History.this, product.class);
                    startActivity(productIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_info) {
                    // Handle settings item click
                    Intent infoIntent = new Intent(History.this, info.class);
                    startActivity(infoIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_sub) {
                    // Handle settings item click
                    Intent subscriptionIntent = new Intent(History.this, sub.class);
                    startActivity(subscriptionIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_Contact) {
                    // Handle Contact item click by opening the URL in a web browser
                    String url = "https://iotaii.com/";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Button to display options
        optionsButton = findViewById(R.id.buttonOptions);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsPopupMenu();
            }
        });

        // Initialize Mapbox with your API key
        Mapbox.getInstance(this);

        // Init the MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                History.this.mapboxMap = mapboxMap; // Assign mapboxMap to class-level variable
                mapboxMap.setStyle(new Style.Builder().fromUri("https://api.maptiler.com/maps/" + mapId + "/style.json?key=" + apiKey));
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(currentLocation).zoom(10.0).build()));

                // Fetch history route data and draw polyline with default filter as 6 months
                fetchHistoryAndDrawRoute("6months");
            }
        });
    }

    private void fetchHistoryAndDrawRoute(String filter) {
        String url = "http://3.109.34.34:8080/fetch-location-history/" + userId + "/" + deviceId + "?filter=" + filter;
        new FetchDataAsyncTask().execute(url);
    }

    private class FetchDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            return fetchDataFromUrl(url);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Parse JSON and draw polyline
                List<LatLng> coordinates = parseHistoryCoordinates(result);
                if (coordinates != null && !coordinates.isEmpty()) {
                    drawPolyline(coordinates);
                }
            } else {
                Log.e("History", "Failed to fetch data from URL");
            }
        }
    }


    private String fetchDataFromUrl(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                Log.d("History", "Received data from URL: " + jsonResponse);
                return jsonResponse;
            } else {
                Log.e("History", "Error response from server: " + response.code());
                return null;
            }
        } catch (Exception e) {
            Log.e("History", "Error fetching data from URL: " + e.getMessage());
            e.printStackTrace(); // Log the stack trace for debugging
            return null;
        }
    }



    private List<LatLng> parseHistoryCoordinates(String historyJson) {
        try {
            // Implement parsing history coordinates from JSON and return as a list of LatLng objects
            // Parse the JSON string and extract latitude and longitude values for each location
            // Construct LatLng objects and add them to a list
            // Example:
            List<LatLng> coordinates = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(historyJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                double latitude = jsonObject.getDouble("latitude");
                double longitude = jsonObject.getDouble("longitude");
                coordinates.add(new LatLng(latitude, longitude));
            }
            return coordinates;

            // Placeholder implementation (replace with your actual implementation)
        } catch (JSONException e) {
            Log.e("History", "Error parsing JSON data: " + e.getMessage());
            return null;
        }
    }

    private void drawPolyline(List<LatLng> coordinates) {
        List<LatLng> points = new ArrayList<>();
        points.addAll(coordinates);

        mapboxMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(Color.BLACK)
                .width(5f));
    }


    private void showOptionsPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, optionsButton);
        popupMenu.getMenuInflater().inflate(R.menu.options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            String filter = "6months"; // Default filter

            if (itemId == R.id.option_24_hours) {
                filter = "24hours";
            } else if (itemId == R.id.option_6_hours) {
                filter = "6hours";
            } else if (itemId == R.id.option_6_months) {
                filter = "6months";
            }

            // Fetch history route data and redraw polyline based on selected filter
            fetchHistoryAndDrawRoute(filter);

            return true;
        });
        popupMenu.show();
    }

    // Other lifecycle methods

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
        // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}
