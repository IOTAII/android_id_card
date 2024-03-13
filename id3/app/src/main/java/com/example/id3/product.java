package com.example.id3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class  product extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Set the title of the action bar
        setTitle("Buy Product"); // Replace "Your New Title" with your desired title

        // Find the "Buy" button by its ID
        Button tracker = findViewById(R.id.button);
        Button cartracker = findViewById(R.id.carbutton);

        // Setup the toolbar and navigation drawer toggle
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.syncState();

        // Set a click listener on the "Buy" button
        tracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the BuyActivity
                Intent intent = new Intent(product.this, buy.class);
                startActivity(intent); // Start the BuyActivity
            }
        });

        cartracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the BuyActivity
                Intent intent = new Intent(product.this, buy.class);
                startActivity(intent); // Start the BuyActivity
            }
        });

        // Setup navigation item click listener
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_history) {
                    // Handle history item click
                    Intent historyIntent = new Intent(product.this, History.class);
                    startActivity(historyIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_product) {
                    // Handle product item click (already in product activity, so do nothing)
                    return true;
                } else if (item.getItemId() == R.id.nav_info) {
                    // Handle settings item click
                    Intent infoIntent = new Intent(product.this, info.class);
                    startActivity(infoIntent);
                    return true;
                } else if (item.getItemId() == R.id.nav_sub) {
                    // Handle settings item click
                    Intent subscriptionIntent = new Intent(product.this, sub.class);
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
    }

    // Other lifecycle methods

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
