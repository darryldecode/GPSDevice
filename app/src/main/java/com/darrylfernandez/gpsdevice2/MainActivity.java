package com.darrylfernandez.gpsdevice2;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "GPSTracker";

    GPSTracker GpsTracker;
    SmsSender smsSender;
    Encrypter encrypter;

    EditText deviceToken;
    EditText deviceIdEditText;

    Button startTrackerBtn;
    Button stopTrackerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // labels
        TextView latView = (TextView) findViewById(R.id.latitudeTextView);
        TextView lngView = (TextView) findViewById(R.id.longitudeTextView);

        // inputs
        deviceToken = (EditText) findViewById(R.id.deviceTokenEditText);
        deviceIdEditText = (EditText) findViewById(R.id.deviceIdEditText);

        // button
        startTrackerBtn = (Button) findViewById(R.id.btnStartTracker);
        stopTrackerBtn = (Button) findViewById(R.id.btnStopTracking);

        encrypter = new Encrypter();
        smsSender = new SmsSender(deviceToken, deviceIdEditText, encrypter, getApplicationContext());
        GpsTracker = new GPSTracker(getApplicationContext(),smsSender,latView,lngView);
    }

    public void startTrackingClick(View view) {

        GpsTracker.getLocation();

        if( ! GpsTracker.canGetLocation() ) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Please turn on GPS or Network.")
                    .show();
            return;
        }

        startTrackerBtn.setEnabled(false);
        deviceToken.setEnabled(false);
        deviceIdEditText.setEnabled(false);
    }

    public void btnStopTrackingClick(View view) {
        GpsTracker.stopUsingGPS();
        startTrackerBtn.setEnabled(true);
        deviceToken.setEnabled(true);
        deviceIdEditText.setEnabled(true);
    }
}
