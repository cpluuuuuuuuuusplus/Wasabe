package com.ensai.pfe.wasabe.rest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.ensai.pfe.wasabe.R;
import com.ensai.pfe.wasabe.activities.MainActivity;
import com.ensai.pfe.wasabe.util.DeviceInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nicolas on 08/02/15.
 */
public class CallAPI extends AsyncTask<String, String, String> {


    public static final String TAG = "CallAPI";

    private TextView statusTextView;
    private Context mContext ;


    public CallAPI(Context ctx, TextView statusTextView){
        this.mContext = ctx;
        this.statusTextView = statusTextView;
    }



    protected void onPreExecute() {
        // Change a string on activity
        statusTextView.setText("Requete en cours d'envoi ..");
    }


    @Override
    protected String doInBackground(String... params) {

        String json = params[0]; // JSONized DeviceInfo


        // HTTP POST

            Log.i(TAG, "Entered AsyncTask");
            Log.i(TAG, "DeviceInfo :" + json);


            // Envoi de la requête
            HttpResponse httpResponse = sendDeviceInfo("http://62.147.137.127:7070/Wasabe/", json);

            //vérif
            System.out.println("Contenu de la HTTPResponse : "+httpResponse.toString());

            // On tente d'ouvrir le resultat -> InputStream
            InputStream resultIS = null;
            try {
                resultIS = httpResponse.getEntity().getContent();
            }catch(IOException e){
                System.err.println("IOException "+ e.getMessage());

            }catch(NullPointerException e){
                System.err.println("Il n'y a rien dans le InputStream ;"+ e.getMessage());
            }

            //vérif
            System.out.println("Contenu du InputStream : "+resultIS.toString());


            // On tente  une conversion en String
            String resultS = null;
            try{
                resultS = convertInputStreamToString(resultIS);
            }catch(IOException e){
                System.err.println("Impossible de convertir le InputStream en String ;"+ e.getMessage());

            }

            // vérif
            System.out.println("Contenu du String : "+resultS.toString());



            return resultS;


    }


    @Override
    protected void onPostExecute(String resultS) {

        DeviceInfo di = decodeDeviceInfo(resultS);

       // statusTextView.setText("Requete reçue (identifiant attribué : "+di.getId()+")");
        if(di != null && di.getId() != 0.0){
            statusTextView.setText("Requête reçue");

            // Update the MainActivity's DeviceInfo
            MainActivity.di.setId(di.getId());


        }else{
            statusTextView.setText("Erreur de communication avec le serveur");

        }


    }



    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    /**
     *
     * Cette classe effectue l'envoi des information sur le device (en JSON)
     * et renvoie une réponse http contenant, si tout va bien, un JSON
     * correspondant à la réponse du serveur
     *
     * @param url
     * @param jsonString
     * @return
     */
    private static HttpResponse sendDeviceInfo(String url, String jsonString){

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);


        // On tente de convertir le DeviceInfo JSONisé Stringizé en StringEntity
        StringEntity se = null;
        try {
            se = new StringEntity(jsonString);
        }catch (UnsupportedEncodingException e) {
            System.err.println("UnsupportedEncodingException ;"+ e.getMessage());
        }


        // Construction de la requete POST
        httpPost.setEntity(se);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        // On tente l'envoi de la requete au serveur et la réception du message
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpclient.execute(httpPost);
        }catch(ClientProtocolException e){
            System.err.println("ClientProtocolException ;"+ e.getMessage());
        }catch(IOException e){
            System.err.println("IOException ;"+ e.getMessage());

        }
        return httpResponse;
    }



    private static DeviceInfo decodeDeviceInfo(String resultS){
        // On tente de coercer le résultat en JSON
        JSONObject resultJ = null;
        // Si c'est bon on crée le DeviceInfo contenant l'identifiant à jour
        DeviceInfo di = null;

        try {
            resultJ = new JSONObject(resultS);

            // Extraction de l'information
            double temps = resultJ.getDouble("temps");
            double longitude = resultJ.getDouble("longitude");
            double latitude = resultJ.getDouble("latitude");
            double precision = resultJ.getDouble("precision");
            double id = resultJ.getDouble("id");
            String destination = resultJ.getString("destination");

            di = new DeviceInfo(temps, longitude, latitude, precision, id, destination);
        }catch(JSONException e){
            System.err.println("Le résultat du InputStream n'a pas pu être coercé en JSON ;"+ e.getMessage());

        }

        return di;
    }


} // end CallAPI
