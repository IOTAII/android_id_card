package com.example.id3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class info extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView servicesOfferedTextView;
    private TextView batteryPercentageTextView;
    private TextView deviceIdTextView;
    private TextView dateOfPurchaseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Set the title of the action bar
        setTitle("Device Information");

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
                    Intent historyIntent = new Intent(info.this, History.class);
                    startActivity(historyIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_product) {
                    // Handle product item click
                    Intent productIntent = new Intent(info.this, product.class);
                    startActivity(productIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_info) {
                    // Handle settings item click
                    Intent infoIntent = new Intent(info.this, info.class);
                    startActivity(infoIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_sub) {
                    // Handle settings item click
                    Intent subscriptionIntent = new Intent(info.this, sub.class);
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

        // Initialize TextViews to display device information
        servicesOfferedTextView = findViewById(R.id.services_offered);
        batteryPercentageTextView = findViewById(R.id.battery_percentage);
        deviceIdTextView = findViewById(R.id.device_id);
        dateOfPurchaseTextView = findViewById(R.id.date_of_purchase);

        // Fetch device information and display it
        String userId = getIntent().getStringExtra("userId");
        String deviceId = getIntent().getStringExtra("deviceId");
        fetchDeviceInfo(userId, deviceId);
    }

    // Method to fetch device information from the server
    @SuppressLint("StaticFieldLeak")
    private void fetchDeviceInfo(String userId, String deviceId) {
        // Construct the URL with the received userId and deviceId
        String apiUrl = "http://3.109.34.34:8080/device-info/" + userId + "/" + deviceId;

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
            protected void onPostExecute(String deviceInfoJson) {
                if (deviceInfoJson != null) {
                    try {
                        JSONObject deviceInfo = new JSONObject(deviceInfoJson);
                        // Display device information in the TextViews
                        servicesOfferedTextView.setText(deviceInfo.getString("services_offered"));
                        batteryPercentageTextView.setText(String.valueOf(deviceInfo.getInt("battery_percentage")));
                        deviceIdTextView.setText(deviceInfo.getString("device_id"));
                        dateOfPurchaseTextView.setText(deviceInfo.getString("date_of_purchase"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(info.this, "Error fetching device information", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(apiUrl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}
