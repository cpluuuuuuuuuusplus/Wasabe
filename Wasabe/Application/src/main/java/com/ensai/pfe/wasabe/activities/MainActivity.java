/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ensai.pfe.wasabe.activities;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ensai.pfe.wasabe.logger.Log;
import com.ensai.pfe.wasabe.rest.CallAPI;
import com.ensai.pfe.wasabe.util.DeviceInfo;
import com.ensai.pfe.wasabe.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Sample application demonstrating how to test whether a device is connected,
 * and if so, whether the connection happens to be wifi or mobile (it could be
 * something else).
 * <p/>
 * This sample uses the logging framework to display log output in the log
 * fragment (LogFragment).
 */
public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "Wasabe_main";


    // Location related
    public LocationManager locationManager;
    private Double latitude;
    private Double longitude;
    private Double precision;


    private DeviceInfo di;



    // Device identifier, filled in by getDeviceIdentifier()
    private Double deviceID = -1.0;

    // The base of the URL to which the device should send information
    private String baseURL = "http://62.147.137.127:7070/WasabeServer/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Population de la Spinner des echangeurs
        Spinner spinner = (Spinner) findViewById(R.id.ou_aller);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sorties_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // Tell the spinner that is is this class that should react when something is selected
        spinner.setOnItemSelectedListener(this);


        checkNetworkConnection();



        // Create DeviceInfo

        createDeviceInfo();

        // send DeviceInfo via REST

        sendDeviceInfo();

        // Get DeviceInfo's ID and attribute it to the device


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position,
                               long id) {
        // L'utilisateur a sélectionné une porte
        Log.i(TAG, "L'utilisateur a sélectionné la porte qui se trouve en position " + position);
        System.out.println(TAG + "Position dans la Spinner de l'item sélectionné : " + position);
        System.out.println(TAG + "Localisation détectée : " + latitude + "  " + longitude);


        // Récuperer la porte selectionnée
        // dans position


        // construire une requete GET à partir de cette porte
        createDeviceInfo();
        sendDeviceInfo();


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // L'utilisateur s'est débrouillé pour ne rien sélectionner
        // Tres difficile; on ne devrait pas rentrer dans cette méthode
        Log.e(TAG, "L'utilisateur a sélectionné 'aucune porte'; il ne devrait pas avoir cette option là ! ");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     * Creates a deviceInfo from the data currently available
     *
     * @return current DeviceInfo
     */
    private void createDeviceInfo() {
        locateDevice();
        Double unixtimestamp = (double) System.currentTimeMillis()/1000;
        di = new DeviceInfo(unixtimestamp, latitude, longitude, precision, 0.0, 0.0);
    }


    /**
     * Envoi de DeviceInfo (contient l'appel asynchrone)
     */
    private void sendDeviceInfo() {

        JSONObject jso = createJSONfromDeviceInfo(di);

        String urlString = baseURL + "Whatever";

        TextView progress = (TextView) findViewById(R.id.resultat);

        new CallAPI(progress).execute(urlString);


    }


    /**
     *
     * Creates a JSON object from a DeviceInfo
     *
     * @param di deviceinfo
     * @return JSONObject
     */
    private JSONObject createJSONfromDeviceInfo(DeviceInfo di){
        JSONObject res = new JSONObject();

        try {
            res.put("temps", di.getTemps());
            res.put("longitude", di.getLongitude());
            res.put("latitude", di.getLatitude());
            res.put("precision", di.getPrecision());
            res.put("id", di.getId());
            res.put("destination", di.getDestination());

        }catch(JSONException je){
            System.out.println(TAG + " Exception JSON while creating JSONized DeviceInfo");
            je.printStackTrace();
        }
        return res;
    }

    /**
     * Check whether the device is connected, and if so, whether the connection
     * is wifi or mobile (it could be something else).
     */
    private void checkNetworkConnection() {
        // BEGIN_INCLUDE(connect)
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            Log.i(TAG, "Connecté à Internet");
        } else {
            Log.i(TAG, "Pas connecté à internet");
        }
        // END_INCLUDE(connect)
    }


    /**
     * Localiser le device
     */
    private void locateDevice() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location lastLocation = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null) {
            updateLoc(lastLocation);
        } else {
            lastLocation = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastLocation != null) {
                updateLoc(lastLocation);
            } else {
                latitude = -1000.0;
                longitude = -1000.0;
                precision = -1000.0;
            }
        }
    }

    private void updateLoc(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        precision = Double.parseDouble(location.getAccuracy() + "");
    }


}
