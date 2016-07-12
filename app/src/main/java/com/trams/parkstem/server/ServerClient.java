package com.trams.parkstem.server;

import android.util.Log;

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
    private String uniqueID;

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


    //RES가 0이거나 exception이 발생하면 throw
    public class ServerErrorException extends Exception {

    }


    //회원가입 및 로그인 관련 함수
    public JSONObject login(final String parkstemID, final String parkstemPW) {
        String msg;
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
            Log.d("ServerClient",msg);
            uniqueID = result.getString("uniqueID");
            return result;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public boolean regbyemail(final String memberGubun, final String name, final String email, final String mobile, final String nickName, final String ID){
        String msg;
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

    public JSONObject memberDelete() throws ServerErrorException{
        String msg;
        final String DEL_URL = "http://app.parkstem.com/api/member_del.php\n";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                result = connect(hashMap, DEL_URL);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if(result.getInt("res")==1){
                msg = result.getString("msg");
                Log.d("ServerClient",msg);
                return result;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    //회원 정보관리 함수
    public class DashBoard{
        int res;
        String msg;
        String mycar;
        String mycard;
        ArrayList<Payment> data;
    }
    public class Payment{
        String card_name;
        String price;
        String pay_date;
    }

    public DashBoard dashboard() throws ServerErrorException{
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
            if(result.getInt("res")==1){
                DashBoard dashboard = new DashBoard();
                dashboard.res = result.getInt("res");
                dashboard.msg = result.getString("msg");
                Log.d("ServerClient",dashboard.msg);
                dashboard.mycar = result.getString("mycar");
                dashboard.mycard = result.getString("mycard");
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                Payment pm = new Payment();
                pm.card_name = jdata.getString("card_name");
                pm.pay_date = jdata.getString("pay_date");
                pm.price = jdata.getString("price");
                dashboard.data.add(pm);
                return dashboard;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public JSONObject hipassOn(final String hipass) throws ServerErrorException{
        String msg;
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
            if(result.getInt("res")==1){
                msg = result.getString("msg");
                Log.d("ServerClient",msg);
                uniqueID = result.getString("uniqueID");
                return result;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex){
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }


    //주차 현황 함수
    public class RecentCar{
        int res;
        String msg;
        String local_id;
        String in_date;
        String out_date;
        String total;
    }
    public RecentCar recentCar() throws ServerErrorException{
        final String Recent_URL = "http://app.parkstem.com/api/car_recent.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                result = connect(hashMap, Recent_URL);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if(result.getInt("res")==1){
                RecentCar recentcar = new RecentCar();
                recentcar.res = result.getInt("res");
                recentcar.msg = result.getString("msg");
                Log.d("ServerClient",recentcar.msg);
                recentcar.local_id = result.getString("local_id");
                recentcar.in_date = result.getString("in_date");
                recentcar.out_date = result.getString("out_date");
                recentcar.total = result.getString("total");
                return recentcar;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    //주차장정보 함수
    public class ParkInfo{
        int res;
        String msg;
        String local_id;
        String local_name;
        String local_content;
        String local_address;
        String local_phone;
        String local_photo;
        String free_time;
        String park_price;
        String park_price_time;
    }

    public ParkInfo parkInfo(final String local_id) throws ServerErrorException{
        final String Parkinfo_URL = "http://app.parkstem.com/api/car_recent.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("local_id",local_id);
                result = connect(hashMap, Parkinfo_URL);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if(result.getInt("res")==1){
                ParkInfo parkinfo = new ParkInfo();
                parkinfo.res = result.getInt("res");
                parkinfo.msg = result.getString("msg");
                Log.d("ServerClient",parkinfo.msg);
                parkinfo.local_id = result.getString("local_id");
                parkinfo.local_name = result.getString("local_name");
                parkinfo.local_content = result.getString("local_content");
                parkinfo.local_address = result.getString("local_address");
                parkinfo.local_phone = result.getString("local_phone");
                parkinfo.local_photo = result.getString("local_photo");
                parkinfo.free_time = result.getString("free_time");
                parkinfo.park_price = result.getString("park_price");
                parkinfo.park_price_time = result.getString("park_price_time");
                return parkinfo;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    //차량관리 함수
    public class CarRegister{
        int res;
        String msg;
        ArrayList<CarInfo> data;
    }
    public class CarInfo {
        int idx;
        String uniqueID;
        int sort;
        String mycar;
        String reg_date;
    }

    public CarRegister registerCar(final String mycar) throws ServerErrorException{
        final String DASH_URL = "http://app.parkstem.com/api/car_reg.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                hashMap.put("mycar", mycar);
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
            if(result.getInt("res")==1){
                CarRegister carregister = new CarRegister();
                carregister.res = result.getInt("res");
                carregister.msg = result.getString("msg");
                Log.d("ServerClient", carregister.msg);
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                CarInfo carInfo = new CarInfo();
                carInfo.idx = jdata.getInt("idx");
                carInfo.uniqueID = jdata.getString("uniqueID");
                carInfo.sort = jdata.getInt("sort");
                carInfo.mycar = jdata.getString("mycar");
                carInfo.reg_date = jdata.getString("reg_date");
                carregister.data.add(carInfo);
                return carregister;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }

    }

    public CarRegister listOfCar() throws ServerErrorException{
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
            if(result.getInt("res")==1){
                CarRegister carregister = new CarRegister();;
                carregister.res = result.getInt("res");
                carregister.msg = result.getString("msg");
                Log.d("ServerClient", carregister.msg);
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                CarInfo carInfo = new CarInfo();
                carInfo.idx = jdata.getInt("idx");
                carInfo.uniqueID = jdata.getString("uniqueID");
                carInfo.sort = jdata.getInt("sort");
                carInfo.mycar = jdata.getString("mycar");
                carInfo.reg_date = jdata.getString("reg_date");
                carregister.data.add(carInfo);
                return carregister;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public CarRegister priorityCar(final String index) throws ServerErrorException{
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
            if(result.getInt("res")==1){
                CarRegister carregister = new CarRegister();
                carregister.res = result.getInt("res");
                carregister.msg = result.getString("msg");
                Log.d("ServerClient", carregister.msg);
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                CarInfo carInfo = new CarInfo();
                carInfo.idx = jdata.getInt("idx");
                carInfo.uniqueID = jdata.getString("uniqueID");
                carInfo.sort = jdata.getInt("sort");
                carInfo.mycar = jdata.getString("mycar");
                carInfo.reg_date = jdata.getString("reg_date");
                carregister.data.add(carInfo);
                return carregister;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public JSONObject deleteCar(final String carnum) throws ServerErrorException{
        String msg;
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
            if(result.getInt("res")==1){
                msg = result.getString("msg");
                Log.d("ServerClient",msg);
                return result;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public class CarIn{
        int res;
        String msg;
        String indate;
        String local_id;
    }

    public CarIn carIn() throws ServerErrorException{
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
            if(result.getInt("res")==1){
                CarIn carin = new CarIn();
                carin.msg = result.getString("msg");
                Log.d("ServerClient",carin.msg);
                carin.res = result.getInt("res");
                carin.indate = result.getString("indate");
                carin.local_id = result.getString("local_id");
                return carin;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public class CarOut{
        int res;
        String msg;
        String outdate;
        String local_id;
    }
    public CarOut carOut() throws ServerErrorException{
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
            if(result.getInt("res")==1){
                CarOut carout = new CarOut();
                carout.msg = result.getString("msg");
                Log.d("ServerClient",carout.msg);
                carout.res = result.getInt("res");
                carout.outdate = result.getString("outdate");
                carout.local_id = result.getString("local_id");
                return carout;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    //카드 관련 함수
    public class CardList{
        int res;
        String msg;
        ArrayList<CardInfo>  data;
    }
    public class CardInfo{
        int idx;
        int sort;
        String card_name;
        String reg_date;
    }
    /**
    public CardInfo card_Register() throws ServerErrorException{
        //일단 보류
    }
     **/

    public CardList cardList() throws ServerErrorException{
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
            if(result.getInt("res")==1){
                CardList cardList = new CardList();
                cardList.res = result.getInt("res");
                cardList.msg = result.getString("msg");
                Log.d("ServerClient", cardList.msg);
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                CardInfo cardInfo = new CardInfo();
                cardInfo.idx = jdata.getInt("idx");
                cardInfo.sort = jdata.getInt("sort");
                cardInfo.card_name = jdata.getString("card_name");
                cardInfo.reg_date = jdata.getString("reg_date");
                cardList.data.add(cardInfo);
                return cardList;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public CardList cardSort(final String idx) throws ServerErrorException{
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
            if(result.getInt("res")==1){
                CardList cardList = new CardList();
                cardList.res = result.getInt("res");
                cardList.msg = result.getString("msg");
                Log.d("ServerClient", cardList.msg);
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                CardInfo cardInfo = new CardInfo();
                cardInfo.idx = jdata.getInt("idx");
                cardInfo.sort = jdata.getInt("sort");
                cardInfo.card_name = jdata.getString("card_name");
                cardInfo.reg_date = jdata.getString("reg_date");
                cardList.data.add(cardInfo);
                return cardList;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public JSONObject cardDelete(final String idx) throws ServerErrorException{
        String msg;
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
            if(result.getInt("res")==1){
                msg = result.getString("msg");
                Log.d("ServerClient",msg);
                return result;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public class PaymentList{
        int res;
        String msg;
        int itemTotalCount;
        int pageCount;
        ArrayList<Payment> data;
    }
    public PaymentList hipassPayment() throws ServerErrorException{
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
            if(result.getInt("res")==1){
                PaymentList paymentlist = new PaymentList();
                paymentlist.res = result.getInt("res");
                paymentlist.msg = result.getString("msg");
                Log.d("ServerClient", paymentlist.msg);
                paymentlist.itemTotalCount = result.getInt("itemTotalCount");
                paymentlist.pageCount = result.getInt("pageCount");
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                Payment payment = new Payment();
                payment.card_name = jdata.getString("card_name");
                payment.price = jdata.getString("price");
                payment.pay_date = jdata.getString("pay_date");
                paymentlist.data.add(payment);
                return paymentlist;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public class PaymentInfo{
        int res;
        String msg;
        int itemTotalCount;
        int pageCount;
        ArrayList<TicketBuyList> data;
    }
    public class TicketBuyList{
        String gubun;
        String local_id;
        String card_name;
        String price;
        String start_date;
        String end_date;
        String pay_date;
    }

    public PaymentInfo ticketpurchase() throws ServerErrorException{
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
            if(result.getInt("res")==1){
                PaymentInfo paymentinfo = new PaymentInfo();
                paymentinfo.res = result.getInt("res");
                paymentinfo.msg = result.getString("msg");
                Log.d("ServerClient", paymentinfo.msg);
                paymentinfo.itemTotalCount = result.getInt("itemTotalCount");
                paymentinfo.pageCount = result.getInt("pageCount");
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                TicketBuyList ticketBuyList = new TicketBuyList();
                ticketBuyList.gubun = jdata.getString("gubun");
                ticketBuyList.local_id = jdata.getString("local_id");
                ticketBuyList.card_name = jdata.getString("card_name");
                ticketBuyList.price = jdata.getString("price");
                ticketBuyList.start_date = jdata.getString("start_date");
                ticketBuyList.end_date = jdata.getString("end_date");
                ticketBuyList.pay_date = jdata.getString("pay_date");
                paymentinfo.data.add(ticketBuyList);
                return paymentinfo;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    //티켓 관련 함수
    public class TicketLists{
        int res;
        String msg;
        int itemTotalCount;
        int pageCount;
        ArrayList<Ticket> data;
    }
    public class Ticket{
        int idx;
        String local_id;
        String ticket_name;
        String term;
        String term_name;
        String gubun;
        String original_price;
        String price;
        String regdate;
    }

    public TicketLists listOfTicket() throws ServerErrorException{
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
            if(result.getInt("res")==1){
                TicketLists ticketLists = new TicketLists();
                ticketLists.res = result.getInt("res");
                ticketLists.msg = result.getString("msg");
                Log.d("ServerClient", ticketLists.msg);
                ticketLists.itemTotalCount = result.getInt("itemTotalCount");
                ticketLists.pageCount = result.getInt("pageCount");
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                Ticket ticket = new Ticket();
                ticket.idx = jdata.getInt("idx");
                ticket.local_id = jdata.getString("local_id");
                ticket.ticket_name = jdata.getString("ticket_name");
                ticket.term = jdata.getString("term");
                ticket.term_name = jdata.getString("term_name");
                ticket.gubun = jdata.getString("gubun");
                ticket.original_price = jdata.getString("original_price");
                ticket.price = jdata.getString("price");
                ticket.regdate = jdata.getString("regdate");
                ticketLists.data.add(ticket);
                return ticketLists;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public class LongTicketLists{
        int res;
        String msg;
        int itemTotalCount;
        int pageCount;
        ArrayList<Ticket> data;
    }
    public LongTicketLists listOfLongTicket() throws ServerErrorException{
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
            if(result.getInt("res")==1){
                LongTicketLists longTicketLists = new LongTicketLists();
                longTicketLists.res = result.getInt("res");
                longTicketLists.msg = result.getString("msg");
                Log.d("ServerClient", longTicketLists.msg);
                longTicketLists.itemTotalCount = result.getInt("itemTotalCount");
                longTicketLists.pageCount = result.getInt("pageCount");

                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                Ticket ticket = new Ticket();
                ticket.idx = jdata.getInt("idx");
                ticket.local_id = jdata.getString("local_id");
                ticket.ticket_name = jdata.getString("ticket_name");
                ticket.term = jdata.getString("term");
                ticket.term_name = jdata.getString("term_name");
                ticket.gubun = jdata.getString("gubun");
                ticket.original_price = jdata.getString("original_price");
                ticket.price = jdata.getString("price");
                ticket.regdate = jdata.getString("regdate");

                longTicketLists.data.add(ticket);
                return longTicketLists;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public class TicketInfo{
        int res;
        String msg;
        int idx;
        String local_id;
        String gubun;
        String price;
        String ticket_name;
        int card_use;
    }
    public TicketInfo ticketInfo(final String local_id, final String gubun, final String idx) throws ServerErrorException{
        final String T_INFO_URL = "http://app.parkstem.com/api/ticket_info.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                hashMap.put("local_id",local_id);
                hashMap.put("gubun",gubun);
                hashMap.put("idx",idx);
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
            if(result.getInt("res")==1){
                TicketInfo ticketInfo = new TicketInfo();
                ticketInfo.res = result.getInt("res");
                ticketInfo.msg = result.getString("msg");
                Log.d("ServerClient",ticketInfo.msg);
                ticketInfo.idx = result.getInt("idx");
                ticketInfo.local_id = result.getString("local_id");
                ticketInfo.gubun = result.getString("gubun");
                ticketInfo.price = result.getString("price");
                ticketInfo.ticket_name = result.getString("ticket_name");
                ticketInfo.card_use = result.getInt("card_use");
                return ticketInfo;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public JSONObject ticketInfoRegister(final String gubun, final String idx, final String user_name, final String user_phone, final String user_email, final String start_date, final String end_date, final String price) throws ServerErrorException{
        String msg;
        final String TIREG_URL = "http://app.parkstem.com/api/ticket_pay.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                hashMap.put("gubun",gubun);
                hashMap.put("idx",idx);
                hashMap.put("user_name",user_name);
                hashMap.put("user_phone",user_phone);
                hashMap.put("user_email",user_email);
                hashMap.put("start_date",start_date);
                hashMap.put("end_date",end_date);
                hashMap.put("price",price);
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
            if(result.getInt("res")==1){
                msg = result.getString("msg");
                Log.d("ServerClient",msg);
                return result;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    //모바일 인증

    //약관
    public String clause(final String idx) throws ServerErrorException{
        String msg;
        String contents;
        final String Clause_URL = "http://app.parkstem.com/api/clause.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("idx", idx);
                result = connect(hashMap, Clause_URL);
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if(result.getInt("res")==1){
                msg = result.getString("msg");
                Log.d("ServerClient", msg);
                contents = result.getString("contents");
                return contents;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }
}
