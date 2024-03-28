package com.sp.stride;


import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.hardware.SensorEventListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;

import static android.os.SystemClock.sleep;

//public class Map extends FragmentActivity implements SensorEventListener {
public class Map extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;

    private double lat;
    private double lon;
    private String restaurantName;
    private double myLat;
    private double myLon;
    private LatLng RESTAURANT;
    private LatLng ME;
    //testing trash
    private SensorEvent event;
    String compassallowed = "Y";
    String markerallowed = "Y";


    private float[] mRotationMatrix = new float[16];
    private SensorManager mSensorManager;
    private Sensor mRotVectSensor;
    float mDeclination;
    double angle;
    GeomagneticField geoField;
    private Button compassbutton;
    private Button reset;
    private Button marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        lat = getIntent().getDoubleExtra("LATITUDE", 0);
        lon = getIntent().getDoubleExtra("LONGITUDE", 0);
        restaurantName = getIntent().getStringExtra("NAME");
        myLat = getIntent().getDoubleExtra("MYLATITUDE", 0);
        myLon = getIntent().getDoubleExtra("MYLONGITUDE", 0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // the 2 lines is what they want
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mRotVectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        compassbutton = (Button) findViewById(R.id.compass_button);
        compassbutton.setOnClickListener(compass);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(resetpos);

        marker = (Button) findViewById(R.id.marker);
        marker.setOnClickListener(addmarker);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //RESTAURANT = new LatLng(lat, lon);
        ME = new LatLng(myLat, myLon);
        RESTAURANT = mMap.getCameraPosition().target;

        //Marker restaurant = mMap.addMarker(new MarkerOptions().position(RESTAURANT).title(restaurantName));
        Marker me = mMap.addMarker(new MarkerOptions().position(ME).title("ME").snippet("My location").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_me)));

        // Move the camera instantly to restaurant with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ME, 15));


        //CODE FOR MOVING CAMERA
        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ME)     // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        */

    }

    //all edits start here
    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mRotVectSensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    //@Override
    public void onLocationChanged(Location location) {
        GeomagneticField field = new GeomagneticField(
                //(float)location.getLatitude(),
                //(float)location.getLongitude(),
                //(float)location.getAltitude(),
                Double.valueOf(location.getLatitude()).floatValue(),
                Double.valueOf(location.getLongitude()).floatValue(),
                Double.valueOf(location.getAltitude()).floatValue(),
                System.currentTimeMillis()
        );

        // getDeclination returns degrees
        mDeclination = field.getDeclination();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            SensorManager.getRotationMatrixFromVector(mRotationMatrix , event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            double Bearing = Math.toDegrees(orientation[0]) + mDeclination;
            float bearing = (float) Bearing;
            if(compassallowed != "N")
            {
                if(mMap != null) {
                    updateCamera(bearing);
                }

            }

            //angle = Math.toDegrees(orientation[0]);
            //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(ME, 15, 30, bearing)));
        }
    }

        private void updateCamera(float bearing) {
            CameraPosition oldPos = mMap.getCameraPosition();

            CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos),200,null);

        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ME)
                .zoom(15)
                .bearing(bearing)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

        }



        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // not in use
        }

    View.OnClickListener compass = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (compassallowed == "Y")
            {
                compassallowed = "N";
                float zoomlevel = mMap.getCameraPosition().zoom;
                //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(ME, zoomlevel, 30, 0)),200,null);
            }
            else if (compassallowed == "N")
            {
                compassallowed = "Y";
            }
        }
    };

    View.OnClickListener resetpos = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //HERE LIES THE IMPT CODE mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ME, 15));
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(ME, 15,30,0)),1000,null);
            compassallowed = "N";
            //CameraPosition oldPos = mMap.getCameraPosition();
            //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(oldPos),200,null);
        }
    };

    View.OnClickListener addmarker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (markerallowed == "Y") {
                markerallowed = "N";
                Marker marker = mMap.addMarker(new MarkerOptions().position(ME).title("ME").snippet("My location").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            }
            else if (markerallowed == "N")
            {
                markerallowed = "Y";
                mMap.clear();
                Marker me = mMap.addMarker(new MarkerOptions().position(ME).title("ME").snippet("My location").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_me)));
            }

        }
    };

    }
