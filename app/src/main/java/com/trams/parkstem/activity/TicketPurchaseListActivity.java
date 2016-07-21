package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.LinearLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.LongTicketUsedView;
import com.trams.parkstem.view.TicketUsedView;

import java.util.Calendar;

/**
 * Created by Noverish on 2016-07-08.
 */
public class TicketPurchaseListActivity extends BaseBackSearchActivity {
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_purchase_list);
        setSearchEnable(true);
        setToolbarTitle("주차권 구매내역");

        LinearLayout content = (LinearLayout) findViewById(R.id.activity_ticket_purchase_list_layout);

        ServerClient.TicketPurchaseList ticketPurchaseList;
        ServerClient.TicketLists list;
        ServerClient.LongTicketLists longlist;
        Calendar ca= Calendar.getInstance();

        try {
            ticketPurchaseList = ServerClient.getInstance().ticketPurchase();
            list = ServerClient.getInstance().listOfTicket();
            longlist = ServerClient.getInstance().listOfLongTicket();

            for(ServerClient.TicketPurchase ticketPurchase : ticketPurchaseList.data){
                /*
                만료일이 지났는지 확인하는 부분
                if(!ca.after(ticketPurchase.end_date))
                    continue;
                */
                if(ticketPurchase.gubun==1){
                    for(ServerClient.Ticket ticket: list.data)
                        if(ticket.local_id == ticketPurchase.local_id){
                            TicketUsedView ticketUsedView = new TicketUsedView(this, ticket, ticketPurchase);
                            content.addView(ticketUsedView);
                            break;
                        }
                }

                else if(ticketPurchase.gubun==2){
                    for(ServerClient.Ticket ticket: longlist.data)
                        if(ticket.local_id == ticketPurchase.local_id){
                            LongTicketUsedView longTicketUsedView = new LongTicketUsedView(this, ticket, ticketPurchase);
                            content.addView(longTicketUsedView);
                            break;
                        }
                }
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.activity_ticket_purchase_refresh_layout);
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