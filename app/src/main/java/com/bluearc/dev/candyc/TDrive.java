package com.bluearc.dev.candyc;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TDrive {

    TDrive() {
    }

    public static void Engage() {
        MainActivity.saySomethingStatic("Requesting T-drive");

        try {
            //byte[] ipAddr = new byte[]{127, 0, 0, 1};
            InetAddress addr = InetAddress.getByName("192.168.1.45");
            //InetAddress addr = InetAddress.getLocalHost();
            Socket socket = new Socket(addr, 2648);
            Log.d("main", "connected");
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            //out.println("POST request HTTP/1.1");
            //out.println("Content-type: application/json");
            //out.println("");
            out.println("{quarum:{plsQuarum: \"yes\",heartbeat:\"no\"}}");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                Log.d("client", line);
            }
            socket.close();
        }
        catch (IOException e) {

        }
    }

}
