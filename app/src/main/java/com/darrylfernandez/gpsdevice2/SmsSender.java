package com.darrylfernandez.gpsdevice2;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Base64;
import android.widget.EditText;
import android.widget.Toast;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SmsSender {

    EditText deviceTokenEditText;
    EditText deviceIdEditText;
    String message;
    Context context;
    Encrypter encrypter;
    static final String SMS_GATEWAY = "09986366371";

    public SmsSender(EditText oDeviceToken, EditText oDeviceIdEditText, Encrypter encr, Context c) {
        deviceTokenEditText = oDeviceToken;
        deviceIdEditText = oDeviceIdEditText;
        context = c;
        encrypter = encr;
    }

    public void sendData(String lat, String lng, String speed) {

        String deviceToken = deviceTokenEditText.getText().toString();
        String deviceId = deviceIdEditText.getText().toString();
        String encrypted = "";

        if( deviceToken.matches("") || deviceId.matches("") ) {

            Toast.makeText(context, "A field is required. No SMS sent.", Toast.LENGTH_LONG).show();

        } else {

            message = "from:mobile|for:data|action:newCoordinates|deviceID:"+deviceId+"|deviceToken:"+deviceToken+"|lat:"+lat+"|lng:"+lng+"|speed:"+speed;

            // encrypt
            try {
                encrypted = encrypter.encrypt(deviceToken, message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(SMS_GATEWAY, null, encrypted, null, null);
                Toast.makeText(context, "SMS sent.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void sendNotifyProviderChanged(String provider, String changed) {

        String deviceToken = deviceTokenEditText.getText().toString();
        String deviceId = deviceIdEditText.getText().toString();
        String type = (changed.equals("enabled")) ? "gps-turned-on" : "gps-turned-off";
        String encrypted = "";

        if( deviceToken.matches("") || deviceId.matches("") ) {

            Toast.makeText(context, "A field is required. No SMS sent.", Toast.LENGTH_LONG).show();

        } else {

            // from: from mobile or GPS stand alone device?
            // for: what is this? for data coordinates or alerts?
            // type: what type of alert?
            // deviceID: the device ID
            // deviceToken: the device token
            // providerName: gps or network
            // providerStatus: disabled or enabled
            message = "from:mobile|for:alert|type:"+type+"|deviceID:"+deviceId+"|deviceToken:"+deviceToken+"|providerName:"+provider+"|providerStatus:" + changed;

            // encrypt
            try {
                encrypted = encrypter.encrypt(deviceToken, message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(SMS_GATEWAY, null, encrypted, null, null);
                Toast.makeText(context, "Alert SMS sent.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
