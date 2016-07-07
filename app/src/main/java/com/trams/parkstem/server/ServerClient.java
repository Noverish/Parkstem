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

    //idx, uniqueID, sort, mycar, reg_date
    static String[][] carinfo;
    static String indate;
    static String outdate;

    static String[][] cardlist;

    static String itemTotalCount;
    static String pageCount;

    //idx, local_id, ticket_name, term, term_name, gubun, original_price, price, regdate
    static String[][] ticketinfo;
    static String idx;
    static String gubun;
    static String price;
    static String ticket_name;
    static String card_use;


    public static ServerClient serverClient;
    public static ServerClient getInstance() {
        if(serverClient == null) {
            serverClient = new ServerClient();
        }
        return serverClient;
    }

    public ServerClient() {

    }

    private JSONObject result;

    private final String TAG = getClass().getSimpleName();

    //POST방식으로 JSON데이터를 보내는 함수

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


    //회원가입 및 로그인 관련 함수

    public boolean Login(final String parkstemID, final String parkstemPW) {

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


    public boolean Regbyemail(final String memberGubun, final String name, final String email, final String mobile, final String nickName, final String ID){
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
                else if(memberGubun.equals("fb")){
                    hashMap.put("facebookID", ID);
                }
                else if(memberGubun.equals("naver")){
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

    //회원 정보관리 함수
    public boolean Dashboard(){
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
            mycard = result.getString("mycard");
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

    public boolean HipassOn(final String hipass) {

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

    //주차 현황 함수

    public boolean PresentPark(){
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

    //차량관리 함수

    public boolean RegisterCar(final String carnumber){
        final String DASH_URL = "http://app.parkstem.com/api/car_reg.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                hashMap.put("mycar", carnumber);
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
            JSONArray jdata = result.getJSONArray("data");
            int size = jdata.length();
            for(int i=0;i<size;i++){
                carinfo = new String [size][5];
                carinfo[i][0] = jdata.getJSONObject(i).getString("idx");
                carinfo[i][1] = jdata.getJSONObject(i).getString("uniqueID");
                carinfo[i][2] = jdata.getJSONObject(i).getString("sort");
                carinfo[i][3] = jdata.getJSONObject(i).getString("mycar");
                carinfo[i][4] = jdata.getJSONObject(i).getString("reg_date");
            }
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean ListOfCar(){
        final String Clist_URL = "http://app.parkstem.com/api/car_list.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                result = connect(hashMap, Clist_URL);
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
            JSONArray jdata = result.getJSONArray("data");
            int size = jdata.length();
            for(int i=0;i<size;i++){
                carinfo = new String [size][5];
                carinfo[i][0] = jdata.getJSONObject(i).getString("idx");
                carinfo[i][1] = jdata.getJSONObject(i).getString("uniqueID");
                carinfo[i][2] = jdata.getJSONObject(i).getString("sort");
                carinfo[i][3] = jdata.getJSONObject(i).getString("mycar");
                carinfo[i][4] = jdata.getJSONObject(i).getString("reg_date");
            }
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean PriorityCar(final String index){
        final String Clist_URL = "http://app.parkstem.com/api/car_sort.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                hashMap.put("idx",index);
                result = connect(hashMap, Clist_URL);
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
            JSONArray jdata = result.getJSONArray("data");
            int size = jdata.length();
            for(int i=0;i<size;i++){
                carinfo = new String [size][5];
                carinfo[i][0] = jdata.getJSONObject(i).getString("idx");
                carinfo[i][1] = jdata.getJSONObject(i).getString("uniqueID");
                carinfo[i][2] = jdata.getJSONObject(i).getString("sort");
                carinfo[i][3] = jdata.getJSONObject(i).getString("mycar");
                carinfo[i][4] = jdata.getJSONObject(i).getString("reg_date");
            }
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean DeleteCar(final String carnum){
        final String DELETE_URL = "http://app.parkstem.com/api/car_del.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("mycar",carnum);
                result = connect(hashMap, DELETE_URL);
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
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean CarIn(){
        final String CARIN_URL = "http://app.parkstem.com/api/car_in.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                result = connect(hashMap, CARIN_URL);
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
            indate = result.getString("indate");
            local_id = result.getString("local_id");
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean CarOut(){
        final String CAROUT_URL = "http://app.parkstem.com/api/car_out.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                result = connect(hashMap, CAROUT_URL);
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
            outdate = result.getString("outdate");
            local_id = result.getString("local_id");
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //카드 관련 함수

    //티켓 관련 함수

    public boolean ListOfTicket() {
        final String Tlist_URL = "http://app.parkstem.com/api/ticket_list.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                result = connect(hashMap, Tlist_URL);
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
            itemTotalCount = result.getString("itemTotalCount");
            pageCount = result.getString("pageCount");
            JSONArray jdata = result.getJSONArray("data");
            int size = jdata.length();
            for (int i = 0; i < size; i++) {
                ticketinfo = new String[size][9];
                ticketinfo[i][0] = jdata.getJSONObject(i).getString("idx");
                ticketinfo[i][1] = jdata.getJSONObject(i).getString("local_id");
                ticketinfo[i][2] = jdata.getJSONObject(i).getString("ticket_name");
                ticketinfo[i][3] = jdata.getJSONObject(i).getString("term");
                ticketinfo[i][4] = jdata.getJSONObject(i).getString("term_name");
                ticketinfo[i][0] = jdata.getJSONObject(i).getString("gubun");
                ticketinfo[i][0] = jdata.getJSONObject(i).getString("original_price");
                ticketinfo[i][0] = jdata.getJSONObject(i).getString("price");
                ticketinfo[i][0] = jdata.getJSONObject(i).getString("regdate");
            }
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean ListOfLongTicket() {
        final String LTlist_URL = "http://app.parkstem.com/api/longticket_list.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                result = connect(hashMap, LTlist_URL);
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
            itemTotalCount = result.getString("itemTotalCount");
            pageCount = result.getString("pageCount");
            JSONArray jdata = result.getJSONArray("data");
            int size = jdata.length();
            for (int i = 0; i < size; i++) {
                ticketinfo = new String[size][9];
                ticketinfo[i][0] = jdata.getJSONObject(i).getString("idx");
                ticketinfo[i][1] = jdata.getJSONObject(i).getString("local_id");
                ticketinfo[i][2] = jdata.getJSONObject(i).getString("ticket_name");
                ticketinfo[i][3] = jdata.getJSONObject(i).getString("term");
                ticketinfo[i][4] = jdata.getJSONObject(i).getString("term_name");
                ticketinfo[i][0] = jdata.getJSONObject(i).getString("gubun");
                ticketinfo[i][0] = jdata.getJSONObject(i).getString("original_price");
                ticketinfo[i][0] = jdata.getJSONObject(i).getString("price");
                ticketinfo[i][0] = jdata.getJSONObject(i).getString("regdate");
            }
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean TicketInfo(final String parkid, final String caseinfo, final String index){
        final String T_INFO_URL = "http://app.parkstem.com/api/ticket_info.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                hashMap.put("local_id",parkid);
                hashMap.put("gubun",caseinfo);
                hashMap.put("idx",index);
                result = connect(hashMap, T_INFO_URL);
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
            idx = result.getString("idx");
            local_id = result.getString("local_id");
            gubun = result.getString("gubun");
            price = result.getString("price");
            ticket_name = result.getString("ticket_name");
            card_use = result.getString("card_use");
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean TicketInfoRegister(final String caseinfo, final String index, final String user_name, final String user_phone, final String user_email, final String start_date, final String end_date, final String Tprice){
        final String TIREG_URL = "http://app.parkstem.com/api/ticket_pay.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                hashMap.put("gubun",caseinfo);
                hashMap.put("idx",index);
                hashMap.put("user_name",user_name);
                hashMap.put("user_phone",user_phone);
                hashMap.put("user_email",user_email);
                hashMap.put("start_date",start_date);
                hashMap.put("end_date",end_date);
                hashMap.put("price",Tprice);
                result = connect(hashMap, TIREG_URL);
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
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
