package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.HistoryPaymentView;
import com.trams.parkstem.view.TicketMobileView;

/**
 * Created by JaeHyo on 2016-07-06.
 */
public class TicketMobileListActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_mobile_list);
        setSearchEnable(true);
        setToolbarTitle("모바일 주차권");

        LinearLayout content = (LinearLayout) findViewById(R.id.activity_ticket_mobile_list_layout);

        ServerClient.TicketLists list;

        try {
            list = ServerClient.getInstance().listOfTicket();
            int count=0;
            for(ServerClient.Ticket ticket: list.data){
                TicketMobileView ticketMobileView = new TicketMobileView(this, ticket);

                content.addView(ticketMobileView);
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }

    }
}
