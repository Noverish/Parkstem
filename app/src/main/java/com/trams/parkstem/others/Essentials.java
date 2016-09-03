package com.trams.parkstem.others;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.server.ServerClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by JaeHyo on 2016-07-13.
 */
public class Essentials {
    public static final String WON_SYMBOL = (char) 0xffe6 + "";

    public static String numberWithComma(int number) {
        return numberWithComma(String.valueOf(number));
    }

    public static String numberWithComma(long number) {
        return numberWithComma(String.valueOf(number));
    }

    public static String numberWithComma(String number) {
        number = number.replaceAll(",","");

        for(int i=number.length() - 3 ; i>=1 ; i-=3){
            number = number.substring(0,i) + "," +number.substring(i);
        }
        return number;
    }

    public static String numberWithZero(int number) {
        return numberWithZero(String.valueOf(number));
    }

    public static String numberWithZero(long number) {
        return numberWithZero(String.valueOf(number));
    }

    public static String numberWithZero(String number) {
        if(number.length()==1)
            number = "0" + number;
        return number;
    }

    public static int numberWithOutComma(String number) {
        number = number.replaceAll(",","");
        return Integer.parseInt(number);
    }

    public static void makePopup(Context context, String title, String content) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //R.layout.dialog는 xml 파일명이고  R.id.popup은 보여줄 레이아웃 아이디
        View layout = inflater.inflate(R.layout.popup_layout, null);
        AlertDialog.Builder aDialog = new AlertDialog.Builder(context);

        aDialog.setView(layout); //dialog.xml 파일을 뷰로 셋팅
        aDialog.setTitle(title); //타이틀바 제목
        TextView contentTextView = (TextView) layout.findViewById(R.id.popup_layout_content);
        contentTextView.setText(content);

        //그냥 닫기버튼을 위한 부분
        aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //팝업창 생성
        AlertDialog ad = aDialog.create();
        ad.show();//보여줌!
    }

    public static void makeHipassPopup(Context context, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.hipass_popup, null);

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 223, context.getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, context.getResources().getDisplayMetrics());

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);

        ImageView btnDismiss = (ImageView) popupView.findViewById(R.id.hipass_popup_close);
        btnDismiss.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }
        });

        TextView content = (TextView) popupView.findViewById(R.id.hipass_popup_content);
        try {
            String str = ServerClient.getInstance().hipassClause("1");
            str = str.replaceAll("</div>","");
            str = str.replaceAll("<div>","\n");
            str = str.replaceAll("<br />","");
            str = str.replaceAll("&nbsp;"," ");

            content.setText(str);
            popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(context, ex.msg, Toast.LENGTH_SHORT).show();
        }

    }

    //String값으로 받은 pay_date등을 Calendar로 변환
    public static Calendar stringToCalendar(final String date){
        String[] dates;
        Calendar calendar;
        dates = date.replaceFirst("\\[\"","").split("[\\D]+");
        calendar = Calendar.getInstance();
        if(dates.length == 3){
            calendar.set(Integer.parseInt(dates[0]),Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]));
            return calendar;
        }
        else if(dates.length == 5){
            calendar.set(Integer.parseInt(dates[0]),Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]),Integer.parseInt(dates[3]),Integer.parseInt(dates[4]));
            return calendar;
        }
        else if(dates.length == 6){
            calendar.set(Integer.parseInt(dates[0]),Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]),Integer.parseInt(dates[3]),Integer.parseInt(dates[4]), Integer.parseInt(dates[5]));
            return calendar;
        } else {
//            Log.e("ERROR","stringToCalendar Error - datas.lenght is " + dates.length + " [" + date + "]");
            return null;
        }
    }

    public static String calendarToTime(Calendar calendar) {
        String amPm = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
        String hour = calendar.get(Calendar.HOUR) + "";
        String min = calendar.get(Calendar.MINUTE) + "";

        return hour + ":" + numberWithZero(min) + " " + amPm;
    }

    public static String calendarToDateWithDot(Calendar calendar) {
        String date = calendar.get(Calendar.YEAR) + ".";
        date += Essentials.numberWithZero(calendar.get(Calendar.MONTH) + 1) + ".";
        date += Essentials.numberWithZero(calendar.get(Calendar.DAY_OF_MONTH));

        return date;
    }

    public static String calendarToDateWithBar(Calendar calendar) {
        String date = calendar.get(Calendar.YEAR) + "-";
        date += Essentials.numberWithZero(calendar.get(Calendar.MONTH) + 1) + "-";
        date += Essentials.numberWithZero(calendar.get(Calendar.DAY_OF_MONTH));

        return date;
    }

    public static void alertParkState(Activity activity) {
        try {
            ServerClient.RecentCar recentCar = ServerClient.getInstance().recentCar();
            ServerClient.ParkInfo parkInfo = ServerClient.getInstance().parkInfo(recentCar.local_id);

            if(recentCar.in_date != null && recentCar.out_date == null) {
                new android.support.v7.app.AlertDialog.Builder(activity)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("차량 입차")
                        .setMessage(parkInfo.local_name + " " + Essentials.calendarToTime(recentCar.in_date) + " 입차")
                        .setPositiveButton("확인", null)
                        .show();
            } else if (recentCar.in_date != null){
                new android.support.v7.app.AlertDialog.Builder(activity)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("차량 출차")
                        .setMessage(parkInfo.local_name + " " + Essentials.calendarToTime(recentCar.out_date) + " 출차")
                        .setPositiveButton("확인", null)
                        .show();
            } else {

            }

        } catch (ServerClient.ServerErrorException error) {
            error.printStackTrace();
        }
    }

    public static void toastMessage(android.os.Handler handler, final Context context, final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void toastMessage(android.os.Handler handler, final Context context, final String message, final int textSize) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if(v != null)
                    v.setTextSize(textSize);
                toast.show();
            }
        });
    }

    public static List<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    public static String mobileNumberWithBar(String mobile) {
        mobile = mobile.replaceAll("[^\\d]","");

        String num1 = mobile.substring(0, 3);
        String num2 = mobile.substring(3, 7);
        String num3 = mobile.substring(7, 11);
        mobile = num1 + "-" + num2 + "-" + num3;

        return mobile;
    }
}
