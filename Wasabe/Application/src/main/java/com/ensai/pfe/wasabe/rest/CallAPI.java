package com.ensai.pfe.wasabe.rest;

import android.os.AsyncTask;
import android.widget.TextView;

import com.ensai.pfe.wasabe.util.DeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nicolas on 08/02/15.
 */
public class CallAPI extends AsyncTask<String, String, DeviceInfo> {


    private TextView statusTextView;


    public CallAPI(TextView statusTextView){
        this.statusTextView = statusTextView;
    }



    protected void onPreExecute() {
        // Change a string on activity
        statusTextView.setText("Requete en cours d'envoi ..");
    }


    @Override
    protected DeviceInfo doInBackground(String... params) {

        String urlString = params[0]; // URL to call
        String resultToDisplay = "";
        InputStream in = null;
        JSONObject res = null;
        DeviceInfo di = null;

        // HTTP Get
        try {
            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());

        } catch (Exception e) {

            System.out.println(e.getMessage());
            di = new DeviceInfo("Exception");

        }

        try {
            res = new JSONObject(in.toString());

            // Extraction de l'information
            double temps = res.getDouble("temps");
            double longitude = res.getDouble("longitude");
            double latitude = res.getDouble("latitude");
            double precision = res.getDouble("precision");
            double id = res.getDouble("id");
            double destination = res.getDouble("Destination");

            di = new DeviceInfo(temps, longitude, latitude, precision, id, destination);
        } catch (JSONException e) {
            System.out.println("JSON Exception" + e.getMessage());
        }

        return di;
    }

    protected void onPostExecute(String result) {
        statusTextView.setText("Requete reçue");

    }

} // end CallAPI

