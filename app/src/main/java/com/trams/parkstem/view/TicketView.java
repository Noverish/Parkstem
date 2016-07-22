package com.trams.parkstem.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.activity.ManagePurchaseActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

import java.util.Calendar;

/**
 * Created by Noverish on 2016-07-21.
 */
public class TicketView extends LinearLayout {
    public static boolean LONG_TICKET = true;
    public static boolean SHORT_TICKET = false;

    private Context context;
    private Calendar calendar = Calendar.getInstance();

    private RelativeLayout showDetailButton;
    private ServerClient.Ticket ticket;

    private boolean detailOpen = true;
    private boolean isItLongTicket;

    public TicketView(final Context contextParam, ServerClient.Ticket ticket, boolean isItLongTicket, String buttonName, boolean purchaseButtonOn, boolean alwaysOpened, boolean calendarButton) {
        super(contextParam);
        this.context = contextParam;
        this.ticket = ticket;
        this.isItLongTicket = isItLongTicket;

        LayoutInflater inflater = LayoutInflater.from(contextParam);
        inflater.inflate(R.layout.ticket_item, this, true);

        LinearLayout totalLayout = (LinearLayout) findViewById(R.id.ticket_item);

        findViewById(R.id.long_ticket_mobile_item_above_layout).setBackgroundColor(ContextCompat.getColor(contextParam, R.color.btn_3));

        //구매 버튼 여부에 따라 구매버튼 삭제
        RelativeLayout purchaseButton = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_bottom_layout);
        if(purchaseButtonOn) {
            purchaseButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPurchaseButtonClicked();
                }
            });
        } else {
            ((ViewGroup) purchaseButton.getParent()).removeView(purchaseButton);
        }

        if(!alwaysOpened) {
            showDetailButton = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_view);
            showDetailButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShowDetailButtonClicked();
                }
            });

            onShowDetailButtonClicked();
        }

        ImageView calendarImageView = (ImageView) findViewById(R.id.long_ticket_mobile_item_calender);
        if(calendarButton) {
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
        } else {
            ViewGroup parent = (ViewGroup) calendarImageView.getParent();
            parent.removeView(calendarImageView); //평범하게 this.removeView(view) 하면 안됨;;
        }


        TextView startDate = (TextView) findViewById(R.id.long_ticket_mobile_item_start_date);
        if(isItLongTicket) {
            startDate.setText(Essentials.calendarToDate(ticket.start_date));

            TextView endDate = (TextView) findViewById(R.id.long_ticket_mobile_item_end_date);
            endDate.setText(Essentials.calendarToDate(ticket.end_date));

            View dueDateLayout = findViewById(R.id.ticket_item_due_date_layout);
            ((ViewGroup) dueDateLayout.getParent()).removeView(dueDateLayout);//평범하게 this.removeView(view) 하면 안됨;;
        } else {
            startDate.setText(Essentials.calendarToDate(calendar));

            View dueDateLayout = findViewById(R.id.ticket_item_end_date_layout);
            ((ViewGroup) dueDateLayout.getParent()).removeView(dueDateLayout);//평범하게 this.removeView(view) 하면 안됨;;

            TextView dueDateStart = (TextView) findViewById(R.id.ticket_item_due_date_start);
            dueDateStart.setText(Essentials.calendarToDate(calendar));

            TextView dueDateEnd = (TextView) findViewById(R.id.ticket_item_due_date_end);
            dueDateEnd.setText(Essentials.calendarToDate(ticket.term));
        }

        TextView buttonNameText = (TextView) findViewById(R.id.long_ticket_mobile_item_button_name);
        TextView ticketName = (TextView) findViewById(R.id.long_ticket_mobile_item_name);
        TextView parkAddress = (TextView) findViewById(R.id.long_ticket_mobile_item_place_2);
        TextView beforePriceTerm = (TextView) findViewById(R.id.long_ticket_mobile_item_before_price_term_name);
        TextView beforePrice = (TextView) findViewById(R.id.long_ticket_mobile_item_before_price_amount);
        TextView afterPriceTerm = (TextView) findViewById(R.id.long_ticket_mobile_item_after_price_term_name) ;
        TextView afterPrice = (TextView) findViewById(R.id.long_ticket_mobile_item_after_price_amount);
        TextView shortAddress = (TextView) findViewById(R.id.long_ticket_mobile_item_place);

        buttonNameText.setText(buttonName);

        ticketName.setText(ticket.ticket_name);

        try {
            ServerClient.ParkInfo parkInfo = ServerClient.getInstance().parkInfo(ticket.local_id);
            parkAddress.setText(parkInfo.local_address);
            shortAddress.setText(parkInfo.short_address);
        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(contextParam, ex.msg, Toast.LENGTH_SHORT).show();
            parkAddress.setText("");
            shortAddress.setText("");
        }


        beforePriceTerm.setText(ticket.term_name);
        beforePrice.setText(Essentials.numberWithComma(ticket.original_price));
        afterPriceTerm.setText(ticket.term_name);
        afterPrice.setText(Essentials.numberWithComma(ticket.price));
    }

    private void onShowDetailButtonClicked() {
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
//
//        LayoutParams layoutParamsOn_60 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        LayoutParams layoutParamsOff = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        LayoutParams layoutParamsOn = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout belowcontent = (LinearLayout) findViewById(R.id.long_ticket_mobile_item_below_layout);
        RelativeLayout abovecontent = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_above_layout);

        detailOpen = !detailOpen;

        if(detailOpen) {
            abovecontent.setBackgroundColor(ContextCompat.getColor(context, R.color.btn_3));
            belowcontent.setLayoutParams(layoutParamsOn);
        } else {
            abovecontent.setBackgroundColor(ContextCompat.getColor(context, R.color.WHITE));
            belowcontent.setLayoutParams(layoutParamsOff);
        }
    }

    private void refreshDate() {
        /*TextView startDate = (TextView) findViewById(R.id.long_ticket_mobile_item_start_date);
        startDate.setText(Essentials.calendarToDate(calendar));

        Calendar expiration = (Calendar) calendar.clone();
        expiration.add(Calendar.DAY_OF_MONTH, 100);

        String expiryEnd = expiration.get(Calendar.YEAR) + ".";
        expiryEnd += Essentials.numberWithZero(expiration.get(Calendar.MONTH) + 1) + ".";
        expiryEnd += Essentials.numberWithZero(expiration.get(Calendar.DAY_OF_MONTH));

        TextView expiryDateStart = (TextView) findViewById(R.id.ticket_item_due_date_start);
        expiryDateStart.setText(calendar);

        TextView expiryDateEnd = (TextView) findViewById(R.id.ticket_item_due_date_end);
        expiryDateEnd.setText(expiryEnd);*/
    }

    private void onPurchaseButtonClicked() {
        Intent intent = new Intent(context, ManagePurchaseActivity.class);
        intent.putExtra("ticket",ticket);
        context.startActivity(intent);
    }

    public ServerClient.Ticket getTicket() {
        return ticket;
    }
}
