package com.trams.parkstem.others;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.trams.parkstem.R;

import java.util.Calendar;

/**
 * Created by JaeHyo on 2016-07-13.
 */
public class Essentials {

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

        aDialog.setTitle(title); //타이틀바 제목
        aDialog.setView(layout); //dialog.xml 파일을 뷰로 셋팅
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

    //String값으로 받은 pay_date등을 Calendar로 변환
    public static Calendar stringToCalendar(final String date){
        String[] dates;
        Calendar calendar;

        dates = date.split("\\D");
        calendar = Calendar.getInstance();
        if(dates.length == 3){
            calendar.set(Integer.parseInt(dates[0]),Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
            return calendar;
        }
        else if(dates.length == 5){
            calendar.set(Integer.parseInt(dates[0]),Integer.parseInt(dates[1]), Integer.parseInt(dates[2]),Integer.parseInt(dates[3]),Integer.parseInt(dates[4]));
            return calendar;
        }
        else if(dates.length == 6){
            calendar.set(Integer.parseInt(dates[0]),Integer.parseInt(dates[1]), Integer.parseInt(dates[2]),Integer.parseInt(dates[3]),Integer.parseInt(dates[4]), Integer.parseInt(dates[5]));
            return calendar;
        } else {
            Log.e("ERROR","stringToCalendar Error - datas.lenght is " + dates.length + " [" + date + "]");
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(0);
            return calendar;
        }
    }
}
