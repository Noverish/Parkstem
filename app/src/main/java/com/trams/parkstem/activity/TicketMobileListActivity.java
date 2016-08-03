package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.HandlerHelper;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.TicketView;

import java.util.ArrayList;

/**
 * Created by JaeHyo on 2016-07-06.
 */
public class TicketMobileListActivity extends BaseBackSearchActivity {
    private SwipeRefreshLayout swipeLayout;
    private ArrayList<TicketView> ticketMobileListViews = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_mobile_list);
        setSearchEnable(true);
        setToolbarTitle("모바일 주차권");

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.activity_ticket_mobile_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {loadServerDataProcess();
            }
        });

        loadServerDataProcess();
    }

    private void loadServerDataProcess() {
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                reloadServerData();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void reloadServerData() {
        ticketMobileListViews.clear();

        try {
            ServerClient.TicketPurchaseList list = ServerClient.getInstance().ticketPurchase();

            TextView textView = (TextView) findViewById(R.id.activity_ticket_mobile_list_no_item);
            if(list.data.size() != 0)
                HandlerHelper.setVisibilityHandler(handler, textView, View.GONE);
            else
                HandlerHelper.setVisibilityHandler(handler, textView, View.VISIBLE);

            for(ServerClient.TicketPurchase purchase: list.data){
                if(purchase.allow) {
                    TicketView ticketView = new TicketView(this, purchase, "사용가능", false, true, false);
                    ticketMobileListViews.add(ticketView);
                }
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }

        showData();
    }

    private void showData() {
        showSearchResult("");
    }

    private void showSearchResult(String str) {
        LinearLayout content = (LinearLayout) findViewById(R.id.activity_ticket_mobile_list_layout);
        HandlerHelper.removeAllViewsHandler(handler, content);

        for(TicketView listView : ticketMobileListViews) {
            if(listView.getTicketName().contains(str)) {
                HandlerHelper.addViewHandler(handler, content, listView);
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
