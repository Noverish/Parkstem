package com.trams.parkstem.others;

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
}
