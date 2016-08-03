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
import java.util.Calendar;

/**
 * Created by Noverish on 2016-07-08.
 */
public class ManageTicketActivity extends BaseBackSearchActivity {
    private SwipeRefreshLayout swipeLayout;
    private ArrayList<TicketView> ticketViews = new ArrayList<>();
    private TextView termName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ticket);
        setSearchEnable(true);
        setToolbarTitle("주차권 구매");

        termName = (TextView) findViewById(R.id.activity_manage_ticket_term_name);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.activity_manage_ticket_refresh_layout);
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
        ticketViews.clear();

        try {
            ServerClient.TicketLists list = ServerClient.getInstance().listOfTicket(Calendar.getInstance());

            TextView textView = (TextView) findViewById(R.id.activity_manage_ticket_list_no_item);
            if(list.data.size() != 0)
                HandlerHelper.setVisibilityHandler(handler, textView, View.GONE);
            else
                HandlerHelper.setVisibilityHandler(handler, textView, View.VISIBLE);

            for (ServerClient.Ticket ticket : list.data) {
                TicketView ticketView = new TicketView(this, ticket, "상세정보", true, false, false);
                ticketViews.add(ticketView);
            }

            HandlerHelper.setTextHandler(handler, termName, list.data.get(0).term_name);
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!", ex.msg);
        }

        showData();
    }

    private void showData() {
        showSearchResult("");
    }

    private void showSearchResult(String result) {
        LinearLayout content = (LinearLayout) findViewById(R.id.activity_manage_ticket_layout);
        HandlerHelper.removeAllViewsHandler(handler, content);

        for(TicketView ticketView : ticketViews)
            if(ticketView.getTicketName().contains(result))
                HandlerHelper.addViewHandler(handler, content, ticketView);
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
