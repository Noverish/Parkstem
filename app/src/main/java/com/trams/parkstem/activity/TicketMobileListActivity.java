package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.LongTicketMobileListView;
import com.trams.parkstem.view.TicketMobileListView;

/**
 * Created by JaeHyo on 2016-07-06.
 */
public class TicketMobileListActivity extends BaseBackSearchActivity {
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_mobile_list);
        setSearchEnable(true);
        setToolbarTitle("모바일 주차권");

        LinearLayout content = (LinearLayout) findViewById(R.id.activity_ticket_mobile_list_layout);

        ServerClient.TicketLists list;
        ServerClient.LongTicketLists longlist;


        try {
            list = ServerClient.getInstance().listOfTicket();

            for(ServerClient.Ticket ticket: list.data){
                TicketMobileListView ticketMobileListView = new TicketMobileListView(this, ticket);
                TextView buttonname = (TextView)ticketMobileListView.findViewById(R.id.ticket_mobile_item_button_name);
                buttonname.setText("사용가능");
                content.addView(ticketMobileListView);
            }

            longlist = ServerClient.getInstance().listOfLongTicket();

            for(ServerClient.Ticket ticket: longlist.data){
                LongTicketMobileListView longTicketMobileListView = new LongTicketMobileListView(this, ticket);
                TextView buttonname = (TextView)longTicketMobileListView.findViewById(R.id.long_ticket_mobile_item_button_name);
                buttonname.setText("사용가능");
                content.addView(longTicketMobileListView);
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.activity_ticket_mobile_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                Log.e("Swipe", "Refreshing Number");
                ( new android.os.Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 10000);
            }
        });

    }
}
