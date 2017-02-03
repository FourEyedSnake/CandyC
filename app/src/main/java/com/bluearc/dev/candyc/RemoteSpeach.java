package com.bluearc.dev.candyc;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chrose on 03/02/2017.
 */

public class RemoteSpeach extends AsyncTask<String , Void ,String> {
    String server_response;

    @Override
    protected String doInBackground(String... strings) {

        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                server_response = readStream(urlConnection.getInputStream());
                Log.v("CatalogClient", server_response);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.e("Response", "" + server_response);
        if (!(server_response == null)) {
            parseBody(server_response);
        }
    }

// Converting InputStream to String

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private boolean parseBody(String line) {

        String theMessage = "";
        Double thePitch = 0.5;
        Double theSpeed = 0.5;
        String theLanguage = "UK";
        if (line.contains("error")){
            return true;
        }
        try {
            JSONObject json = new JSONObject(line);
            if (!json.isNull("error")){
                Log.d("RemoteSpeach", "Nothing to say");
                return true;
            }
            if (json.getJSONObject("message").has("name")) {
                theMessage =json.getJSONObject("message").getString("name");
            }
            if (json.getJSONObject("message").has("pitch")) {
                thePitch =json.getJSONObject("message").getDouble("pitch");
            }
            if (json.getJSONObject("message").has("speed")) {
                theSpeed =json.getJSONObject("message").getDouble("speed");
            }
            if (json.getJSONObject("message").has("lang")) {
                theLanguage =json.getJSONObject("message").getString("lang");
            }

            Log.d("ServerConnection", theMessage);
            MainActivity.saySomethingStatic(theMessage, thePitch.floatValue(), theSpeed.floatValue(), theLanguage);

        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d("Jsonfound", theMessage);
            Log.d("Jsonfound", thePitch.toString());
            Log.d("Jsonfound", theSpeed.toString());
            Log.d("Jsonfound", theLanguage);
            return false;
        }
        catch (ClassCastException e) {
            Log.d("RemoteSpeach", "Nothing to say");
        }

        return true;
    }
}
