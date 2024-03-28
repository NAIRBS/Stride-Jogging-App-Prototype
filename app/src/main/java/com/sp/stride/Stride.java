
package com.sp.stride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import javax.crypto.spec.GCMParameterSpec;

public class
Stride extends AppCompatActivity {

    //private TextView location = null;
    private GPSTracker gpsTracker;
    private double latitude = 0.0d;
    private double longitude = 0.0d;
    private double myLatitude = 0.0d;
    private double myLongitude = 0.0d;
    private Button showMap;
    private Button showlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gpsTracker = new GPSTracker(Stride.this);
        //location = (TextView) findViewById(R.id.location);
        showMap = (Button) findViewById(R.id.show_map);
        showMap.setOnClickListener(OnMap);
        showlocation = (Button) findViewById(R.id.location);
        showlocation.setOnClickListener(OnLocation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    View.OnClickListener OnMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Get my current location
            myLatitude = gpsTracker.getLatitude();
            myLongitude = gpsTracker.getLongitude();

            Intent intent = new Intent(Stride.this, Map.class);

            intent.putExtra("LATITUDE", latitude);
            intent.putExtra("LONGITUDE", longitude);
            intent.putExtra("MYLATITUDE", myLatitude);
            intent.putExtra("MYLONGITUDE", myLongitude);
            startActivity(intent);
            //finish(); this will close the entire activity entirely
        }
        };

    View.OnClickListener OnLocation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Get my current location
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                myLatitude = gpsTracker.getLatitude();
                myLongitude = gpsTracker.getLongitude();
                //showlocation.setText(String.valueOf(latitude) + ", " + String.valueOf(longitude));
                // \n is for new line
                Toast.makeText(getApplicationContext(), "Your Location is -\nLat: " + myLatitude + "\nLong: " + myLongitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location. GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTracker.showEnableLocationAlert();
            }
        }
    };


    }


