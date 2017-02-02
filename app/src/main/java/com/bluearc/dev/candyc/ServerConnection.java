package com.bluearc.dev.candyc;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by Greg on 13/10/2016.
 */

public class ServerConnection {
    final private static String CRLF = "\r\n";
    final private static String continueMessage = "100 Continue" + CRLF;
    final private static String ok = "200 OK" + CRLF;
    final private static String badRequest = "400 Bad Request" + CRLF;
    final private static String notFound = "404 Not Found" + CRLF;
    final private static String badMethod = "405 Method Not Allowed" + CRLF;

    // private Socket socket;
    // private BufferedReader in;
    private DataInputStream in;
    private OutputStream out;

    private String method;
    private String resource;
    boolean jsonSeen;

    ServerConnection(Socket socket)
    {
        try {
            // in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            in = new DataInputStream(socket.getInputStream());
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonSeen = false;
    }

    void Serve() throws IOException {

        String line;
        int count = 0;
        while ((line = in.readLine()) != null) {
            Log.d("connection", line);
            if (line.equals(CRLF)) {
                Log.d("connection", "crlf");
                break;
            }
            else if (line.isEmpty()) {
                Log.d("connection", "empty");
                break;
            }
            if (count == 0) {
                if (!parseFirstLine(line)) {
                    return;
                }
            }
            else if (!parseHeader(line)) {
                return;
            }

            count++;
        }

        if (method.equals("POST")) {

            writeResponse(continueMessage);

            //in.reset();

            Log.d("connection", "wait for body");
            line = in.readLine();
            //line = getBody();
            if (!parseBody(line)) {
                return;
            }

            /*StringBuilder buffer = new StringBuilder();
            String body;
            while ((body = in.readLine()) != null) {
                buffer.append(body);
            }
            String data = buffer.toString();

            if (!parseBody(data)) {
                return;
            }*/
        }


        writeResponse(ok);
    }

    private boolean parseFirstLine(String line) {
        Log.d("ServerConnection", "parseFirstLine");
        StringTokenizer st = new StringTokenizer(line);

        if (st.countTokens() != 3) {
            // POST /resource HTTP/1.1
            writeResponse(badRequest);
            return false;
        }
        method = st.nextToken();
        resource = st.nextToken();
        // Don't bother with HTTP ver

        if (!method.equals("POST")) {
            //writeResponse(badMethod);
            //return false;
        }
        if (!resource.equals(Server.SpeakResource)) {
            writeResponse(notFound);
            return false;
        }

        return true;
    }

    private boolean parseHeader(String line) {
        Log.d("ServerConnection", "parseHeader");
        StringTokenizer st = new StringTokenizer(line);

        if (st.countTokens() < 2) {
            // Name: blah
            writeResponse(badRequest);
            return false;
        }
        String header = st.nextToken();
        String value = st.nextToken();

        if (header.equals("Content-type:")) {
            if (!value.contains("json")) {
                writeResponse(badRequest);
                return false;
            }
            jsonSeen = true;
        }
        return true;
    }

    private boolean parseBody(String line) {
        Log.d("ServerConnection", "parseBody");
        Log.d("ServerConnection", line);

        String theMessage = "";
        Double thePitch = 0.5;
        Double theSpeed = 0.5;
        String theLanguage = "UK";

        try {
            JSONObject json = new JSONObject(line);
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
            Log.d("ServerConnection", theMessage);
            Log.d("ServerConnection", thePitch.toString());
            Log.d("ServerConnection", theSpeed.toString());
            Log.d("ServerConnection", theLanguage);
            writeResponse(badRequest);
            return false;
        }

        return true;
    }

    private void writeResponse(String message) {
        try {
            out.write(message.getBytes());
            // out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
