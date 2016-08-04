package com.trams.parkstem.server;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.trams.parkstem.others.Essentials;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by monc2 on 2016-07-04.
 */
public class ServerClient  {
    //모든 함수에 더미 반환값 추가.
    //코드정리
    //1.모든 함수가 JSON 반환하면 안됨.
    //1.exception에 res랑 msg를 넣어서 throw.
    //1.모든 클래스에 res랑 msg랑 uniqueID를 뺀다.
    //uniqueID를 반환하는 함수는 현재의 uniqueID와 같은지 체크하는 코드
    //dummy 카드 관련 모든것 입차 출차 주차장

    private String uniqueID = "13617600";
    private String userName;
    private String userEmail;
    private String userMobile;
    private boolean userCertification;
    private boolean userPush;

    public static ServerClient serverClient;
    public static ServerClient getInstance() {
        if(serverClient == null) {
            serverClient = new ServerClient();
        }
        return serverClient;
    }

    private ServerClient() {}

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

            Log.e("jsonStr",jsonStr);

            return new JSONObject(jsonStr);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    //회원가입 및 로그인 관련 함수
    public void login(final String memberGubun, final String parkstemID, final String parkstemPW, final String token) throws ServerErrorException {
        Log.e(TAG,"login : " + memberGubun + ", " + parkstemID + ", " + parkstemPW + ", " + token);

        int res;
        String msg;
        final String LOGIN_URL = "http://app.parkstem.com/api/member_login.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("memberGubun", memberGubun);
                hashMap.put("parkstemID", parkstemID);
                hashMap.put("parkstemPW", parkstemPW);
                hashMap.put("token", token);
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
            res = result.getInt("res");
            Log.e("login res",res + "");

            if(res != 0){
                Log.d("ServerClient",msg);
                uniqueID = result.getString("uniqueID");

                userName = result.getString("name");
                userEmail = result.getString("email");
                userMobile = result.getString("mobile");
                userPush = result.getString("pushYN").equals("Y");
                userCertification = result.getString("certification").equals("Y");
            }
            else
            {
                Log.e("throw","던진다!");
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }

        /*login.name = "나이름";
        login.email = "email@email.com";
        login.phone = "000-000-0000";
        login.certification = true;
        login.pushYN = true;*/
    }


    public void register(final String name, final String email, final String mobile, final String nickName, final String parkstemID, final String parkstemPW, final String token) throws ServerErrorException{
        Log.e(TAG,"register : " + name + ", " + email + ", " + mobile + ", " + nickName + ", " + parkstemID + ", " + parkstemPW + ", " + token);

        JSONObject result;
        String uniqueID;

        final String JOIN_URL = "http://app.parkstem.com/api/member_join.php";
        String date = Essentials.calendarToDateWithBar(Calendar.getInstance());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("memberGubun","parkstem");
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("mobile", mobile);
        hashMap.put("nickName", nickName);
        hashMap.put("parkstemID", parkstemID);
        hashMap.put("parkstemPW", parkstemPW);
        hashMap.put("token", token);
        hashMap.put("regDate", date);

        try {
            ConnectThread thread = new ConnectThread(JOIN_URL, hashMap);
            result = thread.getResult();

            uniqueID = result.getString("uniqueID");
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        }
    }

