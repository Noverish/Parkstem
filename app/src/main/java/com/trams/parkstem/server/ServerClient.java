package com.trams.parkstem.server;

import android.util.Log;

import org.json.JSONArray;
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
    //모든 함수에 더미 반환값 추가.
    //코드정리
    //1.모든 함수가 JSON 반환하면 안됨.
    //1.exception에 res랑 msg를 넣어서 throw.
    //1.모든 클래스에 res랑 msg랑 uniqueID를 뺀다.
    //uniqueID를 반환하는 함수는 현재의 uniqueID와 같은지 체크하는 코드
    //dummy 카드 관련 모든것 입차 출차 주차장

    private String uniqueID = "13617600";

    public static ServerClient serverClient;
    public static ServerClient getInstance() {
        if(serverClient == null) {
            serverClient = new ServerClient();
        }
        return serverClient;
    }

    private ArrayList<CarInfo> carInfoArrayList = new ArrayList<>();

    public ServerClient() {
        CarInfo ci1 = new CarInfo();
        ci1.reg_date = Calendar.getInstance();
        ci1.sort = 3;
        ci1.idx = 14;
        ci1.uniqueID = "134";
        ci1.mycar = "11일1111";
        CarInfo ci2 = new CarInfo();
        ci2.reg_date = Calendar.getInstance();
        ci2.sort = 4;
        ci2.idx = 134;
        ci2.uniqueID = "12";
        ci2.mycar = "22이2222";

        carInfoArrayList.add(ci1);
        carInfoArrayList.add(ci2);
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

    //String값으로 받은 pay_date등을 Calendar로 변환
    public Calendar stringToCalendar(final String date){
        String[] dates;
        Calendar calendar;

        dates = date.split("\\D");
        calendar = Calendar.getInstance();
        if(dates.length<4){
            calendar.set(Integer.parseInt(dates[0]),Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
            return calendar;
        }
        else{
            calendar.set(Integer.parseInt(dates[0]),Integer.parseInt(dates[1]), Integer.parseInt(dates[2]),Integer.parseInt(dates[3]),Integer.parseInt(dates[4]));
            return calendar;
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
     * 암호화
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
        final String DEL_URL = "http://app.parkstem.com/api/member_del.php";
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
    public DashBoard dashboard() throws ServerErrorException{
//        String msg;
//        final String DASH_URL = "http://app.parkstem.com/api/dashboard.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID",uniqueID);
//                result = connect(hashMap, DASH_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                DashBoard dashboard = new DashBoard();
//                Log.d("ServerClient",msg);
//                dashboard.mycar = result.getString("mycar");
//                dashboard.mycard = result.getString("mycard");
//
//                JSONArray jarray = result.getJSONArray("data");
//                for(int i=0;i<jarray.length();i++){
//                    JSONObject jdata = jarray.getJSONObject(i);
//                    Payment pm = new Payment();
//                    pm.card_name = jdata.getString("card_name");
//                    pm.pay_date = stringToCalendar(jdata.getString("card_name"));
//                    pm.price = jdata.getInt("price");
//                    dashboard.data.add(pm);
//                }
//                return dashboard;
//            }
//            else{
//                throw new ServerErrorException(result.getInt("res"),msg);
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        DashBoard dashboard = new DashBoard();
        dashboard.mycar = "현기차";
        dashboard.mycard = "하나카드";

        Payment pm1 = new Payment();
        pm1.pay_date = Calendar.getInstance();
        pm1.price = 1000;
        pm1.card_name = "하나카드";
        Payment pm2 = new Payment();
        pm2.pay_date = Calendar.getInstance();
        pm2.price = 20000;
        pm2.card_name = "삼성카드";

        ArrayList<Payment> list = new ArrayList<>();
        list.add(pm1);
        list.add(pm2);

        dashboard.data = list;
        return dashboard;
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
    public RecentCar recentCar() throws ServerErrorException{
//        String msg;
//        final String Recent_URL = "http://app.parkstem.com/api/car_recent.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID",uniqueID);
//                result = connect(hashMap, Recent_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                RecentCar recentcar = new RecentCar();
//                Log.d("ServerClient",msg);
//                recentcar.local_id = result.getString("local_id");
//                recentcar.in_date = stringToCalendar(result.getString("in_date"));
//                recentcar.out_date = stringToCalendar(result.getString("out_date"));
//                recentcar.total = result.getInt("total");
//                return recentcar;
//            }
//            else{
//                throw new ServerErrorException(result.getInt("res"),msg);
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        RecentCar recentCar = new RecentCar();
        recentCar.local_id = "132654";
        recentCar.in_date = Calendar.getInstance();
        recentCar.out_date = Calendar.getInstance();
        recentCar.total = 202200;

        return recentCar;
    }


    //주차장정보 함수

    /**이 함수는 data형식으로 값을 받아온다
     * 확인 필요
     * **/
    public ParkInfo parkInfo(final String local_id) throws ServerErrorException{
//        String msg;
//        final String Parkinfo_URL = "http://app.parkstem.com/api/car_recent.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("local_id",local_id);
//                result = connect(hashMap, Parkinfo_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                ParkInfo parkinfo = new ParkInfo();
//                Log.d("ServerClient",msg);
//                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
//
//                parkinfo.local_id = jdata.getString("local_id");
//                parkinfo.local_name = jdata.getString("local_name");
//                parkinfo.local_content = jdata.getString("local_content");
//                parkinfo.local_address = jdata.getString("local_address");
//                parkinfo.local_phone = jdata.getString("local_phone");
//                parkinfo.local_photo = jdata.getString("local_photo");
//                parkinfo.free_time = jdata.getInt("free_time");
//                parkinfo.park_price = jdata.getInt("park_price");
//                parkinfo.park_price_time = jdata.getInt("park_price_time");
//                return parkinfo;
//            }
//            else{
//                throw new ServerErrorException(result.getInt("res"), msg);
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        ParkInfo parkInfo = new ParkInfo();
        parkInfo.local_id = "12345";
        parkInfo.local_name = "주차아장";
        parkInfo.local_content = "귀찮아";
        parkInfo.local_address = "12345";
        parkInfo.local_phone = "12345";
        parkInfo.local_photo = "1.jpg";
        parkInfo.free_time = 10;
        parkInfo.park_price = 10;
        parkInfo.park_price_time = 10;

        return parkInfo;
    }


    //차량관리 함수
    public CarLists CarRegister(final String mycar) throws ServerErrorException{
//        String msg;
//        final String DASH_URL = "http://app.parkstem.com/api/car_reg.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID",uniqueID);
//                hashMap.put("mycar", mycar);
//                result = connect(hashMap, DASH_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                CarLists carLists = new CarLists();;
//                Log.d("ServerClient", msg);
//                carLists.itemTotalCount = result.getInt("itemTotalCount");
//                carLists.pageCount = result.getInt("pageCount");
//
//                JSONArray jarray = result.getJSONArray("data");
//                for(int i=0;i<jarray.length();i++) {
//                    JSONObject jdata = jarray.getJSONObject(i);
//                    CarInfo carInfo = new CarInfo();
//                    carInfo.idx = jdata.getInt("idx");
//                    carInfo.uniqueID = jdata.getString("uniqueID");
//                    carInfo.sort = jdata.getInt("sort");
//                    carInfo.mycar = jdata.getString("mycar");
//                    carInfo.reg_date = stringToCalendar(jdata.getString("mycar"));
//                    carLists.data.add(carInfo);
//                }
//                return carLists;
//            }
//            else{
//                throw new ServerErrorException(result.getInt("res"), msg);
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        CarLists carLists = new CarLists();
        carLists.itemTotalCount = 2;
        carLists.pageCount = 1;
        carLists.data = carInfoArrayList;
        return carLists;
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
                Log.e("listOfCar",result.toString());
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
                carLists.data = new ArrayList<>();

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    CarInfo carInfo = new CarInfo();
                    //carInfo.idx = jdata.getInt("idx");
                    //carInfo.uniqueID = jdata.getString("uniqueID");
                    carInfo.sort = jdata.getInt("sort");
                    carInfo.mycar = jdata.getString("carNumber");
                    //carInfo.reg_date = stringToCalendar(jdata.getString("regdate"));
                    carLists.data.add(carInfo);
                }
                return carLists;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
//        CarLists carLists = new CarLists();
//        carLists.itemTotalCount = 2;
//        carLists.pageCount = 1;
//        carLists.data = carInfoArrayList;
//        return carLists;
    }

    public CarLists priorityCar(final String index) throws ServerErrorException{
//        String msg;
//        final String Clist_URL = "http://app.parkstem.com/api/car_sort.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID",uniqueID);
//                hashMap.put("idx",index);
//                result = connect(hashMap, Clist_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                CarLists carLists = new CarLists();;
//                Log.d("ServerClient", msg);
//                carLists.itemTotalCount = result.getInt("itemTotalCount");
//                carLists.pageCount = result.getInt("pageCount");
//
//                JSONArray jarray = result.getJSONArray("data");
//                for(int i=0;i<jarray.length();i++) {
//                    JSONObject jdata = jarray.getJSONObject(i);
//                    CarInfo carInfo = new CarInfo();
//                    carInfo.idx = jdata.getInt("idx");
//                    carInfo.uniqueID = jdata.getString("uniqueID");
//                    carInfo.sort = jdata.getInt("sort");
//                    carInfo.mycar = jdata.getString("mycar");
//                    carInfo.reg_date = stringToCalendar(jdata.getString("mycar"));
//                    carLists.data.add(carInfo);
//                }
//                return carLists;
//            }
//            else{
//                throw new ServerErrorException(result.getInt("res"), msg);
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        CarLists carLists = new CarLists();
        carLists.itemTotalCount = 2;
        carLists.pageCount = 1;

        CarInfo ci1 = new CarInfo();
        ci1.reg_date = Calendar.getInstance();
        ci1.sort = 5;
        ci1.idx = 234;
        ci1.uniqueID = "14";
        ci1.mycar = "BMK";
        CarInfo ci2 = new CarInfo();
        ci2.reg_date = Calendar.getInstance();
        ci2.sort = 2;
        ci2.idx = 134;
        ci2.uniqueID = "5";
        ci2.mycar = "SM5";


        ArrayList<CarInfo> list = new ArrayList<>();
        list.add(ci1);
        list.add(ci2);

        carLists.data = list;
        return carLists;
    }

    public void deleteCar(final String mycar) throws ServerErrorException{
        String msg;
        final String DELETE_URL = "http://app.parkstem.com/api/car_del.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("mycar",mycar);
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
            if(result.getInt("res")==1){
                Log.d("ServerClient",msg);
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public CarIn carIn() throws ServerErrorException{
        String msg;
        final String CARIN_URL = "http://app.parkstem.com/api/car_in.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
                result = connect(hashMap, CARIN_URL);
                Log.e("carIn",result.toString());
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
                CarIn carin = new CarIn();
                Log.d("ServerClient", msg);

                carin.itemTotalCount = result.getInt("itemTotalCount");
                carin.pageCount = result.getInt("pageCount");
                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
                carin.indate = stringToCalendar(jdata.getString("indate"));
                carin.local_id = jdata.getString("local_id");
                return carin;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
//        CarIn carin = new CarIn();
//
//        carin.local_id = "12345";
//        carin.indate = Calendar.getInstance();
//
//        return carin;
    }

    public CarOut carOut() throws ServerErrorException {
//        String msg;
//        final String CAROUT_URL = "http://app.parkstem.com/api/car_out.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID", uniqueID);
//                result = connect(hashMap, CAROUT_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if (result.getInt("res") == 1) {
//                CarOut carOut = new CarOut();
//                Log.d("ServerClient", msg);
//
//                carOut.itemTotalCount = result.getInt("itemTotalCount");
//                carOut.pageCount = result.getInt("pageCount");
//
//                JSONObject jdata = result.getJSONArray("data").getJSONObject(0);
//                carOut.outdate = stringToCalendar(jdata.getString("outdate"));
//                carOut.local_id = jdata.getString("local_id");
//                return carOut;
//            } else {
//                throw new ServerErrorException(result.getInt("res"), msg);
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }

        CarOut carout = new CarOut();

        carout.local_id = "12345";
        carout.outdate = Calendar.getInstance();

        return carout;
    }


    //카드 관련 함수
    /**
     * 미완성
     */
    public CardList card_Register(final String card_name) throws ServerErrorException{
//        String msg;
//        final String CardRegIn_URL = "https://inilite.inicis.com/inibill/inibill_card.jsp";
//        final String CardRegOut_URL = "http://app.parkstem.com/api/card_reg.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID", uniqueID + "^" +card_name);
//                hashMap.put("uniqueID", "hotelvey11");
//                hashMap.put("uniqueID", "certification");
//                hashMap.put("uniqueID", "1");
//                hashMap.put("uniqueID", "1");
//                hashMap.put("uniqueID", "AAA");
//                hashMap.put("uniqueID", "good");
//                hashMap.put("uniqueID", "20160427171717");
//                hashMap.put("uniqueID", "");
//                hashMap.put("hashdata", "0c4b70d28e3dfbdf6561d3aff631f8355a3991c965223bd88285a8d9f8c0e935");
//                result = connect(hashMap, CardRegIn_URL, CardRegOut_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                CardList cardList = new CardList();
//                Log.d("ServerClient", msg);
//                cardList.itemTotalCount= result.getInt("itemTotalCount");
//                cardList.pageCount= result.getInt("pageCount");
//
//                JSONArray jarray = result.getJSONArray("data");
//                for(int i=0;i<jarray.length();i++) {
//                    JSONObject jdata = jarray.getJSONObject(i);
//                    CardInfo cardInfo = new CardInfo();
//                    cardInfo.idx = jdata.getInt("idx");
//                    cardInfo.sort = jdata.getInt("sort");
//                    cardInfo.card_name = jdata.getString("card_name");
//                    cardInfo.reg_date = stringToCalendar(jdata.getString("reg_date"));
//                    cardList.data.add(cardInfo);
//                }
//                return cardList;
//            }
//            else{
//                throw new ServerErrorException();
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        CardList cardLists = new CardList();
        cardLists.itemTotalCount = 2;
        cardLists.pageCount = 1;

        CardInfo ci1 = new CardInfo();
        ci1.reg_date = Calendar.getInstance();
        ci1.sort = 1;
        ci1.idx = 1234;
        ci1.card_name = "나라사랑카드";
        CardInfo ci2 = new CardInfo();
        ci2.reg_date = Calendar.getInstance();
        ci2.sort = 3;
        ci2.idx = 134;
        ci2.card_name = "나라사랑카드22";


        ArrayList<CardInfo> list = new ArrayList<>();
        list.add(ci1);
        list.add(ci2);

        cardLists.data = list;
        return cardLists;
    }

    public CardList cardList() throws ServerErrorException{
        String msg;
        final String CL_URL = "http://app.parkstem.com/api/card_list.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                result = connect(hashMap, CL_URL);
                Log.e("cardList",result.toString());
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
                CardList cardList = new CardList();
                Log.d("ServerClient", msg);
                cardList.itemTotalCount= result.getInt("itemTotalCount");
                cardList.pageCount= result.getInt("pageCount");
                cardList.data = new ArrayList<>();

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    CardInfo cardInfo = new CardInfo();
                    cardInfo.idx = jdata.getInt("idx");
                    //cardInfo.sort = jdata.getInt("sort");
                    cardInfo.card_name = jdata.getString("card_name");
                    //cardInfo.reg_date = stringToCalendar(jdata.getString("reg_date"));
                    cardList.data.add(cardInfo);
                }
                return cardList;
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
//        CardList cardLists = new CardList();
//        cardLists.itemTotalCount = 2;
//        cardLists.pageCount = 1;
//
//        CardInfo ci1 = new CardInfo();
//        ci1.reg_date = Calendar.getInstance();
//        ci1.sort = 12;
//        ci1.idx = 12;
//        ci1.card_name = "나라사랑카드ver2";
//        CardInfo ci2 = new CardInfo();
//        ci2.reg_date = Calendar.getInstance();
//        ci2.sort = 23;
//        ci2.idx = 14;
//        ci2.card_name = "나라사랑카드ver2.2";
//
//
//        ArrayList<CardInfo> list = new ArrayList<>();
//        list.add(ci1);
//        list.add(ci2);
//
//        cardLists.data = list;
//        return cardLists;
    }

    public CardList cardSort(final String idx) throws ServerErrorException{
//        String msg;
//        final String CS_URL = "http://app.parkstem.com/api/card_sort.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID", uniqueID);
//                hashMap.put("idx", idx);
//                result = connect(hashMap, CS_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                CardList cardList = new CardList();
//                Log.d("ServerClient", msg);
//                cardList.itemTotalCount= result.getInt("itemTotalCount");
//                cardList.pageCount= result.getInt("pageCount");
//
//                JSONArray jarray = result.getJSONArray("data");
//                for(int i=0;i<jarray.length();i++) {
//                    JSONObject jdata = jarray.getJSONObject(i);
//                    CardInfo cardInfo = new CardInfo();
//                    cardInfo.idx = jdata.getInt("idx");
//                    cardInfo.sort = jdata.getInt("sort");
//                    cardInfo.card_name = jdata.getString("card_name");
//                    cardInfo.reg_date = stringToCalendar(jdata.getString("reg_date"));
//                    cardList.data.add(cardInfo);
//                }
//                return cardList;
//            }
//            else{
//                throw new ServerErrorException();
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        CardList cardLists = new CardList();
        cardLists.itemTotalCount = 2;
        cardLists.pageCount = 1;

        CardInfo ci1 = new CardInfo();
        ci1.reg_date = Calendar.getInstance();
        ci1.sort = 13;
        ci1.idx = 4;
        ci1.card_name = "나라사랑카드ver3";
        CardInfo ci2 = new CardInfo();
        ci2.reg_date = Calendar.getInstance();
        ci2.sort = 43;
        ci2.idx = 1;
        ci2.card_name = "나라사랑카드ver2.3";


        ArrayList<CardInfo> list = new ArrayList<>();
        list.add(ci1);
        list.add(ci2);

        cardLists.data = list;
        return cardLists;
    }

    public void cardDelete(final String idx) throws ServerErrorException{
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
            msg = result.getString("msg");
            if(result.getInt("res")==1){
                Log.d("ServerClient",msg);
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public PaymentList hipassPayment() throws ServerErrorException{
        final String HiPay_URL = "http://app.parkstem.com/api/pay_list.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                result = connect(hashMap, HiPay_URL);
                Log.e("hipass",result.toString());
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

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    Payment pm = new Payment();
                    pm.card_name = jdata.getString("card_name");
                    pm.pay_date = stringToCalendar(jdata.getString("pay_date"));
                    pm.price = jdata.getInt("price");
                    paymentlist.data.add(pm);
                }
                return paymentlist;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), result.getString("msg"));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }


//        Payment p1 = new Payment();
//        p1.price = 10000;
//        p1.card_name = "국민카드";
//        p1.pay_date = Calendar.getInstance();
//        Payment p2 = new Payment();
//        p2.price = 3000;
//        p2.card_name = "나라사랑카드";
//        p2.pay_date = Calendar.getInstance();
//        Payment p3 = new Payment();
//        p3.price = 200000;
//        p3.card_name = "삼성카드";
//        p3.pay_date = Calendar.getInstance();
//
//        ArrayList<Payment> list = new ArrayList<>();
//        list.add(p1);
//        list.add(p2);
//        list.add(p3);
//
//        PaymentList paymentList = new PaymentList();
//        paymentList.pageCount = 1;
//        paymentList.itemTotalCount = 3;
//        paymentList.data = list;
//
//        return paymentList;
    }

    public PaymentInfo ticketpurchase() throws ServerErrorException{
//        String msg;
//        final String TicketBuy_URL = "http://app.parkstem.com/api/ticket_buy_list.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID", uniqueID);
//                result = connect(hashMap, TicketBuy_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                PaymentInfo paymentinfo = new PaymentInfo();
//                Log.d("ServerClient", msg);
//                paymentinfo.itemTotalCount = result.getInt("itemTotalCount");
//                paymentinfo.pageCount = result.getInt("pageCount");
//
//                JSONArray jarray = result.getJSONArray("data");
//                for(int i=0;i<jarray.length();i++) {
//                    JSONObject jdata = jarray.getJSONObject(i);
//                    TicketBuyList ticketBuyList = new TicketBuyList();
//                    ticketBuyList.gubun = jdata.getInt("gubun");
//                    ticketBuyList.local_id = jdata.getString("local_id");
//                    ticketBuyList.card_name = jdata.getString("card_name");
//                    ticketBuyList.price = jdata.getInt("price");
//                    ticketBuyList.start_date = stringToCalendar(jdata.getString("start_date"));
//                    ticketBuyList.end_date = stringToCalendar(jdata.getString("end_date"));
//                    ticketBuyList.pay_date = stringToCalendar(jdata.getString("pay_date"));
//                    paymentinfo.data.add(ticketBuyList);
//                }
//                return paymentinfo;
//            }
//            else{
//                throw new ServerErrorException(result.getInt("res"), msg);
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        TicketBuyList tbl1 = new TicketBuyList();
        tbl1.price = 10000;
        tbl1.card_name = "국민카드";
        tbl1.local_id = "12345";
        tbl1.gubun = 1;
        tbl1.pay_date = Calendar.getInstance();
        tbl1.end_date = Calendar.getInstance();
        tbl1.start_date = Calendar.getInstance();
        TicketBuyList tbl2 = new TicketBuyList();
        tbl2.price = 12000;
        tbl2.card_name = "삼성카드";
        tbl2.local_id = "12355";
        tbl2.gubun = 2;
        tbl2.pay_date = Calendar.getInstance();
        tbl2.end_date = Calendar.getInstance();
        tbl2.start_date = Calendar.getInstance();
        TicketBuyList tbl3 = new TicketBuyList();
        tbl3.price = 5000;
        tbl3.card_name = "나라사랑카드";
        tbl3.local_id = "12365";
        tbl3.gubun = 2;
        tbl3.pay_date = Calendar.getInstance();
        tbl3.end_date = Calendar.getInstance();
        tbl3.start_date = Calendar.getInstance();


        ArrayList<TicketBuyList> list = new ArrayList<>();
        list.add(tbl1);
        list.add(tbl2);
        list.add(tbl3);

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.pageCount = 1;
        paymentInfo.itemTotalCount = 3;
        paymentInfo.data = list;

        return paymentInfo;
    }


    //티켓 관련 함수
    public TicketLists listOfTicket() throws ServerErrorException{
//        String msg;
//        final String Tlist_URL = "http://app.parkstem.com/api/ticket_list.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID", uniqueID);
//                result = connect(hashMap, Tlist_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                TicketLists ticketLists = new TicketLists();
//                Log.d("ServerClient", msg);
//                ticketLists.itemTotalCount = result.getInt("itemTotalCount");
//                ticketLists.pageCount = result.getInt("pageCount");
//
//                JSONArray jarray = result.getJSONArray("data");
//                for(int i=0;i<jarray.length();i++) {
//                    JSONObject jdata = jarray.getJSONObject(i);
//                    Ticket ticket = new Ticket();
//                    ticket.idx = jdata.getInt("idx");
//                    ticket.local_id = jdata.getString("local_id");
//                    ticket.ticket_name = jdata.getString("ticket_name");
//                    ticket.term = stringToCalendar(jdata.getString("term"));
//                    ticket.term_name = jdata.getString("term_name");
//                    ticket.gubun = jdata.getInt("gubun");
//                    ticket.original_price = jdata.getInt("original_price");
//                    ticket.price = jdata.getInt("price");
//                    ticket.regdate = stringToCalendar(jdata.getString("regdate"));
//                    ticketLists.data.add(ticket);
//                }
//                return ticketLists;
//            }
//            else{
//                throw new ServerErrorException(result.getInt("res"),msg);
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        TicketLists ticketLists = new TicketLists();
        ticketLists.pageCount = 1;
        ticketLists.itemTotalCount = 2;


        Ticket t1 = new Ticket();
        t1.price = 12000;
        t1.idx = 12;
        t1.term = Calendar.getInstance();
        t1.term_name = "term_name1";
        t1.ticket_name = "고급티켓";
        t1.local_id = "12345";
        t1.gubun = 1;
        t1.original_price = 14000;
        t1.regdate = Calendar.getInstance();
        Ticket t2 = new Ticket();
        t2.price = 14000;
        t2.idx = 13;
        t2.term = Calendar.getInstance();
        t2.term_name = "term_name2";
        t2.ticket_name = "저급티켓";
        t2.local_id = "12355";
        t2.gubun = 1;
        t2.original_price = 15000;
        t2.regdate = Calendar.getInstance();


        ArrayList<Ticket> list = new ArrayList<>();
        list.add(t1);
        list.add(t2);

        ticketLists.data = list;

        return ticketLists;
    }

    public LongTicketLists listOfLongTicket() throws ServerErrorException{
//        String msg;
//        final String LTlist_URL = "http://app.parkstem.com/api/longticket_list.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID", uniqueID);
//                result = connect(hashMap, LTlist_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                LongTicketLists longTicketLists = new LongTicketLists();
//                Log.d("ServerClient", msg);
//                longTicketLists.itemTotalCount = result.getInt("itemTotalCount");
//                longTicketLists.pageCount = result.getInt("pageCount");
//
//                JSONArray jarray = result.getJSONArray("data");
//                for(int i=0;i<jarray.length();i++) {
//                    JSONObject jdata = jarray.getJSONObject(i);
//                    Ticket ticket = new Ticket();
//                    ticket.idx = jdata.getInt("idx");
//                    ticket.local_id = jdata.getString("local_id");
//                    ticket.ticket_name = jdata.getString("ticket_name");
//                    ticket.term = stringToCalendar(jdata.getString("term"));
//                    ticket.term_name = jdata.getString("term_name");
//                    ticket.gubun = jdata.getInt("gubun");
//                    ticket.original_price = jdata.getInt("original_price");
//                    ticket.price = jdata.getInt("price");
//                    ticket.regdate = stringToCalendar(jdata.getString("regdate"));
//                    longTicketLists.data.add(ticket);
//                }
//                return longTicketLists;
//            }
//            else{
//                throw new ServerErrorException(result.getInt("res"), msg);
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        LongTicketLists longticketLists = new LongTicketLists();
        longticketLists.pageCount = 1;
        longticketLists.itemTotalCount = 2;


        Ticket t1 = new Ticket();
        t1.price = 12000;
        t1.idx = 12;
        t1.term = Calendar.getInstance();
        t1.term_name = "term_name1";
        t1.ticket_name = "장기고급티켓";
        t1.local_id = "12355";
        t1.gubun = 2;
        t1.original_price = 14000;
        t1.regdate = Calendar.getInstance();
        Ticket t2 = new Ticket();
        t2.price = 14000;
        t2.idx = 13;
        t2.term = Calendar.getInstance();
        t2.term_name = "term_name2";
        t2.ticket_name = "장기저급티켓";
        t2.local_id = "12365";
        t2.gubun = 2;
        t2.original_price = 15000;
        t2.regdate = Calendar.getInstance();


        ArrayList<Ticket> list = new ArrayList<>();
        list.add(t1);
        list.add(t2);

        longticketLists.data = list;

        return longticketLists;
    }

    public TicketInfo ticketInfo(final String local_id, final String gubun, final String idx) throws ServerErrorException{
//        String msg;
//        final String T_INFO_URL = "http://app.parkstem.com/api/ticket_info.php";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("uniqueID",uniqueID);
//                hashMap.put("local_id",local_id);
//                hashMap.put("gubun",gubun);
//                hashMap.put("idx",idx);
//                result = connect(hashMap, T_INFO_URL);
//            }
//        });
//
//        try {
//            thread.start();
//            thread.join();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            msg = result.getString("msg");
//            if(result.getInt("res")==1){
//                TicketInfo ticketInfo = new TicketInfo();
//                Log.d("ServerClient",msg);
//                ticketInfo.idx = result.getInt("idx");
//                ticketInfo.local_id = result.getString("local_id");
//                ticketInfo.gubun = result.getInt("gubun");
//                ticketInfo.price = result.getInt("price");
//                ticketInfo.ticket_name = result.getString("ticket_name");
//                ticketInfo.card_use = result.getBoolean("card_use");
//                return ticketInfo;
//            }
//            else{
//                throw new ServerErrorException(result.getInt("res"), msg);
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            throw new ServerErrorException();
//        }
        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.idx = 12;
        ticketInfo.local_id = "123";
        ticketInfo.gubun = 1;
        ticketInfo.price = 12000;
        ticketInfo.ticket_name = "쓸모없는티켓";
        ticketInfo.card_use = true;

        return ticketInfo;
    }

    public void ticketInfoRegister(final String gubun, final String idx, final String user_name, final String user_phone, final String user_email, final String start_date, final String end_date, final String price) throws ServerErrorException{
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
            msg = result.getString("msg");
            if(result.getInt("res")==1){
                Log.d("ServerClient",msg);
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }


    //모바일 인증
    public void mobileCertification() throws ServerErrorException{
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
            msg = result.getString("msg");
            if(result.getInt("res")==1){
                Log.d("ServerClient", msg);
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
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




    //Classes

    public class DashBoard{
        public String mycar;
        public String mycard;
        public ArrayList<Payment> data;
    }
    public class Payment{
        public String card_name;
        public int price;
        public Calendar pay_date;
    }
    public class PaymentList{
        public int itemTotalCount;
        public int pageCount;
        public ArrayList<Payment> data;
    }

    public class RecentCar{
        public String local_id;
        public Calendar in_date;
        public Calendar out_date;
        public int total;
    }

    public class ParkInfo{
        public String local_id;
        public String local_name;
        public String local_content;
        public String local_address;
        public String local_phone;
        public String local_photo;
        public int free_time;
        public int park_price;
        public int park_price_time;
    }

    public class CarLists{
        public int itemTotalCount;
        public int pageCount;
        public ArrayList<CarInfo> data;
    }
    public class CarInfo {
        public int idx;
        public String uniqueID;
        public int sort;
        public String mycar;
        public Calendar reg_date;
    }
    public class CarIn{
        public int itemTotalCount;
        public int pageCount;
        public Calendar indate;
        public String local_id;
    }
    public class CarOut{
        public int itemTotalCount;
        public int pageCount;
        public Calendar outdate;
        public String local_id;
    }

    public class CardList{
        public int itemTotalCount;
        public int pageCount;
        public ArrayList<CardInfo> data;
    }
    public class CardInfo{
        public int idx;
        public int sort;
        public String card_name;
        public Calendar reg_date;
    }

    public class PaymentInfo{
        public int itemTotalCount;
        public int pageCount;
        public ArrayList<TicketBuyList> data;
    }
    public class TicketBuyList{
        public int gubun;
        public String local_id;
        public String card_name;
        public int price;
        public Calendar start_date;
        public Calendar end_date;
        public Calendar pay_date;
    }

    public class TicketLists{
        public int itemTotalCount;
        public int pageCount;
        public ArrayList<Ticket> data;
    }
    public class Ticket{
        public int idx;
        public String local_id;
        public String ticket_name;
        public Calendar term;
        public String term_name;
        public int gubun;
        public int original_price;
        public int price;
        public Calendar regdate;
    }

    public class LongTicketLists{
        public int itemTotalCount;
        public int pageCount;
        public ArrayList<Ticket> data;
    }

    public class TicketInfo{
        public int idx;
        public String local_id;
        public int gubun;
        public int price;
        public String ticket_name;
        public boolean card_use;
    }
}
