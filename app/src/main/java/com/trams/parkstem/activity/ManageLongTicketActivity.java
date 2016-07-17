package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.LinearLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.LongTicketMobileView;
import com.trams.parkstem.view.TicketMobileView;

/**
 * Created by Noverish on 2016-07-08.
 */
public class ManageLongTicketActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_long_ticket);
        setSearchEnable(true);
        setToolbarTitle("정기권 관리");

        LinearLayout content = (LinearLayout) findViewById(R.id.activity_manage_long_ticket_layout);

        ServerClient.LongTicketLists list;

        try {
            list = ServerClient.getInstance().listOfLongTicket();
            int count=0;
            for(ServerClient.Ticket ticket: list.data){
                LongTicketMobileView longTicketMobileView = new LongTicketMobileView(this, ticket);

                content.addView(longTicketMobileView);
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }
    }
}
