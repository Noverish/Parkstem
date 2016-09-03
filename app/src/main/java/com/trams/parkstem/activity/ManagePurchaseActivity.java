package com.trams.parkstem.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.TicketView;

import java.util.Calendar;

/**
 * Created by Noverish on 2016-07-18.
 */
public class ManagePurchaseActivity extends BaseBackSearchActivity {
    ServerClient.Ticket ticket;
    TicketView ticketView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_purchase);

        ticket = getIntent().getParcelableExtra("ticket");

        LinearLayout content = (LinearLayout) findViewById(R.id.activity_manage_purchase_ticket);
        RelativeLayout image = (RelativeLayout) findViewById(R.id.activity_manage_purchase_image);

        if (ticket.gubun == 1) {
            setToolbarTitle("주차권 구매");

            ((TextView) findViewById(R.id.activity_manage_purchase_duetext)).setText(ticket.term_name);

            ticketView = new TicketView(this, ticket, "상세정보", false, false, false);
            content.addView(ticketView);
        }

        else if(ticket.gubun == 2){
            setToolbarTitle("정기권 구매");

            ((TextView) findViewById(R.id.activity_manage_purchase_duetext)).setText("");

            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47, getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 33, getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams rlayoutParams = new RelativeLayout.LayoutParams(width, height);
            rlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            image.setLayoutParams(rlayoutParams);
            image.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_menu_1month_2));

            ticketView = new TicketView(this, ticket, "상세정보", false, false, true);
            content.addView(ticketView);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getIntent().getLongExtra("calendar",0));
            ticketView.setDate(calendar);
        }
        ticketView.deleteGUMEword();

        Log.e("purchase", Essentials.numberWithComma(ticket.price));
        ((TextView)findViewById(R.id.activity_manage_purchase_price)).setText((char) 0xffe6 + Essentials.numberWithComma(ticket.price));

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_manage_purchase_complete_button);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPurchaseButtonClicked();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        try{
            ServerClient.DashBoard dashBoard = ServerClient.getInstance().dashboard();

            ((TextView)findViewById(R.id.activity_manage_purchase_name)).setText(ServerClient.getInstance().getUserName());
            ((TextView)findViewById(R.id.activity_manage_purchase_email)).setText(ServerClient.getInstance().getUserEmail());
            ((TextView)findViewById(R.id.activity_manage_purchase_phonenumber)).setText(ServerClient.getInstance().getUserMobile());

            TextView carEditButton = (TextView) findViewById(R.id.activity_manage_purchase_car_edit);
            TextView carnum = (TextView)findViewById(R.id.activity_manage_purchase_carnum);
            ServerClient.CarLists carLists = ServerClient.getInstance().listOfCar();
            if(carLists.data.size() == 0) {
                carnum.setText("차량등록");
                carnum.setTextColor(ContextCompat.getColor(this, R.color.gray));
                carEditButton.setText("등록");
            }
            else {
                carnum.setText(dashBoard.mycar);
                carnum.setTextColor(ContextCompat.getColor(this, R.color.gray_for_text));
                carEditButton.setText("변경");
            }

            TextView cardEditButton = (TextView) findViewById(R.id.activity_manage_purchase_card_edit);
            TextView cardname = (TextView) findViewById(R.id.activity_manage_purchase_cardname);
            ServerClient.CardList cardList = ServerClient.getInstance().cardList();
            if(cardList.data.size() == 0) {
                cardname.setText("카드등록");
                cardname.setTextColor(ContextCompat.getColor(this, R.color.gray));
                cardEditButton.setText("등록");
            }
            else {
                cardname.setText(dashBoard.mycard);
                cardname.setTextColor(ContextCompat.getColor(this, R.color.gray_for_text));
                cardEditButton.setText("변경");
            }

            carEditButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    onCarEditButtonClicked();
                }
            });

            cardEditButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    onCardEditButtonClicked();
                }
            });
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!", ex.msg);
        }
    }

    private void onCarEditButtonClicked() {
        Intent intent = new Intent(this, InputCarActivity.class);
        startActivity(intent);
    }

    private void onCardEditButtonClicked() {
        Intent intent = new Intent(this, InputCardActivity.class);
        startActivity(intent);
    }

    private void onPurchaseButtonClicked() {
        String title = (ticket.gubun == 1) ? "주차권 구매" : "정기권 구매";
        String content = "'" + ticket.ticket_name + "'을\n구매하시겠습니까?";

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("확인", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realPurchase();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void realPurchase() {
        try {
            ServerClient client = ServerClient.getInstance();
            client.ticketInfoRegister(ticket.gubun + "", ticket.idx + "", client.getUserName(), client.getUserMobile(), client.getUserEmail(), ticketView.getDate());

            String title = (ticket.gubun == 1) ? "주차권 구매" : "정기권 구매";
            String content = title + "가\n완료 되었습니다.";

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton("확인", null)
                    .show();
        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(this, "구매에 실패했습니다 - " + ex.msg, Toast.LENGTH_SHORT).show();
        }
    }
}
