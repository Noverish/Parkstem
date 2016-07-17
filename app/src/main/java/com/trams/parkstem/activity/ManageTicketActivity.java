package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.LinearLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.TicketMobileView;

/**
 * Created by Noverish on 2016-07-08.
 */
public class ManageTicketActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ticket);
        setSearchEnable(true);
        setToolbarTitle("주차권 관리");

        LinearLayout content = (LinearLayout) findViewById(R.id.activity_manage_ticket_layout);

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
