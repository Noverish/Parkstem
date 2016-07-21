package com.trams.parkstem.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
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
public class TicketManageView extends LinearLayout {
    public static final int SHORT_TICKTET = 0;
    public static final int LONG_TICKET = 1;

    private boolean viewOn;
    private RelativeLayout ticketView;
    private RelativeLayout purchaseButton;

    private Calendar calendar;

    public TicketManageView(final Context context, ServerClient.Ticket ticket) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        try{
            final Context con = context;
            inflater.inflate(R.layout.ticket_mobile_item, this);

            ImageView calendarImageView = (ImageView) findViewById(R.id.ticket_mobile_item_calender);
            calendarImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            refreshDate();
                        }
                    };

                    new DatePickerDialog(context, dateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            viewOn=false;
            ticketView = (RelativeLayout) findViewById(R.id.ticket_mobile_item_view);
            ticketView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMobileTicketViewButtonClicked(con);
                }
            });

            ((RelativeLayout) findViewById(R.id.ticket_mobile_item_above_layout)).setBackgroundColor(ContextCompat.getColor(context, R.color.WHITE));

            LinearLayout.LayoutParams layoutParamsOff = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            ((LinearLayout) findViewById(R.id.ticket_mobile_item_below_layout)).setLayoutParams(layoutParamsOff);
            ((RelativeLayout) findViewById(R.id.ticket_mobile_item_bottom_layout)).setLayoutParams(layoutParamsOff);

            ServerClient.ParkInfo parkInfo = ServerClient.getInstance().parkInfo(ticket.local_id);

            TextView name = (TextView) findViewById(R.id.ticket_mobile_item_name);
            name.setText(ticket.ticket_name);

            name = (TextView) findViewById(R.id.ticket_mobile_item_place);
            name.setText(parkInfo.local_content);

            name = (TextView) findViewById(R.id.ticket_mobile_item_place_2);
            name.setText(parkInfo.local_address);

            calendar = Calendar.getInstance();
            refreshDate();

            name = (TextView) findViewById(R.id.ticket_mobile_item_before_price);
            name.setText("1시간 " + Essentials.WON_SYMBOL + Essentials.numberWithComma(ticket.original_price));
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            name = (TextView) findViewById(R.id.ticket_mobile_item_after_price);
            name.setText("1시간 " + Essentials.WON_SYMBOL + Essentials.numberWithComma(ticket.price));

            final ServerClient.Ticket putticket = ticket;
            purchaseButton = (RelativeLayout) findViewById(R.id.ticket_mobile_item_purchase_button);
            purchaseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(context, ManagePurchaseActivity.class);

                    intent.putExtra("gubun", putticket.gubun);
                    intent.putExtra("idx", putticket.idx);
                    intent.putExtra("local_id", putticket.local_id);
                    intent.putExtra("original_price", putticket.original_price);
                    intent.putExtra("price", putticket.price);
                    intent.putExtra("term_name", putticket.term_name);
                    intent.putExtra("t_n", putticket.ticket_name);
                    intent.putExtra("term", (long)putticket.term.getTimeInMillis());
                    intent.putExtra("regdate", (long)putticket.regdate.getTimeInMillis());

                    context.startActivity(intent);
                }
            });

        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!", ex.msg);
        }
    }


    private void onMobileTicketViewButtonClicked(Context context) {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams layoutParamsOff = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams layoutParamsOn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParamsOn_60 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);


        LinearLayout belowcontent = (LinearLayout) findViewById(R.id.ticket_mobile_item_below_layout);
        RelativeLayout abovecontent = (RelativeLayout) findViewById(R.id.ticket_mobile_item_above_layout);
        RelativeLayout bottomcontent = (RelativeLayout) findViewById(R.id.ticket_mobile_item_bottom_layout);

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


    private void refreshDate() {
        String date = calendar.get(Calendar.YEAR) + ".";
        date += Essentials.numberWithZero(calendar.get(Calendar.MONTH) + 1) + ".";
        date += Essentials.numberWithZero(calendar.get(Calendar.DAY_OF_MONTH));

        TextView dateTextView = (TextView) findViewById(R.id.ticket_mobile_item_date);
        dateTextView.setText(date);


        Calendar expiration = (Calendar) calendar.clone();
        expiration.add(Calendar.DAY_OF_MONTH, 99);

        date = "유효기간:" + date + "-" + expiration.get(Calendar.YEAR) + ".";
        date += Essentials.numberWithZero(expiration.get(Calendar.MONTH) + 1) + ".";
        date += Essentials.numberWithZero(expiration.get(Calendar.DAY_OF_MONTH));

        TextView expirationTextView = (TextView) findViewById(R.id.ticket_mobile_item_duedate);
        expirationTextView.setText(date);
    }
}