package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.LinearLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.TicketView;

/**
 * Created by Noverish on 2016-07-08.
 */
public class ManageTicketActivity extends BaseBackSearchActivity {
    private SwipeRefreshLayout swipeLayout;

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

            ServerClient.Ticket ticket;
            for(int i=0; i < list.data.size(); ++i){
                ticket = list.data.get(i);
                TicketView ticketView = new TicketView(this, ticket, TicketView.SHORT_TICKET, "상세정보", true, false, true);
                content.addView(ticketView);
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.activity_manage_ticket_refresh_layout);
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
                }, 3000);
            }
        });
    }
}
