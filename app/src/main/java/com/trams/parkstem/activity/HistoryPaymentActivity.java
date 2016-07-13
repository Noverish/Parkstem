package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.LinearLayout;

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
            for(ServerClient.Payment payment: list.data){
                HistoryPaymentView historyPaymentView = new HistoryPaymentView(this, payment);
                content.addView(historyPaymentView);
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }
    }
}
