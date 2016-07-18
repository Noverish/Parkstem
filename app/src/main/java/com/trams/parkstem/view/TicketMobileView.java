package com.trams.parkstem.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

import java.util.Calendar;

/**
 * Created by JaeHyo on 2016-07-13.
 */
public class TicketMobileView extends LinearLayout {
    private Calendar calendar;

    public TicketMobileView(final Context context, ServerClient.Ticket ticket) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
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

        try {
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
            name.setText("1시간 " + (char) 0xffe6 + Essentials.numberWithComma(ticket.original_price));
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            name = (TextView) findViewById(R.id.ticket_mobile_item_after_price);
            name.setText("1시간 " + (char) 0xffe6 + Essentials.numberWithComma(ticket.price));

        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!", ex.msg);
        }
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