package com.trams.parkstem.server;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.trams.parkstem.login.OnLoginSuccessListener;
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
    private String memberGubun;
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

    //회원가입 및 로그인 관련 함수
    public void login(String memberGubun, String parkstemID, String parkstemPW, String token) throws ServerErrorException {
        String urlStr = "http://app.parkstem.com/api/member_login.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("memberGubun", memberGubun);
        hashMap.put("parkstemID", parkstemID);
        hashMap.put("parkstemPW", parkstemPW);
        hashMap.put("token", token);
        if(memberGubun.equals(OnLoginSuccessListener.FACEBOOK)) {
            hashMap.put("facebookID", parkstemID);
        } else if(memberGubun.equals(OnLoginSuccessListener.KAKAO)) {
            hashMap.put("kakaoID", parkstemID);
        } else if(memberGubun.equals(OnLoginSuccessListener.NAVER)) {
            hashMap.put("naverID", parkstemID);
        }

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            uniqueID = result.getString("uniqueID");
            userName = result.getString("name");
            userEmail = result.getString("email");
            userMobile = result.getString("mobile");
            userPush = result.getString("pushYN").equals("Y");
            userCertification = result.getString("certification").equals("Y");

            this.memberGubun = memberGubun;
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public void register(String name, String email, String mobile, String nickName, String parkstemID, String parkstemPW, String token) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/member_join.php";

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
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            uniqueID = result.getString("uniqueID");
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public MemberInfo memberInfo() throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/member_info.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            MemberInfo memberInfo = new MemberInfo();
            userName = memberInfo.name = result.getString("name");
            userEmail = memberInfo.email = result.getString("email");
            userMobile = memberInfo.mobile = result.getString("mobile");
            userPush = memberInfo.pushYN = result.getString("pushYN").equals("Y");
            userCertification = memberInfo.certification = result.getString("certification").equals("Y");
            return memberInfo;
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public void push(String pushYN) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/push_yn.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID", uniqueID);
        hashMap.put("pushYN", pushYN);

        try {
            ConnectThread.connect(urlStr, hashMap);

            this.userPush = pushYN.equals("Y");
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public void memberDelete() throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/member_del.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);

        try {
            ConnectThread.connect(urlStr, hashMap);

        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }


    //비밀번호, 이메일 찾기 또는 변경
    public void findPassword(String parkstemID) throws ServerErrorException {
        String urlStr = "http://app.parkstem.com/api/findpw.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("parkstemID",parkstemID);

        try {
            ConnectThread.connect(urlStr, hashMap);

        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public void changePasword(String currentPW, String newPW, String newPWCheck) throws ServerErrorException {
        String urlStr = "http://app.parkstem.com/api/passwd_update.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);
        hashMap.put("current_parkstemPW",currentPW);
        hashMap.put("parkstemPW",newPW);
        hashMap.put("parkstemPW_check",newPWCheck);

        try {
            ConnectThread.connect(urlStr, hashMap);

        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public void changeEmail(String memberGubun, String email) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/email_update.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);
        hashMap.put("memberGubun",memberGubun);
        hashMap.put("email",email);

        try {
            ConnectThread.connect(urlStr, hashMap);

        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    //회원 정보관리 함수
    public DashBoard dashboard() throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/dashboard.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);


            DashBoard dashboard = new DashBoard();
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
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public void hipassOn(String hipass) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/hipass.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);
        hashMap.put("hipass", hipass);

        try {
            ConnectThread.connect(urlStr, hashMap);

        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }


    //주차 현황 함수
    public RecentCar recentCar() throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/car_recent.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            RecentCar recentcar = new RecentCar();
            recentcar.local_id = result.getString("local_id");
            recentcar.in_date = Essentials.stringToCalendar(result.getString("in_date"));
            recentcar.out_date = Essentials.stringToCalendar(result.getString("out_date"));
            recentcar.total = result.getString("total");
            return recentcar;
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }


    //주차장정보 함수
    public ParkInfo parkInfo(String local_id) throws ServerErrorException {
        String urlStr = "http://app.parkstem.com/api/park_info.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("local_id",local_id);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            ParkInfo parkinfo = new ParkInfo();
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
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }


    //차량관리 함수
    public CarLists CarRegister(String mycar) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/car_reg.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);
        hashMap.put("mycar", mycar);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            CarLists carLists = new CarLists();;
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
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public CarLists listOfCar() throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/car_list.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            CarLists carLists = new CarLists();;
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
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public CarLists priorityCar(String index) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/car_sort.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);
        hashMap.put("idx",index);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            CarLists carLists = new CarLists();;
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
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public void deleteCar(String mycar) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/car_del.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mycar",mycar);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }


    //카드 관련 함수
    public CardList cardList() throws ServerErrorException {
        String urlStr = "http://app.parkstem.com/api/card_list.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID", uniqueID);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            CardList cardList = new CardList();
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
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public CardList cardSort(String idx) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/card_sort.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID", uniqueID);
        hashMap.put("idx", idx);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            CardList cardList = new CardList();
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
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public void cardDelete(String idx) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/card_del.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID", uniqueID);
        hashMap.put("idx", idx);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public ParkHistoryList parkHistory() throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/pay_list.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID", uniqueID);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

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
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public TicketPurchaseList ticketPurchase() throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/ticket_buy_list.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID", uniqueID);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            TicketPurchaseList paymentinfo = new TicketPurchaseList();
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
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public void changeCardName(long idx, String newName) throws ServerErrorException {
        String urlStr = "http://app.parkstem.com/api/ticket_list.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);
        hashMap.put("idx", idx + "");
        hashMap.put("card_name", newName);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }


    //티켓 관련 함수
    private TicketLists listOfAllTickets() throws ServerErrorException {
        String urlStr = "http://app.parkstem.com/api/ticket_list.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            TicketLists ticketLists = new TicketLists();
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
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public TicketLists listOfTicket() throws ServerErrorException{
        TicketLists allTickets;

        try {
            allTickets = listOfAllTickets();
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
    }

    public LongTicketLists listOfLongTicket() throws ServerErrorException{
        LongTicketLists longTicketLists = new LongTicketLists();
        TicketLists allTickets;

        try {
            allTickets = listOfAllTickets();

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
    }

    public TicketInfo ticketInfo(String idx) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/ticket_info.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);
        hashMap.put("idx",idx);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            TicketInfo ticketInfo = new TicketInfo();
            ticketInfo.idx = result.getInt("idx");
            ticketInfo.local_id = result.getString("local_id");
            ticketInfo.gubun = result.getInt("gubun");
            ticketInfo.price = result.getString("price");
            ticketInfo.ticket_name = result.getString("ticket_name");
            ticketInfo.card_use = result.getString("card_use").equals("Y");
            ticketInfo.certification = result.getString("certification").equals("Y");
            return ticketInfo;
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public void ticketInfoRegister(String gubun, String idx, String user_name, String user_phone, String user_email, Calendar startDate, Calendar endDate) throws ServerErrorException {
        String urlStr = "http://app.parkstem.com/api/ticket_pay.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uniqueID",uniqueID);
        hashMap.put("gubun",gubun);
        hashMap.put("idx",idx);
        hashMap.put("user_name",user_name);
        hashMap.put("user_phone",user_phone);
        hashMap.put("user_email",user_email);
        if(Integer.parseInt(gubun) == Ticket.LONG_TICKET_GUBUN) {
            String startDateStr = Essentials.calendarToDateWithBar(startDate);
            hashMap.put("start_date", startDateStr);

            String endDateStr = Essentials.calendarToDateWithBar(endDate);
            hashMap.put("end_date", endDateStr);
        }

        try {
            ConnectThread.connect(urlStr, hashMap);

        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    //약관
    public String clause(String idx) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/clause.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("idx", idx);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            return result.getString("contents");
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }

    public String hipassClause(String idx) throws ServerErrorException{
        String urlStr = "http://app.parkstem.com/api/help.php";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("idx", idx);

        try {
            JSONObject result = ConnectThread.connect(urlStr, hashMap);

            return result.getString("contents");
        } catch (ServerErrorException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "JSON ERROR");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(0, "알 수 없는 오류");
        }
    }



    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getMemberGubun() {
        return memberGubun;
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
    public static class ServerErrorException extends Exception {
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

    private static class ConnectThread extends Thread {
        private JSONObject result;
        private String urlStr = "";
        private HashMap<String, String> data = new HashMap<>();

        private ConnectThread(String urlStr, HashMap<String, String> data) {
            this.urlStr = urlStr;
            this.data = data;

            Log.d("ServerClient",urlStr);
            Log.d("Reqeust", (new JSONObject(data)).toString());
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

                ArrayList<String> strs = (ArrayList<String>) Essentials.splitEqually(jsonStr, 1000);

                for(String str : strs)
                    Log.d("Response",str);

                Pattern pattern = Pattern.compile("[{][\\s\\S]*[}]");
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

        private JSONObject getResult() throws ServerErrorException{
            try {
                join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                throw new ServerErrorException(0, "Thread Join ERROR");
            }

            if(result == null)
                throw new ServerErrorException(0, "JSON ERROR");
            else {
                int res;
                try {
                    res = result.getInt("res");
                } catch (JSONException ex) {
                    throw new ServerErrorException(0, "JSON ERROR - no res");
                }

                String msg;
                try {
                    msg = result.getString("msg");
                } catch (JSONException ex) {
                    throw new ServerErrorException(0, "JSON ERROR - no msg");
                }

                if(res == 0) {
                    throw new ServerErrorException(0, msg);
                }
            }

            return result;
        }

        public static JSONObject connect(String urlStr, HashMap<String, String> data) throws ServerErrorException {
            ConnectThread thread = new ConnectThread(urlStr, data);
            thread.start();
            return thread.getResult();
        }
    }
}
