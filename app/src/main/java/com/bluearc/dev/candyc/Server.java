package com.bluearc.dev.candyc;


import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

public class Server implements Runnable {
    final static String SpeakResource = "/speak";
    private boolean doStuff;

    Server() {
        doStuff  = true;
    }

    private final int port = 2648;

    private void Listen() throws IOException {
        ServerSocket server = new ServerSocket(port);
        //server.setSoTimeout(5000);

        while (doStuff) {
            Log.d("server", "waiting");
            Socket socket = server.accept();
            Log.d("server", "connected");

            ServerConnection conn = new ServerConnection(socket);
            conn.Serve();
            Log.d("server", "close");
            socket.close();
        }

        server.close();
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
    }
}