    public MemberInfo memberInfo() throws ServerErrorException{
        Log.e(TAG,"memberInfo : " + uniqueID);

        //{"res":"1","msg":"회원정보입니다.","name":"","email":"","mobile":"","certification":"","pushYN":"","token":"","uniqueID":""}

        String msg;
        final String LOGIN_URL = "http://app.parkstem.com/api/member_info.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
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
                //uniqueID = result.getString("uniqueID");

                MemberInfo memberInfo = new MemberInfo();
                userName = memberInfo.name = result.getString("name");
                userEmail = memberInfo.email = result.getString("email");
                userMobile = memberInfo.mobile = result.getString("mobile");
                userPush = memberInfo.pushYN = result.getString("pushYN").equals("Y");
                userCertification = memberInfo.certification = result.getString("certification").equals("Y");
                return memberInfo;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류가 발생했습니다.");
        }
    }

    public void memberDelete() throws ServerErrorException{
        Log.e(TAG,"memberDelete");

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
        Log.e(TAG,"dashboard");

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
                dashboard.itemTotalCount = result.getInt("itemTotalCount");
                dashboard.pageCount = result.getInt("pageCount");
                dashboard.hipass = result.getString("hipass").equals("Y");

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++){
                    JSONObject jdata = jarray.getJSONObject(i);
                    Dash pm = new Dash();
                    pm.card_name = jdata.getString("card_name");
                    pm.pay_date = Essentials.stringToCalendar(jdata.getString("pay_date"));
                    pm.price = jdata.getString("price");
                    dashboard.data.add(pm);
                }
                return dashboard;
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    } //확인완료


    public void hipassOn(final String hipass) throws ServerErrorException{
        Log.e(TAG,"hipassOn : " + hipass);

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
    } //확인완료

    //주차 현황 함수
    public RecentCar recentCar() throws ServerErrorException{
        Log.e(TAG,"RecentCar");

        JSONObject result;

        final String Recent_URL = "http://app.parkstem.com/api/car_recent.php";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);

        try {
            ConnectThread thread = new ConnectThread(Recent_URL, hashMap);
            result = thread.getResult();

            RecentCar recentcar = new RecentCar();
            recentcar.local_id = result.getString("local_id");
            recentcar.in_date = Essentials.stringToCalendar(result.getString("in_date"));
            recentcar.out_date = Essentials.stringToCalendar(result.getString("out_date"));
            recentcar.total = result.getString("total");
            return recentcar;
        }  catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        }

        /*RecentCar recentCar = new RecentCar();
        recentCar.local_id = "132654";
        recentCar.in_date = Calendar.getInstance();
        recentCar.out_date = Calendar.getInstance();
        recentCar.total = 202200;

        return recentCar;*/
    } //확인완료


    //주차장정보 함수


    /**이 함수는 data형식으로 값을 받아온다
     * 확인 필요
     * **/
    public ParkInfo parkInfo(final String local_id) throws ServerErrorException{
        Log.e(TAG,"parkInfo : " + local_id);

        if(local_id.equals("")) {
            return new ParkInfo();
        }

        String msg;
        final String Parkinfo_URL = "http://app.parkstem.com/api/park_info.php";
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
                parkinfo.new_address = jdata.getString("new_address");
                parkinfo.short_address = jdata.getString("short_address");
                parkinfo.local_phone = jdata.getString("local_phone");
                parkinfo.local_photo1 = jdata.getString("local_photo1");
                parkinfo.local_photo2 = jdata.getString("local_photo2");
                parkinfo.free_time = jdata.getInt("free_time");
                parkinfo.park_price = jdata.getString("park_price");
                parkinfo.park_price_time = jdata.getInt("park_price_time");
                parkinfo.base_minute = jdata.getInt("base_minute");
                parkinfo.base_price = jdata.getString("base_price");
                return parkinfo;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), "주차장 정보를 불러오는데 오류가 발생했습니다" + msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    } //확인완료

    /**mycar에서 2자리숫자+한글 글자 하나+4자리 숫자를 조합했을 때 앞의 두 자리 숫자만 저장되는 경우가 많음**/
    //차량관리 함수
    public CarLists CarRegister(final String mycar) throws ServerErrorException{
        Log.e(TAG,"listOfCar");

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

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    CarInfo carInfo = new CarInfo();
                    //carInfo.idx = jdata.getInt("idx");
                    carInfo.uniqueID = jdata.getString("uniqueID");
                    //carInfo.sort = jdata.getInt("sort");
                    carInfo.mycar = jdata.getString("mycar");
                    carInfo.reg_date = Essentials.stringToCalendar(jdata.getString("reg_date"));
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
    } //확인완료

    /**이 부분의 data값은 carNumber, sort, regdate만이 존재하며
     * 나머지는 데이터가 존재하지 않고 mycar로 값을 받아오지 않습니다.
     * 밑의 car_sort도 마찬가지
     * 보류
     * @return
     * @throws ServerErrorException
     */
    public CarLists listOfCar() throws ServerErrorException{
        Log.e(TAG,"listOfCar");

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

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    CarInfo carInfo = new CarInfo();
                    carInfo.idx = jdata.getInt("idx");
                    //carInfo.uniqueID = jdata.getString("uniqueID");
                    carInfo.sort = jdata.getInt("sort");
                    carInfo.mycar = jdata.getString("carNumber");
                    carInfo.reg_date = Essentials.stringToCalendar(jdata.getString("regdate"));
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
    } //확인완료

    public CarLists priorityCar(final String index) throws ServerErrorException{
        Log.e(TAG,"priorityCar : " + index);

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

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    CarInfo carInfo = new CarInfo();
                    //carInfo.idx = jdata.getInt("idx");
                    //carInfo.uniqueID = jdata.getString("uniqueID");
                    carInfo.sort = jdata.getInt("sort");
                    carInfo.mycar = jdata.getString("carNumber");
                    carInfo.reg_date = Essentials.stringToCalendar(jdata.getString("regdate"));
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
    } //확인완료

    public void deleteCar(final String mycar) throws ServerErrorException{
        Log.e(TAG,"deleteCar : " + mycar);

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
    } //확인완료

    public CarIn carIn() throws ServerErrorException{
        Log.e(TAG,"carIn");

        String msg;
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
            if(result.getInt("res")==1){
                CarIn carin = new CarIn();
                Log.d("ServerClient", msg);

                carin.itemTotalCount = result.getInt("itemTotalCount");
                carin.pageCount = result.getInt("pageCount");

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    CarInData carInData = new CarInData();
                    carInData.gubun = jdata.getInt("gubun");
                    carInData.carNumber = jdata.getString("carNumber");
                    carInData.indate = Essentials.stringToCalendar(jdata.getString("indate"));
                    carInData.local_id = jdata.getString("local_id");
                    carin.data.add(carInData);
                }
                return carin;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    } //확인불가 - data가 비어있음

    public CarOut carOut() throws ServerErrorException {
        Log.e(TAG,"carOut");

        String msg;
        final String CAROUT_URL = "http://app.parkstem.com/api/car_out.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
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
            if (result.getInt("res") == 1) {
                CarOut carOut = new CarOut();
                Log.d("ServerClient", msg);

                carOut.itemTotalCount = result.getInt("itemTotalCount");
                carOut.pageCount = result.getInt("pageCount");

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    CarOutData carOutData = new CarOutData();
                    carOutData.gubun = jdata.getInt("gubun");
                    carOutData.carNumber = jdata.getString("carNumber");
                    carOutData.outdate = Essentials.stringToCalendar(jdata.getString("outdate"));
                    carOutData.local_id = jdata.getString("local_id");
                    carOut.data.add(carOutData);
                }
                return carOut;
            } else {
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    } //확인불가 - data가 비어있음

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
        ci1.idx = 1234;
        ci1.card_name = "나라사랑카드";
        CardInfo ci2 = new CardInfo();
        ci2.reg_date = Calendar.getInstance();
        ci2.idx = 134;
        ci2.card_name = "나라사랑카드22";


        ArrayList<CardInfo> list = new ArrayList<>();
        list.add(ci1);
        list.add(ci2);

        cardLists.data = list;
        return cardLists;
    }
    /** card_list 와 card_sort의 card data에서 sort데이터가 들어오지 않음**/
    public CardList cardList() throws ServerErrorException {
        String msg;
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
            if(result.getInt("res")==1){
                CardList cardList = new CardList();
                Log.d("ServerClient", msg);
                cardList.itemTotalCount= result.getInt("itemTotalCount");
                cardList.pageCount= result.getInt("pageCount");

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    CardInfo cardInfo = new CardInfo();
                    cardInfo.idx = jdata.getInt("idx");
                    cardInfo.sort = jdata.getInt("sort");
                    cardInfo.card_name = jdata.getString("card_name");
                    cardInfo.reg_date = Essentials.stringToCalendar(jdata.getString("reg_date"));
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
    } //확인완료

    public CardList cardSort(final String idx) throws ServerErrorException{
        String msg;
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
            if(result.getInt("res")==1){
                CardList cardList = new CardList();
                Log.d("ServerClient", msg);
                cardList.itemTotalCount= result.getInt("itemTotalCount");
                cardList.pageCount= result.getInt("pageCount");

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    CardInfo cardInfo = new CardInfo();
                    cardInfo.idx = jdata.getInt("idx");
                    //cardInfo.sort = jdata.getInt("sort");
                    cardInfo.card_name = jdata.getString("card_name");
                    cardInfo.reg_date = Essentials.stringToCalendar(jdata.getString("reg_date"));
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
    } //확인완료

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
    } //확인불가 - 실패할 수도 있어서 못하겠다

    public ParkHistoryList parkHistory() throws ServerErrorException{
        Log.e(TAG,"parkHistory");

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
                ParkHistoryList paymentlist = new ParkHistoryList();
                paymentlist.itemTotalCount = result.getInt("itemTotalCount");
                paymentlist.pageCount = result.getInt("pageCount");

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    ParkHistory pm = new ParkHistory();
                    pm.card_name = jdata.getString("card_name");
                    pm.local_id = jdata.getString("local_id");
                    pm.local_name = jdata.getString("local_name");
                    pm.in_date = Essentials.stringToCalendar(jdata.getString("in_date"));
                    pm.out_date = Essentials.stringToCalendar(jdata.getString("out_date"));
                    pm.pay_date = Essentials.stringToCalendar(jdata.getString("pay_date"));
                    pm.cal_time = jdata.getString("cal_time");
                    pm.price = jdata.getString("price");
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
    } //확인완료

    public TicketPurchaseList ticketPurchase() throws ServerErrorException{
        Log.e(TAG,"ticketPurchase : " + uniqueID);

        String msg;
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
            if(result.getInt("res")==1){
                TicketPurchaseList paymentinfo = new TicketPurchaseList();
                Log.d("ServerClient", msg);
                paymentinfo.itemTotalCount = result.getInt("itemTotalCount");
                paymentinfo.pageCount = result.getInt("pageCount");

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    TicketPurchase ticketPurchase = new TicketPurchase();
                    ticketPurchase.idx = jdata.getInt("idx");
                    ticketPurchase.gubun = jdata.getInt("gubun");
                    ticketPurchase.ticket_idx = jdata.getInt("ticket_idx");
                    ticketPurchase.local_id = jdata.getString("local_id");
                    ticketPurchase.card_name = jdata.getString("card_name");
                    ticketPurchase.ticket_name = jdata.getString("ticket_name");
                    ticketPurchase.original_price = jdata.getString("original_price");
                    ticketPurchase.price = jdata.getString("price");
                    ticketPurchase.start_date = Essentials.stringToCalendar(jdata.getString("start_date"));
                    ticketPurchase.end_date = Essentials.stringToCalendar(jdata.getString("end_date"));
                    ticketPurchase.pay_date = Essentials.stringToCalendar(jdata.getString("pay_date"));
                    ticketPurchase.term = jdata.getString("term");
                    ticketPurchase.term_name = jdata.getString("term_name");
                    ticketPurchase.available_time = jdata.getString("available_time");
                    ticketPurchase.user_name = jdata.getString("user_name");
                    ticketPurchase.user_email = jdata.getString("user_email");
                    ticketPurchase.user_phone = jdata.getString("user_phone");
                    ticketPurchase.allow = jdata.getString("allow").equals("Y");
                    ticketPurchase.ticket_used = jdata.getString("ticket_used");
                    paymentinfo.data.add(ticketPurchase);
                }
                return paymentinfo;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    } //확인완료

    //티켓 관련 함수
    private TicketLists listOfAllTickets(Calendar now) throws ServerErrorException {
        Log.e(TAG,"listOfAllTickets : " + uniqueID);

        String msg;
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
            if(result.getInt("res")==1){
                TicketLists ticketLists = new TicketLists();
                Log.d("ServerClient", msg);
                ticketLists.itemTotalCount = result.getInt("itemTotalCount");
                ticketLists.pageCount = result.getInt("pageCount");

                JSONArray jarray = result.getJSONArray("data");
                for(int i=0;i<jarray.length();i++) {
                    JSONObject jdata = jarray.getJSONObject(i);
                    Ticket ticket = new Ticket();
                    ticket.idx = jdata.getInt("idx");
                    ticket.local_id = jdata.getString("local_id");
                    ticket.gubun = jdata.getInt("gubun");
                    ticket.ticket_name = jdata.getString("ticket_name");
                    ticket.local_address = jdata.getString("local_address");
                    ticket.term = jdata.getString("term");
                    ticket.term_name = jdata.getString("term_name");
                    ticket.available_time = jdata.getInt("available_time");
                    ticket.start_date = Essentials.stringToCalendar(jdata.getString("start_date"));
                    ticket.end_date = Essentials.stringToCalendar(jdata.getString("end_date"));
                    ticket.start_available_time = jdata.getString("start_available_time");
                    ticket.end_available_time = jdata.getString("end_available_time");
                    ticket.original_price = jdata.getString("original_price");
                    ticket.price = jdata.getString("price");
                    ticket.allow = jdata.getString("allow").equals("Y");
                    ticket.regdate = Essentials.stringToCalendar(jdata.getString("regdate"));
                    ticketLists.data.add(ticket);
                }
                return ticketLists;
            }
            else{
                throw new ServerErrorException(result.getInt("res"),msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    } //확인완료


    public TicketLists listOfTicket(Calendar now) throws ServerErrorException{
        TicketLists allTickets;

        try {
            allTickets = listOfAllTickets(now);
        } catch (ServerErrorException ex) {
            throw ex;
        }

        ArrayList<Ticket> tickets = allTickets.data;
        for(int i = 0;i<tickets.size();i++) {
            if(tickets.get(i).gubun == Ticket.LONG_TICKET_GUBUN) {
                tickets.remove(i--);
            }
        }

        return allTickets;
    } //확인완료

    public LongTicketLists listOfLongTicket(Calendar now) throws ServerErrorException{
        LongTicketLists longTicketLists = new LongTicketLists();
        TicketLists allTickets;

        try {
            allTickets = listOfAllTickets(now);

            longTicketLists.itemTotalCount = allTickets.itemTotalCount;
            longTicketLists.pageCount = allTickets.pageCount;
        } catch (ServerErrorException ex) {
            throw ex;
        }

        ArrayList<Ticket> tickets = allTickets.data;
        for(int i = 0;i<tickets.size();i++) {
            if(tickets.get(i).gubun == Ticket.SHORT_TICEKT_GUBUN) {
                tickets.remove(i--);
            }
        }

        longTicketLists.data = tickets;

        return longTicketLists;
    } //확인완료

    public TicketInfo ticketInfo(final String idx) throws ServerErrorException{
        String msg;
        final String T_INFO_URL = "http://app.parkstem.com/api/ticket_info.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID",uniqueID);
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
            msg = result.getString("msg");
            if(result.getInt("res")==1){
                TicketInfo ticketInfo = new TicketInfo();
                Log.d("ServerClient",msg);
                ticketInfo.idx = result.getInt("idx");
                ticketInfo.local_id = result.getString("local_id");
                ticketInfo.gubun = result.getInt("gubun");
                ticketInfo.price = result.getString("price");
                ticketInfo.ticket_name = result.getString("ticket_name");
                ticketInfo.card_use = result.getString("card_use").equals("Y");
                ticketInfo.certification = result.getString("certification").equals("Y");
                return ticketInfo;
            }
            else{
                throw new ServerErrorException(result.getInt("res"), msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    } //확인완료

    public void ticketInfoRegister(final String gubun, final String idx, final String user_name, final String user_phone, final String user_email, final Calendar date) throws ServerErrorException{
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
                if(Integer.parseInt(gubun) == Ticket.LONG_TICKET_GUBUN) {
                    String str = Essentials.calendarToDateWithBar(date);
                    hashMap.put("start_date", str);
                }
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
    /*public void mobileCertification() throws ServerErrorException{
        String msg;
        final String CertIn_URL = "http://app.parkstem.com/api/kmcis_start.php?uniqueID=" + uniqueID;
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
    }*/


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


    public String hipass(final String idx) throws ServerErrorException{
        String msg;
        String contents;
        final String Hipass_URL = "http://app.parkstem.com/api/help.php\n";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("idx", idx);
                result = connect(hashMap, Hipass_URL);
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

    public void push(final String pushYN) throws ServerErrorException{
        String msg;
        final String Push_URL = "http://app.parkstem.com/api/push_yn.php";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("uniqueID", uniqueID);
                hashMap.put("pushYN", pushYN);
                result = connect(hashMap, Push_URL);
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
            }
            else{
                throw new ServerErrorException();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException();
        }
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public boolean isUserCertification() {
        return userCertification;
    }

    public boolean isUserPush() {
        return userPush;
    }

    //Classes
    public class Login{

        public boolean certification;
        public String name;
        public String email;
        public String phone;
        public boolean pushYN;
    }
    public class MemberInfo{
        public String name;
        public String email;
        public String mobile;
        public boolean certification;
        public boolean pushYN;
    }
    public class DashBoard{

        public String mycar;
        public String mycard;
        public int itemTotalCount;
        public int pageCount;
        public boolean hipass;
        public ArrayList<Dash> data = new ArrayList<>();
    }
    public class Dash{
        public String card_name;
        public String price;
        public Calendar pay_date;
    }
    public class RecentCar{

        public String local_id;
        public Calendar in_date;
        public Calendar out_date;
        public String total;
    }
    public static class ParkInfo{

        public String local_id = "";
        public String local_name = "";
        public String local_content = "";
        public String local_address = "";
        public String new_address = "";
        public String short_address = "";
        public String local_phone = "";
        public String local_photo1 = "";
        public String local_photo2 = "";
        public int free_time = 0;
        public String park_price = "0";
        public int park_price_time = 0;
        public int base_minute = 0;
        public String base_price = "0";
    }
    public class CarLists{

        public int itemTotalCount;
        public int pageCount;
        public ArrayList<CarInfo> data = new ArrayList<>();
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
        public ArrayList<CarInData> data = new ArrayList<>();
    }
    public class CarOut{
        public int itemTotalCount;
        public int pageCount;
        public ArrayList<CarOutData> data = new ArrayList<>();
    }
    public class CarInData{
        public Calendar indate;
        public String local_id;
        public String carNumber;
        public int gubun;
    }
    public class CarOutData{
        public Calendar outdate;
        public String local_id;
        public String carNumber;
        public int gubun;
    }
    public class CardList{

        public int itemTotalCount;
        public int pageCount;
        public ArrayList<CardInfo> data = new ArrayList<>();
    }
    public class CardInfo{
        public int idx;
        public int sort;
        public String card_name;
        public Calendar reg_date;
    }
    public class ParkHistoryList {

        public int itemTotalCount;
        public int pageCount;
        public ArrayList<ParkHistory> data = new ArrayList<>();
    }
    public class ParkHistory {
        public String card_name;
        public String local_id;
        public String local_name;
        public Calendar in_date;
        public Calendar out_date;
        public Calendar pay_date;
        public String price;
        public String cal_time;
    }
    public class TicketPurchaseList {
        public int itemTotalCount;
        public int pageCount;
        public ArrayList<TicketPurchase> data = new ArrayList<>();
    }
    public class TicketPurchase {
        public int idx;
        public int gubun;
        public String local_id;
        public String card_name;
        public String original_price;
        public String price;
        public Calendar start_date;
        public Calendar end_date;
        public Calendar pay_date;
        public String term;
        public String term_name;
        public String available_time;
        public String user_name;
        public String user_email;
        public String user_phone;
        public boolean allow;
        public String ticket_name;
        public int ticket_idx;
        public String ticket_used;
    }

    public class TicketLists{

        public int itemTotalCount;
        public int pageCount;
        public ArrayList<Ticket> data = new ArrayList<>();
    }
    public static class Ticket implements Parcelable {


        public static final int LONG_TICKET_GUBUN = 2;
        public static final int SHORT_TICEKT_GUBUN = 1;
        public int idx;

        public String local_id;
        public String ticket_name;
        public String term;
        public String term_name;
        public int available_time;
        public int gubun;
        public String original_price;
        public String price;
        public Calendar start_date;
        public Calendar end_date;
        public Calendar regdate;
        public boolean allow;
        public String local_address;
        public String start_available_time;
        public String end_available_time;
        public Ticket() {

        }

        public Ticket(Parcel in) {
            String[] data = new String[16];

            in.readStringArray(data);
            this.idx = Integer.parseInt(data[0]);
            this.local_id = data[1];
            this.ticket_name = data[2];
            this.term = data[3];
            this.term_name = data[4];
            this.available_time = Integer.parseInt(data[5]);
            this.gubun = Integer.parseInt(data[6]);
            this.original_price = data[7];
            this.price = data[8];
            this.start_date = Calendar.getInstance();
            this.start_date.setTimeInMillis(Long.parseLong(data[9]));
            if(start_date.getTimeInMillis() == 0)
                start_date = null;

            this.end_date = Calendar.getInstance();
            this.end_date.setTimeInMillis(Long.parseLong(data[10]));
            if(end_date.getTimeInMillis() == 0)
                end_date = null;

            this.regdate = Calendar.getInstance();
            this.regdate.setTimeInMillis(Long.parseLong(data[11]));
            if(regdate.getTimeInMillis() == 0)
                regdate = null;
            this.allow = Boolean.parseBoolean(data[12]);

            this.local_address = data[13];
            this.start_available_time = data[14];
            this.end_available_time = data[15];
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if(start_date == null) {
                start_date = Calendar.getInstance();
                start_date.setTimeInMillis(0);
            }

            if(end_date == null) {
                end_date = Calendar.getInstance();
                end_date.setTimeInMillis(0);
            }

            if(regdate == null) {
                regdate = Calendar.getInstance();
                regdate.setTimeInMillis(0);
            }

            dest.writeStringArray(new String[] {
                    this.idx + "",
                    this.local_id,
                    this.ticket_name,
                    this.term + "",
                    this.term_name,
                    this.available_time + "",
                    this.gubun + "",
                    this.original_price,
                    this.price,
                    this.start_date.getTimeInMillis() + "",
                    this.end_date.getTimeInMillis() + "",
                    this.regdate.getTimeInMillis() + "",
                    this.allow + "",
                    this.local_address,
                    this.start_available_time,
                    this.end_available_time});
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public Ticket createFromParcel(Parcel in) {
                return new Ticket(in);
            }

            public Ticket[] newArray(int size) {
                return new Ticket[size];
            }
        };

    }


    public class LongTicketLists{
        public int itemTotalCount;
        public int pageCount;
        public ArrayList<Ticket> data = new ArrayList<>();
    }

    public class TicketInfo{
        public int idx;
        public String local_id;
        public int gubun;
        public String price;
        public String ticket_name;
        public boolean card_use;
        public boolean certification;
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


    private class ConnectThread extends Thread {
        private JSONObject result;
        private String urlStr = "";
        private HashMap<String, String> data = new HashMap<>();

        private ConnectThread(String urlStr, HashMap<String, String> data) {
            this.urlStr = urlStr;
            this.data = data;

            start();
        }

        @Override
        public void run() {
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
                for(String key : data.keySet()) {
                    json.put(key, data.get(key));
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

                Log.e("jsonStr",jsonStr);

                Pattern pattern = Pattern.compile("[{].*[}]");
                Matcher matcher = pattern.matcher(jsonStr);

                if(matcher.find())
                    result = new JSONObject(matcher.group());
                else
                    result = null;
            } catch (Exception ex) {
                ex.printStackTrace();
                result = null;
            }
        }

        public JSONObject getResult() throws ServerErrorException{
            try {
                join();
                if(result == null)
                    throw new ServerErrorException(0, "JSON ERROR");
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                throw new ServerErrorException(0, "Thread Join ERROR");
            }

            return result;
        }
    }


    /*private JSONObject connect(HashMap<String, String> hashMap, String urlin, String urlout) {
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
    */

/* 암호화
     public static String getSHA256(String str) {
         String rtnSHA = "";

         try {
             MessageDigest sh = MessageDigest.getInstance("SHA-256");
             sh.update(str.getBytes());
             byte byteData[] = sh.digest();
             StringBuffer sb = new StringBuffer();

             for (int i = 0; i < byteData.length; i++) {
                 sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
             }
             rtnSHA = sb.toString();

         } catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
             rtnSHA = null;
         }
         return rtnSHA;
     }*/

/*     public void regByKakao(final String name, final String email, final String mobile, final String nickName, final String kakaoID) throws ServerErrorException {
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
                 hashMap.put("memberGubun", "kakao");
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
             if (result.getInt("res") == 1) {
                 Log.d("ServerClient", msg);
                 uniqueID = result.getString("uniqueID");
             } else {
                 throw new ServerErrorException(result.getInt("res"), msg);
             }
         } catch (JSONException ex) {
             ex.printStackTrace();
             throw new ServerErrorException();
         }
     }*/

/*     public void regByNaver(final String name, final String email, final String mobile, final String nickName, final String naverID) throws ServerErrorException {
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
                 hashMap.put("memberGubun", "naver");
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
             if (result.getInt("res") == 1) {
                 Log.d("ServerClient", msg);
                 uniqueID = result.getString("uniqueID");
             } else {
                 throw new ServerErrorException(result.getInt("res"), msg);
             }
         } catch (JSONException ex) {
             ex.printStackTrace();
             throw new ServerErrorException();
         }
     }*/

/*     public void regByFacebook(final String name, final String email, final String mobile, final String nickName, final String facebookID) throws ServerErrorException {
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
                 hashMap.put("memberGubun", "facebook");
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
             if (result.getInt("res") == 1) {
                 Log.d("ServerClient", msg);
                 uniqueID = result.getString("uniqueID");
             } else {
                 throw new ServerErrorException(result.getInt("res"), msg);
             }
         } catch (JSONException ex) {
             ex.printStackTrace();
             throw new ServerErrorException();
         }
     }*/

}
