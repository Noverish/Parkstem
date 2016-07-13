package com.trams.parkstem.server;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by monc2 on 2016-07-04.
 */
public class ServerClient {
    //코드정리
    //1.모든 함수가 JSON 반환하면 안됨.
    //1.exception에 res랑 msg를 넣어서 throw.
    //1.모든 클래스에 res랑 msg랑 uniqueID를 뺀다.
    //uniqueID를 반환하는 함수는 현재의 uniqueID와 같은지 체크하는 코드



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
    public JSONObject connect(HashMap<String, String> hashMap, String urlStr) {
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

    //POST방식으로 JSON데이터를 보내는 함수
    private JSONObject connect(HashMap<String, String> hashMap, String urlin, String urlout) {
        try {
            String jsonStr;
            URL requrl = new URL(urlin);
            HttpURLConnection req = (HttpURLConnection) requrl.openConnection();

            req.setConnectTimeout(10000);
            req.setDoOutput(true);
            req.setRequestProperty("Content-Type", "application/json");
            req.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            req.setRequestMethod("POST");

            URL resurl = new URL(urlout);
            HttpURLConnection res = (HttpURLConnection) resurl.openConnection();

            res.setConnectTimeout(10000);
            res.setDoInput(true);
            res.setRequestProperty("Content-Type", "application/json");
            res.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            res.setRequestMethod("POST");


            JSONObject json = new JSONObject();
            for(String key : hashMap.keySet()) {
                json.put(key, hashMap.get(key));
            }

            OutputStreamWriter wr= new OutputStreamWriter(req.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            wr.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(res.getInputStream()));

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
        public int res;
        public String msg;

        public ServerErrorException(){
            res = 0;
            msg = "JSON ERROR";
        }
        public ServerErrorException(int res, String msg) {
            this.res = res;
            this.msg = msg;
        }
    }


    //회원가입 및 로그인 관련 함수
    /**
    public static String getSHA256(String str) {
        String rtnSHA = "";

        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();

            for(int i = 0 ; i < byteData.length ; i++){
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }
            rtnSHA = sb.toString();

        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            rtnSHA = null;
        }
        return rtnSHA;
    }
**/

    public boolean login(final String parkstemID, final String parkstemPW) throws ServerErrorException{
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
            if(result.getInt("res")==1){
                Log.d("ServerClient",msg);
                uniqueID = result.getString("uniqueID");
                return true;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }


    public void regByEmail(final String name, final String email, final String mobile, final String nickName, final String parkstemID, final String parkstemPW) throws ServerErrorException{
        String msg;
        final String JOIN_URL = "http://app.parkstem.com/api/member_join.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strCurDate = CurDateFormat.format(date);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("memberGubun","parkstem");
                hashMap.put("name", name);
                hashMap.put("email", email);
                hashMap.put("mobile", mobile);
                hashMap.put("nickName", nickName);
                hashMap.put("parkstemID", parkstemID);
                hashMap.put("parkstemPW", parkstemPW);
                hashMap.put("regDate", strCurDate);
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
            if(result.getInt("res")==1){
                Log.d("ServerClient", msg);
                uniqueID = result.getString("uniqueID");
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public void regByKakao(final String name, final String email, final String mobile, final String nickName, final String kakaoID) throws ServerErrorException{
        String msg;
        final String JOIN_URL = "http://app.parkstem.com/api/member_join.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strCurDate = CurDateFormat.format(date);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("memberGubun","kakao");
                hashMap.put("name", name);
                hashMap.put("email", email);
                hashMap.put("mobile", mobile);
                hashMap.put("nickName", nickName);
                hashMap.put("kakaoID", kakaoID);
                hashMap.put("regDate", strCurDate);
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
            if(result.getInt("res")==1){
                Log.d("ServerClient", msg);
                uniqueID = result.getString("uniqueID");
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public void regByNaver(final String name, final String email, final String mobile, final String nickName, final String naverID) throws ServerErrorException{
        String msg;
        final String JOIN_URL = "http://app.parkstem.com/api/member_join.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strCurDate = CurDateFormat.format(date);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("memberGubun","naver");
                hashMap.put("name", name);
                hashMap.put("email", email);
                hashMap.put("mobile", mobile);
                hashMap.put("nickName", nickName);
                hashMap.put("naverID", naverID);
                hashMap.put("regDate", strCurDate);
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
            if(result.getInt("res")==1){
                Log.d("ServerClient", msg);
                uniqueID = result.getString("uniqueID");
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public void regByFacebook(final String name, final String email, final String mobile, final String nickName, final String facebookID) throws ServerErrorException{
        String msg;
        final String JOIN_URL = "http://app.parkstem.com/api/member_join.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strCurDate = CurDateFormat.format(date);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("memberGubun","facebook");
                hashMap.put("name", name);
                hashMap.put("email", email);
                hashMap.put("mobile", mobile);
                hashMap.put("nickName", nickName);
                hashMap.put("facebookID", facebookID);
                hashMap.put("regDate", strCurDate);
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
            if(result.getInt("res")==1){
                Log.d("ServerClient", msg);
                uniqueID = result.getString("uniqueID");
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public void memberDelete() throws ServerErrorException{
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
            msg = result.getString("msg");
            if(result.getInt("res")==1){
                Log.d("ServerClient", msg);
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    //회원 정보관리 함수
    public class DashBoard{
        String mycar;
        String mycard;
        ArrayList<Payment> data;
    }
    public class Payment{
        public String card_name;
        public int price;
        public Calendar calendar;
    }

    public DashBoard dashboard() throws ServerErrorException{
        String msg;
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
            if(result.getInt("res")==1){
                DashBoard dashboard = new DashBoard();
                Log.d("ServerClient",msg);
                dashboard.mycar = result.getString("mycar");
                dashboard.mycard = result.getString("mycard");
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                Payment pm = new Payment();
                pm.card_name = jdata.getString("card_name");
                //pm.pay_date = jdata.getString("pay_date");
                //pm.price = jdata.getString("price");
                dashboard.data.add(pm);
                return dashboard;
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public void hipassOn(final String hipass) throws ServerErrorException{
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
            msg = result.getString("msg");
            if(result.getInt("res")==1){
                Log.d("ServerClient",msg);
                uniqueID = result.getString("uniqueID");
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex){
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }


    //주차 현황 함수
    public class RecentCar{
        String local_id;
        String in_date;
        String out_date;
        String total;
    }
    public RecentCar recentCar() throws ServerErrorException{
        String msg;
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
            msg = result.getString("msg");
            if(result.getInt("res")==1){
                RecentCar recentcar = new RecentCar();
                Log.d("ServerClient",msg);
                recentcar.local_id = result.getString("local_id");
                recentcar.in_date = result.getString("in_date");
                recentcar.out_date = result.getString("out_date");
                recentcar.total = result.getString("total");
                return recentcar;
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    //주차장정보 함수
    public class ParkInfo{
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

    /**이 함수는 data형식으로 값을 받아온다
     * 확인 필요
     * **/
    public ParkInfo parkInfo(final String local_id) throws ServerErrorException{
        String msg;
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
            msg = result.getString("msg");
            if(result.getInt("res")==1){
                ParkInfo parkinfo = new ParkInfo();
                Log.d("ServerClient",msg);
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);

                parkinfo.local_id = jdata.getString("local_id");
                parkinfo.local_name = jdata.getString("local_name");
                parkinfo.local_content = jdata.getString("local_content");
                parkinfo.local_address = jdata.getString("local_address");
                parkinfo.local_phone = jdata.getString("local_phone");
                parkinfo.local_photo = jdata.getString("local_photo");
                parkinfo.free_time = jdata.getString("free_time");
                parkinfo.park_price = jdata.getString("park_price");
                parkinfo.park_price_time = jdata.getString("park_price_time");
                return parkinfo;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    //차량관리 함수
    public class CarLists{
        int itemTotalCount;
        int pageCount;
        ArrayList<CarInfo> data;
    }
    public class CarInfo {
        int idx;
        String uniqueID;
        int sort;
        String mycar;
        String reg_date;
    }

    public CarLists CarRegister(final String mycar) throws ServerErrorException{
        String msg;
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
            msg = result.getString("msg");
            if(result.getInt("res")==1){
                CarLists carLists = new CarLists();;
                Log.d("ServerClient", msg);
                carLists.itemTotalCount = result.getInt("itemTotalCount");
                carLists.pageCount = result.getInt("pageCount");
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                CarInfo carInfo = new CarInfo();
                carInfo.idx = jdata.getInt("idx");
                carInfo.uniqueID = jdata.getString("uniqueID");
                carInfo.sort = jdata.getInt("sort");
                carInfo.mycar = jdata.getString("mycar");
                carInfo.reg_date = jdata.getString("reg_date");
                carLists.data.add(carInfo);
                return carLists;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }

    }

    public CarLists listOfCar() throws ServerErrorException{
        String msg;
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
            if(result.getInt("res")==1){
                CarLists carLists = new CarLists();;
                Log.d("ServerClient", msg);
                carLists.itemTotalCount = result.getInt("itemTotalCount");
                carLists.pageCount = result.getInt("pageCount");
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                CarInfo carInfo = new CarInfo();
                carInfo.idx = jdata.getInt("idx");
                carInfo.uniqueID = jdata.getString("uniqueID");
                carInfo.sort = jdata.getInt("sort");
                carInfo.mycar = jdata.getString("mycar");
                carInfo.reg_date = jdata.getString("reg_date");
                carLists.data.add(carInfo);
                return carLists;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public CarLists priorityCar(final String index) throws ServerErrorException{
        String msg;
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
            if(result.getInt("res")==1){
                CarLists carLists = new CarLists();;
                Log.d("ServerClient", msg);
                carLists.itemTotalCount = result.getInt("itemTotalCount");
                carLists.pageCount = result.getInt("pageCount");
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                CarInfo carInfo = new CarInfo();
                carInfo.idx = jdata.getInt("idx");
                carInfo.uniqueID = jdata.getString("uniqueID");
                carInfo.sort = jdata.getInt("sort");
                carInfo.mycar = jdata.getString("mycar");
                carInfo.reg_date = jdata.getString("reg_date");
                carLists.data.add(carInfo);
                return carLists;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
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
     * 미완성
     */
    public CardList card_Register(final String card_name) throws ServerErrorException{
        String msg;
        final String CardRegIn_URL = "https://inilite.inicis.com/inibill/inibill_card.jsp";
        final String CardRegOut_URL = "http://app.parkstem.com/api/card_reg.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID + "^" +card_name);
                hashMap.put("uniqueID", "hotelvey11");
                hashMap.put("uniqueID", "certification");
                hashMap.put("uniqueID", "1");
                hashMap.put("uniqueID", "1");
                hashMap.put("uniqueID", "AAA");
                hashMap.put("uniqueID", "good");
                hashMap.put("uniqueID", "20160427171717");
                hashMap.put("uniqueID", "");
                hashMap.put("hashdata", "0c4b70d28e3dfbdf6561d3aff631f8355a3991c965223bd88285a8d9f8c0e935");
                result = connect(hashMap, CardRegIn_URL, CardRegOut_URL);
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
        public int itemTotalCount;
        public int pageCount;
        public ArrayList<Payment> data;
    }
    public PaymentList hipassPayment() throws ServerErrorException{

        /*
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
                throw new ServerErrorException(result.getInt("res"), result.getString("msg"));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
        */

        Payment p1 = new Payment();
        p1.price = 10000;
        p1.card_name = "국민카드";
        p1.calendar = Calendar.getInstance();
        Payment p2 = new Payment();
        p2.price = 3000;
        p2.card_name = "나라사랑카드";
        p2.calendar = Calendar.getInstance();
        Payment p3 = new Payment();
        p3.price = 200000;
        p3.card_name = "삼성카드";
        p3.calendar = Calendar.getInstance();

        ArrayList<Payment> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);

        PaymentList paymentList = new PaymentList();
        paymentList.pageCount = 1;
        paymentList.itemTotalCount = 3;
        paymentList.data = list;

        return paymentList;
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
    public JSONObject mcertification() throws ServerErrorException{
        String msg;
        final String CertIn_URL = "http://app.parkstem.com/api/kmcis_start.php";
        final String CertOut_URL = "http://app.parkstem.com/api/kmcis_mobile.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                result = connect(hashMap, CertIn_URL, CertOut_URL);
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
