package com.bluearc.dev.candyc;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;



public class Server implements Runnable {
    //final static String SpeakResource = "/speak";
    private boolean doStuff;
    private RemoteSpeach http_stuff;

    Server() {
        doStuff  = true;
    }

    private void Listen() throws IOException, InterruptedException {

        while (doStuff) {
            Thread.sleep(2000);
            new RemoteSpeach().execute("http://192.168.1.167:5000/api/get_next_speak_item");
        }
    }

    @Override
    public void run() {
        Log.e("server", "runpls");
        try {
            Listen();
        }
        catch (IOException e){
            Log.e("wellthen", "yes");
        }
        catch (InterruptedException e) {
            Log.e("bestbeoff", "yes");
        }
    }
}

