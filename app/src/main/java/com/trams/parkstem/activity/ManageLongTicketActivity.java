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
public class ManageLongTicketActivity extends BaseBackSearchActivity {
    private SwipeRefreshLayout swipeLayout;

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

            ServerClient.Ticket ticket;
            for(int i=0; i<list.data.size(); i++) {
                ticket = list.data.get(i);
                TicketView ticketView = new TicketView(this, ticket, TicketView.LONG_TICKET, "상세정보", true, false, false);
               /* LongTicketManageView longTicketManageView = new LongTicketManageView(this, ticket);

                */

                /*if (i == list.data.size() - 1) {
                    RelativeLayout relativeLayout = (RelativeLayout) (longTicketView.findViewById(R.id.long_ticket_mobile_item_above_layout));
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) relativeLayout.getLayoutParams();

                    layoutParams.bottomMargin = 0;
                }*/

                content.addView(ticketView);
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.activity_manage_long_ticket_refresh_layout);
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
