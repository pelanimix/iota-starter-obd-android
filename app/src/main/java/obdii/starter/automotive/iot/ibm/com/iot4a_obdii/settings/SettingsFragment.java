/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the IBM License, a copy of which may be obtained at:
 * <p>
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AHKPKY&popup=n&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 * <p>
 * You may not use this file except in compliance with the license.
 */

package obdii.starter.automotive.iot.ibm.com.iot4a_obdii.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import obdii.starter.automotive.iot.ibm.com.iot4a_obdii.R;
import obdii.starter.automotive.iot.ibm.com.iot4a_obdii.obd.ObdBridge;

import static obdii.starter.automotive.iot.ibm.com.iot4a_obdii.ObdAppConstants.DEFAULT_FREQUENCY_SEC;
import static obdii.starter.automotive.iot.ibm.com.iot4a_obdii.ObdAppConstants.MAX_FREQUENCY_SEC;
import static obdii.starter.automotive.iot.ibm.com.iot4a_obdii.ObdAppConstants.MIN_FREQUENCY_SEC;

public class SettingsFragment extends PreferenceFragment {
    public static final String APP_SERVER_URL = "app_server_url";
    public static final String APP_SERVER_USERNAME = "app_server_username";
    public static final String APP_SERVER_PASSWORD = "app_server_password";
    public static final String PROTOCOL = "protocol"; // Protocol to send car probe data. HTTP/MQTT

    public static final String ENDPOINT = "endpoint"; // VDH: URL, IoTP: organization id
    public static final String VENDOR = "vendor"; // VDH: vendor, IoTP type id
    public static final String USERNAME = "username"; //VDH: uesr, IoTP: N/A
    public static final String PASSWORD = "password"; // VDH: password, IoTP: device token
    public static final String MO_ID = "mo_id"; // VDH: mo_id, IoTP: device id

    public static final String BLUETOOTH_DEVICE_ID = "bt_device_id";
    public static final String BLUETOOTH_DEVICE_NAME = "bt_device_name";
    public static final String BLUETOOTH_DEVICE_ADDRESS = "bt_device_address";
    public static final String OBD_TIMEOUT_MS = "obd_timeout_ms";
    public static final String OBD_PROTOCOL = "obd_protocol";
    public static final String UPLOAD_FREQUENCY = "upload_frequency";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        initializeSettings();
    }

    public void initializeSettings() {
        final Intent intent = getActivity().getIntent();
        prepareEditTextPreference(APP_SERVER_URL, intent.getStringExtra(APP_SERVER_URL), "", true, null);
        prepareEditTextPreference(APP_SERVER_USERNAME, intent.getStringExtra(APP_SERVER_USERNAME), "", true, null);
        prepareEditTextPreference(APP_SERVER_PASSWORD, intent.getStringExtra(APP_SERVER_PASSWORD), "", true, null);
        prepareEditTextPreference(BLUETOOTH_DEVICE_NAME, intent.getStringExtra(BLUETOOTH_DEVICE_NAME), "", false, null);
        prepareEditTextPreference(BLUETOOTH_DEVICE_ADDRESS, intent.getStringExtra(BLUETOOTH_DEVICE_ADDRESS), "", false, null);
        prepareEditTextPreference(UPLOAD_FREQUENCY, intent.getStringExtra(UPLOAD_FREQUENCY), "" + DEFAULT_FREQUENCY_SEC, true, new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final int intValue = Integer.parseInt(newValue.toString());
                if (MIN_FREQUENCY_SEC <= intValue && intValue <= MAX_FREQUENCY_SEC) {
                    preference.setSummary(newValue.toString());
                    return true;
                } else {
                    return false;
                }
            }
        });
        prepareEditTextPreference(OBD_TIMEOUT_MS, intent.getStringExtra(OBD_TIMEOUT_MS), "" + ObdBridge.DEFAULT_OBD_TIMEOUT_MS, true, new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final int intValue = Integer.parseInt(newValue.toString());
                if (ObdBridge.MIN_TIMEOUT_MS <= intValue && intValue <= ObdBridge.MAX_TIMEOUT_MS) {
                    preference.setSummary(newValue.toString());
                    return true;
                } else {
                    return false;
                }
            }
        });
        final CharSequence[] protocols = ObdBridge.getOBDProtocols();
        prepareListPreference(OBD_PROTOCOL, intent.getStringExtra(OBD_PROTOCOL), "" + ObdBridge.DEFAULT_OBD_PROTOCOL, protocols, protocols);
    }

    private void prepareEditTextPreference(final String prefKey, final String currentValue, final String defaultValue, final boolean enabled, Preference.OnPreferenceChangeListener changeListener) {
        final Preference preference = findPreference(prefKey);
        if (preference instanceof EditTextPreference) {
            final EditTextPreference editTextPref = (EditTextPreference) preference;
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final String storedValue = preferences.getString(prefKey, defaultValue);
            final String valueToSet = currentValue != null ? currentValue : storedValue;
            editTextPref.setText(valueToSet);
            editTextPref.setEnabled(enabled);

            editTextPref.setSummary(editTextPref.getText());
            if (changeListener == null) {
                changeListener = new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        preference.setSummary(newValue.toString());
                        return true;
                    }
                };
            }
            editTextPref.setOnPreferenceChangeListener(changeListener);
        }
    }

    private void prepareListPreference(final String prefKey, final String currentValue, final String defaultValue, final CharSequence[] entries, CharSequence[] entryValues) {
        final Preference preference = findPreference(prefKey);
        if (preference instanceof ListPreference) {
            final ListPreference listPref = (ListPreference) preference;
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final String storedValue = preferences.getString(prefKey, defaultValue);
            final String valueToSet = currentValue != null ? currentValue : storedValue;
            listPref.setEntries(entries);
            listPref.setEntryValues(entryValues);
            listPref.setValue(valueToSet);
            listPref.setSummary(listPref.getValue());
            listPref.setEnabled(true);

            final Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    return true;
                }
            };
            listPref.setOnPreferenceChangeListener(changeListener);
        }
    }

    private void setEditTextPreference(final String prefKey, final String value, final boolean enabled) {
        final Preference preference = findPreference(prefKey);
        if (preference instanceof EditTextPreference) {
            final EditTextPreference editTextPref = (EditTextPreference) preference;
            editTextPref.setEnabled(enabled);
            editTextPref.setText(value);
            editTextPref.setSummary(editTextPref.getText());
            editTextPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    return true;
                }
            });
        }
    }

    void setPreferenceValue(final String prefKey, final String value) {
        final Preference preference = findPreference(prefKey);
        if (preference instanceof EditTextPreference) {
            ((EditTextPreference) preference).setText(value);
        }
    }

    String getPreferenceValue(final String prefKey) {
        final Preference preference = findPreference(prefKey);
        if (preference instanceof EditTextPreference) {
            return ((EditTextPreference) preference).getText();
        } else {
            return null;
        }
    }
}
