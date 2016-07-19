package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.LinearLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.LongTicketPurchaseView;
import com.trams.parkstem.view.TicketPurchaseView;

import java.util.Calendar;

/**
 * Created by Noverish on 2016-07-08.
 */
public class TicketPurchaseListActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_purchase_list);
        setSearchEnable(true);
        setToolbarTitle("주차권 구매내역");

        LinearLayout content = (LinearLayout) findViewById(R.id.activity_ticket_purchase_list_layout);

        ServerClient.PaymentInfo paymentInfo;
        ServerClient.TicketLists list;
        ServerClient.LongTicketLists longlist;
        Calendar ca= Calendar.getInstance();

        try {
            paymentInfo = ServerClient.getInstance().ticketpurchase();
            list = ServerClient.getInstance().listOfTicket();
            longlist = ServerClient.getInstance().listOfLongTicket();

            for(ServerClient.TicketBuyList ticketBuyList: paymentInfo.data){
                /*
                만료일이 지났는지 확인하는 부분
                if(!ca.after(ticketBuyList.end_date))
                    continue;
                */
                if(ticketBuyList.gubun==1){
                    for(ServerClient.Ticket ticket: list.data)
                        if(ticket.local_id == ticketBuyList.local_id){
                            TicketPurchaseView ticketPurchaseView = new TicketPurchaseView(this, ticket, ticketBuyList);
                            content.addView(ticketPurchaseView);
                            break;
                        }
                }

                else if(ticketBuyList.gubun==2){
                    for(ServerClient.Ticket ticket: longlist.data)
                        if(ticket.local_id == ticketBuyList.local_id){
                            LongTicketPurchaseView longTicketPurchaseView = new LongTicketPurchaseView(this, ticket, ticketBuyList);
                            content.addView(longTicketPurchaseView);
                            break;
                        }
                }
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }
    }

}