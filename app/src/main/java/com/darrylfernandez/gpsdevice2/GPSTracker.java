package com.darrylfernandez.gpsdevice2;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class GPSTracker extends Service implements LocationListener {

    private static final String TAG = "GPSTracker";

    private final Context mCOntext;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;
    double speed;

    TextView latView;
    TextView lngView;

    SmsSender smsSender;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 2 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 20000; // 20 seconds

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context,SmsSender sender,  TextView oLatView, TextView oLngView) {
        mCOntext = context;
        latView = oLatView;
        lngView = oLngView;
        smsSender = sender;
    }

    public Location getLocation() {
        try {

            locationManager = (LocationManager) mCOntext.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if( !isGPSEnabled && !isNetworkEnabled ) {
                Toast.makeText(mCOntext, "No Location Provider Enabled.", Toast.LENGTH_LONG).show();
            } else {

                canGetLocation = true;

                if( isNetworkEnabled ) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this
                    );

                    if(locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if( location != null ) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if( isGPSEnabled ) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this
                    );

                    if(locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if( location != null ) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                latView.setText(String.valueOf(latitude));
                lngView.setText(String.valueOf(longitude));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    /**
     * get speed
     */
    public double getSpeed(){
        if(location != null){
            speed = location.getSpeed();
        }
        return speed;
    }

    /**
     * determine if location source is available
     *
     * @return bool
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        this.location = location;
        this.getLatitude();
        this.getLongitude();

        latView.setText(String.valueOf(latitude));
        lngView.setText(String.valueOf(longitude));

        smsSender.sendData(String.valueOf(latitude), String.valueOf(longitude), String.valueOf(speed));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        smsSender.sendNotifyProviderChanged(provider,"enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        smsSender.sendNotifyProviderChanged(provider,"disabled");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
