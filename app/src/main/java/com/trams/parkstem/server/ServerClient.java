package com.trams.parkstem.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by monc2 on 2016-07-04.
 */
public class ServerClient {
    static String uniqueID;
    static String msg;
    static String res;

    static String mycar;
    static String mycard;
    //card_name, price, pay_date

    static String local_id;
    static String in_date;
    static String out_date;
    static String total;

    //idx, uniqueID, sort, mycar, reg_date
    static String indate;
    static String outdate;

    static String itemTotalCount;
    static String pageCount;

    //idx, local_id, ticket_name, term, term_name, gubun, original_price, price, regdate
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
    public class Membership{
        String card_name;
        String price;
        String pay_date;
    }

    public ArrayList<Membership> dashboard(){
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
            ArrayList<Membership> AL = new ArrayList<Membership>();
            int size = jdata.length();
            for(int i=0;i<size;i++){
                Membership payment = new Membership();
                payment.card_name = jdata.getJSONObject(0).getString("card_name");
                payment.price = jdata.getJSONObject(0).getString("price");
                payment.pay_date = jdata.getJSONObject(0).getString("pay_date");
                AL.add(payment);
            }
            return AL;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean hipassOn(final String hipass) {

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
    public boolean presentPark(){
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
    public class CarNumber {
        String idx;
        String uniqueID;
        String sort;
        String mycar;
        String reg_date;
    }

    public class ServerErrorException extends Exception {

    }

    public boolean registerCar(final String carnumber) throws ServerErrorException{
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
            /**
            int size = jdata.length();
            for(int i=0;i<size;i++){
                carinfo = new String [size][5];
                carinfo[i][0] = jdata.getJSONObject(i).getString("idx");
                carinfo[i][1] = jdata.getJSONObject(i).getString("uniqueID");
                carinfo[i][2] = jdata.getJSONObject(i).getString("sort");
                carinfo[i][3] = jdata.getJSONObject(i).getString("mycar");
                carinfo[i][4] = jdata.getJSONObject(i).getString("reg_date");
            }
             **/
            CarNumber CN = new CarNumber();
            CN.idx = jdata.getJSONObject(0).getString("idx");
            CN.uniqueID = jdata.getJSONObject(0).getString("uniqueID");
            CN.sort = jdata.getJSONObject(0).getString("sort");
            CN.mycar = jdata.getJSONObject(0).getString("mycar");
            CN.reg_date = jdata.getJSONObject(0).getString("reg_date");
            return (result.getInt("res") == 1);
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }

    }

    public ArrayList<CarNumber> listOfCar(){
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
            ArrayList<CarNumber> AL = new ArrayList<CarNumber>();
            int size = jdata.length();
            for(int i=0;i<size;i++){
                CarNumber CN = new CarNumber();
                CN.idx = jdata.getJSONObject(0).getString("idx");
                CN.uniqueID = jdata.getJSONObject(0).getString("uniqueID");
                CN.sort = jdata.getJSONObject(0).getString("sort");
                CN.mycar = jdata.getJSONObject(0).getString("mycar");
                CN.reg_date = jdata.getJSONObject(0).getString("reg_date");
                AL.add(CN);
            }
            return AL;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<CarNumber> priorityCar(final String index){
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
            ArrayList<CarNumber> AL = new ArrayList<CarNumber>();
            int size = jdata.length();
            for(int i=0;i<size;i++){
                CarNumber CN = new CarNumber();
                CN.idx = jdata.getJSONObject(0).getString("idx");
                CN.uniqueID = jdata.getJSONObject(0).getString("uniqueID");
                CN.sort = jdata.getJSONObject(0).getString("sort");
                CN.mycar = jdata.getJSONObject(0).getString("mycar");
                CN.reg_date = jdata.getJSONObject(0).getString("reg_date");
                AL.add(CN);
            }
            return AL;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean deleteCar(final String carnum){
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

    public boolean carIn(){
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

    public boolean carOut(){
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
    public class CardList{
        String idx;
        String sort;
        String card_name;
        String reg_date;
    }
    public class PaymentList{
        String gubun;
        String local_id;
        String card_name;
        String price;
        String start_date;
        String end_date;
        String pay_date;
    }

    public boolean card_Register(){
        //일단 보류
        return true;
    }

    public ArrayList<CardList> cardList(){
        final String CL_URL = "http://app.parkstem.com/api/card_list.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                result = connect(hashMap, CL_URL);
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
            res = result.getString("res");
            JSONArray jdata = result.getJSONArray("data");
            ArrayList<CardList> cardlists = new ArrayList<CardList>();
            int size = jdata.length();
            for(int i=0;i<size;i++){
                CardList cardlist = new CardList();
                cardlist.idx = jdata.getJSONObject(0).getString("idx");
                cardlist.sort = jdata.getJSONObject(0).getString("sort");
                cardlist.card_name = jdata.getJSONObject(0).getString("card_name");
                cardlist.reg_date = jdata.getJSONObject(0).getString("reg_date");
                cardlists.add(cardlist);
            }
            return cardlists;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<CardList> cardPriority(final String idx){
        final String CS_URL = "http://app.parkstem.com/api/card_sort.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                hashMap.put("idx", idx);
                result = connect(hashMap, CS_URL);
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
            res = result.getString("res");
            JSONArray jdata = result.getJSONArray("data");
            ArrayList<CardList> cardlists = new ArrayList<CardList>();
            int size = jdata.length();
            for(int i=0;i<size;i++){
                CardList cardlist = new CardList();
                cardlist.idx = jdata.getJSONObject(0).getString("idx");
                cardlist.sort = jdata.getJSONObject(0).getString("sort");
                cardlist.card_name = jdata.getJSONObject(0).getString("card_name");
                cardlist.reg_date = jdata.getJSONObject(0).getString("reg_date");
                cardlists.add(cardlist);
            }
            return cardlists;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean cardDelete(final String idx){
        final String CD_URL = "http://app.parkstem.com/api/card_del.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                hashMap.put("idx",idx);
                result = connect(hashMap, CD_URL);
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

    public ArrayList<Membership> hipassPayment(){
        final String HiPay_URL = "http://app.parkstem.com/api/pay_list.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                result = connect(hashMap, HiPay_URL);
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
            ArrayList<Membership> memberships = new ArrayList<Membership>();
            int size = jdata.length();
            for(int i=0;i<size;i++){
                Membership membership = new Membership();
                membership.card_name = jdata.getJSONObject(0).getString("card_name");
                membership.price = jdata.getJSONObject(0).getString("price");
                membership.pay_date = jdata.getJSONObject(0).getString("pay_date");
                memberships.add(membership);
            }
            return memberships;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<PaymentList> ticketpurchase(){
        final String TicketBuy_URL = "http://app.parkstem.com/api/ticket_buy_list.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                result = connect(hashMap, TicketBuy_URL);
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
            ArrayList<PaymentList> paymentList = new ArrayList<PaymentList>();
            int size = jdata.length();
            for(int i=0;i<size;i++){
                PaymentList paymentLists = new PaymentList();
                paymentLists.gubun = jdata.getJSONObject(0).getString("gubun");
                paymentLists.local_id = jdata.getJSONObject(0).getString("local_id");
                paymentLists.card_name = jdata.getJSONObject(0).getString("card_name");
                paymentLists.price = jdata.getJSONObject(0).getString("price");
                paymentLists.start_date = jdata.getJSONObject(0).getString("start_date");
                paymentLists.end_date = jdata.getJSONObject(0).getString("end_date");
                paymentLists.pay_date = jdata.getJSONObject(0).getString("pay_date");
                paymentList.add(paymentLists);
            }
            return paymentList;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //티켓 관련 함수
    public class Ticket{
        String idx;
        String local_id;
        String ticket_name;
        String term;
        String term_name;
        String gubun;
        String original_price;
        String price;
        String regdate;
    }

    public ArrayList<Ticket> listOfTicket() {
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
            ArrayList<Ticket> ticketlists = new ArrayList<Ticket>();
            int size = jdata.length();
            for(int i=0;i<size;i++){
                Ticket ticket = new Ticket();
                ticket.idx = jdata.getJSONObject(0).getString("idx");
                ticket.local_id = jdata.getJSONObject(0).getString("local_id");
                ticket.ticket_name = jdata.getJSONObject(0).getString("ticket_name");
                ticket.term = jdata.getJSONObject(0).getString("term");
                ticket.term_name = jdata.getJSONObject(0).getString("term_name");
                ticket.gubun = jdata.getJSONObject(0).getString("gubun");
                ticket.original_price = jdata.getJSONObject(0).getString("original_price");
                ticket.price = jdata.getJSONObject(0).getString("price");
                ticket.regdate = jdata.getJSONObject(0).getString("regdate");
                ticketlists.add(ticket);
            }
            return ticketlists;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<Ticket> listOfLongTicket() {
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
            ArrayList<Ticket> ticketlists = new ArrayList<Ticket>();
            int size = jdata.length();
            for(int i=0;i<size;i++){
                Ticket ticket = new Ticket();
                ticket.idx = jdata.getJSONObject(0).getString("idx");
                ticket.local_id = jdata.getJSONObject(0).getString("local_id");
                ticket.ticket_name = jdata.getJSONObject(0).getString("ticket_name");
                ticket.term = jdata.getJSONObject(0).getString("term");
                ticket.term_name = jdata.getJSONObject(0).getString("term_name");
                ticket.gubun = jdata.getJSONObject(0).getString("gubun");
                ticket.original_price = jdata.getJSONObject(0).getString("original_price");
                ticket.price = jdata.getJSONObject(0).getString("price");
                ticket.regdate = jdata.getJSONObject(0).getString("regdate");
                ticketlists.add(ticket);
            }
            return ticketlists;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean ticketInfo(final String parkid, final String caseinfo, final String index){
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

    public boolean ticketInfoRegister(final String caseinfo, final String index, final String user_name, final String user_phone, final String user_email, final String start_date, final String end_date, final String Tprice){
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
