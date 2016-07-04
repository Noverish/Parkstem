package com.trams.parkstem.server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by monc2 on 2016-07-04.
 */
public class ServerClient {
    public static ServerClient serverClient;
    public static ServerClient getInstance() {
        if(serverClient == null) {
            serverClient = new ServerClient();
        }
        return serverClient;
    }

    private ServerClient() {

    }

    private JSONObject result;

    private final String TAG = getClass().getSimpleName();

    public static final String LOGIN_URL = "http://app.parkstem.com/api/member_login.php";


    public boolean login(final String parkstemID, final String parkstemPW) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("parkstemID", parkstemID);
                hashMap.put("parkstemPW", parkstemPW);
                result = connect(hashMap);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public boolean regid(final String memberGubun, final String name, final String emial, final String mobile, final String nickName, final String kakaoID, final String facebookID, final String naverID, final String parkstemID, final String parkstemPW, final String regDate){
        return true;
    }

    private JSONObject connect(HashMap<String, String> hashMap) {
        try {
            String jsonStr;
            URL url = new URL(LOGIN_URL);HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setConnectTimeout(10000);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            con.setRequestMethod("POST");

            JSONObject json = new JSONObject();
            for(String key : hashMap.keySet()) {
                json.put(key, hashMap.get(key));
            }

            OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            wr.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            jsonStr = "";

            while ((line = reader.readLine()) != null) {
                jsonStr += line + "\n";
            }
            reader.close();

            return new JSONObject(jsonStr);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }
}
