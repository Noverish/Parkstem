package com.trams.parkstem.view;

import android.content.Context;
import android.widget.LinearLayout;

import com.trams.parkstem.server.ServerClient;

/**
 * Created by JaeHyo on 2016-07-13.
 */
public class HistoryPaymentView extends LinearLayout {
    ServerClient.PaymentList list;
    public HistoryPaymentView (Context context) {
        super(context);

        try {
            list = ServerClient.getInstance().hipassPayment();
        } catch (ServerClient.ServerErrorException ex) {

        }
    }
}
