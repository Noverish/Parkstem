package com.trams.parkstem.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.HistoryPaymentView;

import java.util.ArrayList;

/**
 * Created by Noverish on 2016-07-13.
 */
public class HistoryPaymentActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_payment);

        LinearLayout content = (LinearLayout) findViewById(R.id.activity_history_payment_content_layout);


        ServerClient.PaymentList list;

        try {
            list = ServerClient.getInstance().hipassPayment();
            int count=0;
            for(ServerClient.Payment payment: list.data){
                HistoryPaymentView historyPaymentView = new HistoryPaymentView(this, payment);

                if(count%2==0)
                    historyPaymentView.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_3));

                content.addView(historyPaymentView);

                if(count==0)
                    LayoutInflater.from(this).inflate(R.layout.history_payment_item_bar, content);

                count++;
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }
    }
}
