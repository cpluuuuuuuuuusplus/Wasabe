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
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.android.basicnetworking.R;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;

/**
 * Sample application demonstrating how to test whether a device is connected,
 * and if so, whether the connection happens to be wifi or mobile (it could be
 * something else).
 *
 * This sample uses the logging framework to display log output in the log
 * fragment (LogFragment).
 */
public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener{

    public static final String TAG = "Wasabe_main";


    // Location related
    public LocationManager locationManager;
    private Double latitude;
    private Double longitude;



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



        checkNetworkConnection();
        locateDevice();

    }








    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position,
    long id) {
        // L'utilisateur a sélectionné une porte
        Log.i(TAG, "L'utilisateur a sélectionné la porte qui se trouve en position " + position);

        // Récuperer la porte selectionnée


       // construire une requete GET à partir de cette porte



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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.valider_location:

                // get le contenu de la spinner
                Spinner spinner = (Spinner) findViewById(R.id.ou_aller);
                spinner.getPrompt();

                checkNetworkConnection();
                return true;
            // Clear the log view fragment.
            case R.id.clear_action:
                return true;
        }
        return false;
    }

    /**
     * La méthode Qui enverra la localisation + temps + etc
     *
     */
    private void sendLocationInfo(Location loc){

    }


    /**
     *
     *
     */

    private void getDeviceIdentifier(){



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
          Log.i(TAG, getString(R.string.no_wifi_or_mobile));
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
                latitude = 48.033333;
                longitude = -1.750000;
            }
        }
    }

    private void updateLoc(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }


}
