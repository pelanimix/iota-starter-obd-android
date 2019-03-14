/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package obdii.starter.automotive.iot.ibm.com.iot4a_obdii.qrcode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import obdii.starter.automotive.iot.ibm.com.iot4a_obdii.ObdAppIntents;
import obdii.starter.automotive.iot.ibm.com.iot4a_obdii.R;
import obdii.starter.automotive.iot.ibm.com.iot4a_obdii.settings.SettingsFragment;

public class SpecifyServer extends AppCompatActivity {
    public static SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_server);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Specify Server");
    }

    public void openScanner(final View view) {
        final IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void useDefaultServer(View view) {
        Log.i("Button Clicked", "Default Server");
        Intent intent = new Intent();
        intent.putExtra("useDefault", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void moreInfo(View view) {
        Log.i("Button Pressed", "More Info");

        final Uri webpage = Uri.parse("http://www.ibm.com/internet-of-things/iot-industry/iot-automotive/");
        final Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult == null) {
            switch(requestCode){
                case ObdAppIntents.SETTINGS_INTENT:
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                default:
                    break;
            }
        }else{
            if (scanResult.getContents() != null) {
                Log.d("RESULT", scanResult.toString());

                final String[] fullString = scanResult.getContents().split(",");

                if(fullString.length < 2 || !fullString[0].equals("1")){
                    Log.e("RESULT", "Failed");
                    setResult(RESULT_CANCELED);
                }
                Intent result = new Intent();

                String appUrl = fullString[1];
                result.putExtra(SettingsFragment.APP_SERVER_URL, appUrl);
                setPreference(SettingsFragment.APP_SERVER_URL, appUrl);

                if(fullString.length >= 4){
                    String appUsername = fullString[2];
                    String appPassword = fullString[3];
                    result.putExtra(SettingsFragment.APP_SERVER_USERNAME, appUsername);
                    result.putExtra(SettingsFragment.APP_SERVER_PASSWORD, appPassword);
                    setPreference(SettingsFragment.APP_SERVER_USERNAME, appUsername);
                    setPreference(SettingsFragment.APP_SERVER_PASSWORD, appPassword);
                }else{
                    removePreference(SettingsFragment.APP_SERVER_USERNAME);
                    removePreference(SettingsFragment.APP_SERVER_PASSWORD);
                }

                final Toast toast = Toast.makeText(getApplicationContext(), "Changed were successfully applied!", Toast.LENGTH_SHORT);
                toast.show();

                setResult(RESULT_OK, result);
                finish();
            } else {
                Log.e("RESULT", "Failed");
            }
        }
    }

    void setPreference(final String prefKey, final String value) {
        try {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().putString(prefKey, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    String getPreference(final String prefKey, final String defaultValue) {
        try {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            return preferences.getString(prefKey, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
    void removePreference(final String prefKey){
        try{
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().remove(prefKey).apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}