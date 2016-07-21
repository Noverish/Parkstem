package com.trams.parkstem.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.activity.ManagePurchaseActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

import java.util.Calendar;

/**
 * Created by JaeHyo on 2016-07-13.
 */
public class LongTicketManageView extends LinearLayout {
    private boolean viewOn;
    private RelativeLayout ticketView;
    private RelativeLayout purchaseButton;

    public LongTicketManageView(Context context, ServerClient.Ticket ticket) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        try{
            inflater.inflate(R.layout.long_ticket_mobile_item, this);
            final Context con = context;

            viewOn=false;
            ticketView = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_view);

            Log.e("fdsa","Fdsa");
            Log.e("asdf",(ticketView == null) + "");

            ticketView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLongMobileTicketViewButtonClicked(con);
                }
            });

            ((RelativeLayout) findViewById(R.id.long_ticket_mobile_item_above_layout)).setBackgroundColor(ContextCompat.getColor(context, R.color.WHITE));

            LayoutParams layoutParamsOff = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            ((LinearLayout) findViewById(R.id.long_ticket_mobile_item_below_layout)).setLayoutParams(layoutParamsOff);
            ((RelativeLayout) findViewById(R.id.long_ticket_mobile_item_bottom_layout)).setLayoutParams(layoutParamsOff);

            ServerClient.ParkInfo parkInfo = ServerClient.getInstance().parkInfo(ticket.local_id);

            TextView name = (TextView) findViewById(R.id.long_ticket_mobile_item_name);
            name.setText(ticket.ticket_name);

            name = (TextView) findViewById(R.id.long_ticket_mobile_item_place);
            name.setText(parkInfo.local_content);

            name = (TextView) findViewById(R.id.long_ticket_mobile_item_place_2);
            name.setText(parkInfo.local_address);

            Calendar ca = Calendar.getInstance();
            String date = ca.get(Calendar.YEAR) + ".";

            date += Essentials.numberWithZero(ca.get(Calendar.MONTH) + 1) + ".";

            date += Essentials.numberWithZero(ca.get(Calendar.DAY_OF_MONTH));

            name = (TextView) findViewById(R.id.long_ticket_mobile_item_start_date);
            name.setText(date);

            name = (TextView) findViewById(R.id.long_ticket_mobile_item_before_price);
            name.setText("1달 " + Essentials.WON_SYMBOL + Essentials.numberWithComma(ticket.original_price) );
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            name = (TextView) findViewById(R.id.long_ticket_mobile_item_after_price);
            name.setText("1달 " + Essentials.WON_SYMBOL + Essentials.numberWithComma(ticket.price) );

            ca.add(Calendar.DAY_OF_MONTH, 30);

            date = "- " + ca.get(Calendar.YEAR) + ".";

            date += Essentials.numberWithZero(ca.get(Calendar.MONTH) + 1) + ".";

            date += Essentials.numberWithZero(ca.get(Calendar.DAY_OF_MONTH));

            name = (TextView) findViewById(R.id.long_ticket_mobile_item_end_date);
            name.setText(date);

            final ServerClient.Ticket putticket = ticket;
            purchaseButton = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_purchase_button);
            purchaseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(con, ManagePurchaseActivity.class);

                    intent.putExtra("gubun", putticket.gubun);
                    intent.putExtra("idx", putticket.idx);
                    intent.putExtra("local_id", putticket.local_id);
                    intent.putExtra("original_price", putticket.original_price);
                    intent.putExtra("price", putticket.price);
                    intent.putExtra("term_name", putticket.term_name);
                    intent.putExtra("t_n", putticket.ticket_name);
                    intent.putExtra("term", (long)putticket.term.getTimeInMillis());
                    intent.putExtra("regdate", (long)putticket.regdate.getTimeInMillis());

                    con.startActivity(intent);
                }
            });
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }
    }
    private void onLongMobileTicketViewButtonClicked(Context context) {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams layoutParamsOn_60 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        LayoutParams layoutParamsOff = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        LayoutParams layoutParamsOn = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout belowcontent = (LinearLayout) findViewById(R.id.long_ticket_mobile_item_below_layout);
        RelativeLayout abovecontent = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_above_layout);
        RelativeLayout bottomcontent = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_bottom_layout);

        if(viewOn) {
            abovecontent.setBackgroundColor(ContextCompat.getColor(context, R.color.WHITE));
            belowcontent.setLayoutParams(layoutParamsOff);
            bottomcontent.setLayoutParams(layoutParamsOff);
        } else {
            abovecontent.setBackgroundColor(ContextCompat.getColor(context, R.color.btn_3));
            belowcontent.setLayoutParams(layoutParamsOn);
            bottomcontent.setLayoutParams(layoutParamsOn_60);
        }
        viewOn = !viewOn;
    }
}