package com.trams.parkstem.server;

import org.json.JSONArray;
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
    static String uniqueID;
    static String msg;

    static String mycar;
    static String mycard;
    //card_name, price, pay_date
    static String[][] paymentinfo;

    static String local_id;
    static String in_date;
    static String out_date;
    static String total;

    static String[][] carinfo;
    static String indate;
    static String outdate;

    static String[][] cardlist;

    static String itemTotalCount;
    static String pageCount;


    static String[][] datinfo;


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



    private JSONObject connect(HashMap<String, String> hashMap, String urlStr) {
        try {
            String jsonStr;
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

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

    public boolean login(final String parkstemID, final String parkstemPW) {

        final String LOGIN_URL = "http://app.parkstem.com/api/member_login.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("parkstemID", parkstemID);
                hashMap.put("parkstemPW", parkstemPW);
                result = connect(hashMap, LOGIN_URL);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            msg = result.getString("msg");
            uniqueID = result.getString("uniqueID");
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public boolean regbyemail(final String memberGubun, final String name, final String email, final String mobile, final String nickName, final String ID){
        final String JOIN_URL = "http://app.parkstem.com/api/member_join.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("memberGubun",memberGubun);
                hashMap.put("name", name);
                hashMap.put("email", email);
                hashMap.put("mobile", mobile);
                hashMap.put("nickName", nickName);
                if(memberGubun=="kakao"){
                    hashMap.put("kakaoID", ID);
                }
                else if(memberGubun=="fb"){
                    hashMap.put("facebookID", ID);
                }
                else if(memberGubun=="naver"){
                    hashMap.put("naverID", ID);
                }
                hashMap.put("parkstemID", ID);
                result = connect(hashMap, JOIN_URL);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            msg = result.getString("msg");
            uniqueID = result.getString("uniqueID");
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean dashboard(){
        final String DASH_URL = "http://app.parkstem.com/api/dashboard.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                result = connect(hashMap, DASH_URL);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            msg = result.getString("msg");
            mycar = result.getString("mycar");
            JSONArray jdata = result.getJSONArray("data");
            int size = jdata.length();
            for(int i=0;i<size;i++){
                paymentinfo = new String [size][3];
                paymentinfo[i][0] = jdata.getJSONObject(i).getString("card_name");
                paymentinfo[i][1] = jdata.getJSONObject(i).getString("price");
                paymentinfo[i][2] = jdata.getJSONObject(i).getString("pay_date");
            }
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean hipass(final String hipass) {

        final String LOGIN_URL = "http://app.parkstem.com/api/hipass.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                hashMap.put("hipass", hipass);
                result = connect(hashMap, LOGIN_URL);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            msg = result.getString("msg");
            uniqueID = result.getString("uniqueID");
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean presentpark(){
        final String DASH_URL = "http://app.parkstem.com/api/car_recent.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                result = connect(hashMap, DASH_URL);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            msg = result.getString("msg");
            local_id = result.getString("local_id");
            in_date = result.getString("in_date");
            out_date = result.getString("out_date");
            total = result.getString("total");
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean regcar(final String mycar2){
        final String DASH_URL = "http://app.parkstem.com/api/car_reg.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                hashMap.put("mycar", mycar2);
                result = connect(hashMap, DASH_URL);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            msg = result.getString("msg");
            mycar = result.getString("mycar");
            JSONArray jdata = result.getJSONArray("data");
            int size = jdata.length();
            for(int i=0;i<size;i++){
                paymentinfo = new String [size][3];
                paymentinfo[i][0] = jdata.getJSONObject(i).getString("card_name");
                paymentinfo[i][1] = jdata.getJSONObject(i).getString("price");
                paymentinfo[i][2] = jdata.getJSONObject(i).getString("pay_date");
            }
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
