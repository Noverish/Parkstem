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

import java.util.ArrayList;

/**
 * Created by JaeHyo on 2016-07-06.
 */
public class TicketMobileListActivity extends BaseBackSearchActivity {
    private SwipeRefreshLayout swipeLayout;
    private ArrayList<TicketView> ticketMobileListViews = new ArrayList<>();
    private ArrayList<TicketView> longTicketMobileListViews = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_mobile_list);
        setSearchEnable(true);
        setToolbarTitle("모바일 주차권");

        reloadData();

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.activity_ticket_mobile_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                reloadData();
                swipeLayout.setRefreshing(false);
            }
        });

    }

    private void reloadData() {
        LinearLayout content = (LinearLayout) findViewById(R.id.activity_ticket_mobile_list_layout);
        content.removeAllViews();
        ticketMobileListViews.clear();
        longTicketMobileListViews.clear();

        Log.e("reload","reload");

        ServerClient.TicketLists list;
        ServerClient.LongTicketLists longlist;

        try {
            list = ServerClient.getInstance().listOfTicket();

            for(ServerClient.Ticket ticket: list.data){
                TicketView ticketView = new TicketView(this, ticket, TicketView.SHORT_TICKET, "사용가능", false, true, false);
                content.addView(ticketView);
            }

            longlist = ServerClient.getInstance().listOfLongTicket();

            for(ServerClient.Ticket ticket: longlist.data){
                TicketView ticketView = new TicketView(this, ticket, TicketView.LONG_TICKET, "사용가능", false, true, false);

//                LongTicketMobileListView longTicketMobileListView = new LongTicketMobileListView(this, ticket);
//                longTicketMobileListViews.add(longTicketMobileListView);
//                TextView buttonname = (TextView)longTicketMobileListView.findViewById(R.id.long_ticket_mobile_item_button_name);
//                buttonname.setText("사용가능");
                content.addView(ticketView);
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }
    }



    private void showSearchResult(String str) {
        LinearLayout content = (LinearLayout) findViewById(R.id.activity_ticket_mobile_list_layout);
        content.removeAllViews();

        for(TicketView listView : ticketMobileListViews) {
            if(listView.getTicket().ticket_name.contains(str)) {
                content.addView(listView);
            }
        }

        for(TicketView listView : longTicketMobileListViews) {
            if(listView.getTicket().ticket_name.contains(str)) {
                content.addView(listView);
            }
        }
    }

    @Override
    public boolean onClose() {
        reloadData();
        return super.onClose();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return super.onQueryTextSubmit(query);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        showSearchResult(newText);
        return super.onQueryTextChange(newText);
    }
}
