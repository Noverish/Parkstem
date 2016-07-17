package com.trams.parkstem.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

import java.util.Calendar;

/**
 * Created by JaeHyo on 2016-07-13.
 */
public class TicketPurchaseView extends LinearLayout {
    public TicketPurchaseView(Context context, ServerClient.Ticket ticket, ServerClient.TicketBuyList ticketBuyList) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        try{
            inflater.inflate(R.layout.ticket_purchase_item, this);

            ServerClient.ParkInfo parkInfo = ServerClient.getInstance().parkInfo(ticket.local_id);

            TextView name = (TextView) findViewById(R.id.ticket_purchase_item_name);
            name.setText(ticket.ticket_name);

            name = (TextView) findViewById(R.id.ticket_purchase_item_place);
            name.setText(parkInfo.local_content);

            name = (TextView) findViewById(R.id.ticket_purchase_item_place_2);
            name.setText(parkInfo.local_address);

            Calendar ca = ticketBuyList.start_date;
            String date = ca.get(Calendar.YEAR) + ".";

            date += Essentials.numberWithZero(ca.get(Calendar.MONTH) + 1) + ".";

            date += Essentials.numberWithZero(ca.get(Calendar.DAY_OF_MONTH));

            name = (TextView) findViewById(R.id.ticket_purchase_item_date);
            name.setText(date);

            name = (TextView) findViewById(R.id.ticket_purchase_item_before_price);
            name.setText("1시간 " + (char)0xffe6 + Essentials.numberWithComma(ticket.original_price) );
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            name = (TextView) findViewById(R.id.ticket_purchase_item_after_price);
            name.setText("1시간 " + (char)0xffe6 + Essentials.numberWithComma(ticket.price) );

            ca.add(Calendar.DAY_OF_MONTH, 99);

            date = "유효기간:" + date + "-" + ca.get(Calendar.YEAR) + ".";

            date += Essentials.numberWithZero(ca.get(Calendar.MONTH) + 1) + ".";

            date += Essentials.numberWithZero(ca.get(Calendar.DAY_OF_MONTH));

            name = (TextView) findViewById(R.id.ticket_mobile_item_duedate);
            name.setText(date);
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }
}
}
