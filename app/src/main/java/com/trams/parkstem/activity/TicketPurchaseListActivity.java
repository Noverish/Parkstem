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
 * Created by Noverish on 2016-07-08.
 */
public class TicketPurchaseListActivity extends BaseBackSearchActivity {
    private SwipeRefreshLayout swipeLayout;
    private ArrayList<TicketView> ticketMobileListViews = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_purchase_list);
        setSearchEnable(true);
        setToolbarTitle("주차권 구매내역");

        reloadServerData();

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.activity_ticket_purchase_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                reloadServerData();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    private void showData() {
        showSearchResult("");
    }

    private void reloadServerData() {
        ticketMobileListViews.clear();

        try {
            ServerClient.TicketPurchaseList list = ServerClient.getInstance().ticketPurchase();

            for(ServerClient.TicketPurchase purchase: list.data){
                if(!purchase.allow) {
                    TicketView ticketView = new TicketView(this, purchase, "사용가능", false, true, false);
                    ticketMobileListViews.add(ticketView);
                }
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }

        showData();
    }

    private void showSearchResult(String str) {
        LinearLayout content = (LinearLayout) findViewById(R.id.activity_ticket_purchase_list_layout);
        content.removeAllViews();

        for(TicketView listView : ticketMobileListViews) {
            if(listView.getTicketName().contains(str)) {
                content.addView(listView);
            }
        }
    }

    @Override
    public boolean onClose() {
        showData();
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