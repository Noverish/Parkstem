package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.LongTicketMobileManageView;
import com.trams.parkstem.view.TicketMobileManageView;

/**
 * Created by Noverish on 2016-07-18.
 */
public class ManagePurchaseActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_purchase);

        Intent intent = getIntent();

        //SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.KOREA);

        ServerClient serverClient = ServerClient.getInstance();
        ServerClient.Ticket ticket = serverClient.new Ticket();

        ticket.gubun = intent.getIntExtra("gubun", -1);
        ticket.local_id = intent.getStringExtra("local_id");
        ticket.idx = intent.getIntExtra("idx", -1);
        ticket.original_price = intent.getStringExtra("original_price");
        ticket.price = intent.getStringExtra("price");
        ticket.term_name = intent.getStringExtra("term_name");
        ticket.ticket_name = intent.getStringExtra("t_n");
        // calendar 함수는 안받아옴. 후일에 혹여 필요해질 시,받아오는 것으로

        LinearLayout content = (LinearLayout) findViewById(R.id.activity_manage_purchase_ticket);
        RelativeLayout image = (RelativeLayout) findViewById(R.id.activity_manage_purchase_image);

        if (ticket.gubun == 1) {

            TicketMobileManageView ticketMobileManageView = new TicketMobileManageView(this, ticket);
            ViewGroup viewGroup = (ViewGroup)(ticketMobileManageView.findViewById(R.id.ticket_mobile_item_unity));
            viewGroup.removeView(viewGroup.findViewById(R.id.ticket_mobile_item_bottom_layout));

            RelativeLayout relativeLayout = (RelativeLayout)(ticketMobileManageView.findViewById(R.id.ticket_mobile_item_above_layout));
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) relativeLayout.getLayoutParams();
            layoutParams.bottomMargin=0;

            content.addView(ticketMobileManageView);
        }

        else if(ticket.gubun == 2){
            ((TextView) findViewById(R.id.activity_manage_purchase_duetext)).setText("");

            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47, getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 33, getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams rlayoutParams = new RelativeLayout.LayoutParams(width, height);
            rlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            image.setLayoutParams(rlayoutParams);
            image.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_menu_1month_2));

            LongTicketMobileManageView longTicketMobileManageView = new LongTicketMobileManageView(this, ticket);
            ViewGroup viewGroup = (ViewGroup)(longTicketMobileManageView.findViewById(R.id.long_ticket_mobile_item_unity));
            viewGroup.removeView(viewGroup.findViewById(R.id.long_ticket_mobile_item_bottom_layout));

            RelativeLayout relativeLayout = (RelativeLayout)(longTicketMobileManageView.findViewById(R.id.long_ticket_mobile_item_above_layout));
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) relativeLayout.getLayoutParams();
            layoutParams.bottomMargin=0;

            content.addView(longTicketMobileManageView);
        }

        ((TextView)findViewById(R.id.activity_manage_purchase_price)).setText((char) 0xffe6 + Essentials.numberWithComma(ticket.price));
    }

    @Override
    protected void onResume() {
        super.onResume();

        try{
            ((TextView)findViewById(R.id.activity_manage_purchase_name)).setText(ServerClient.getInstance().login.name);
            ((TextView)findViewById(R.id.activity_manage_purchase_email)).setText(ServerClient.getInstance().login.email);
            ((TextView)findViewById(R.id.activity_manage_purchase_phonenumber)).setText(ServerClient.getInstance().login.phone);

            TextView carEditButton = (TextView) findViewById(R.id.activity_manage_purchase_car_edit);
            TextView carnum = (TextView)findViewById(R.id.activity_manage_purchase_carnum);
            ServerClient.CarLists carLists = ServerClient.getInstance().listOfCar();
            if(carLists.data.size() == 0) {
                carnum.setText("12가1234");
                carnum.setTextColor(ContextCompat.getColor(this, R.color.gray));
                carEditButton.setText("등록");
            }
            else {
                carnum.setText(ServerClient.getInstance().dashboard().mycar);
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
                cardname.setText(ServerClient.getInstance().dashboard().mycard);
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

    private void onCarEditButtonClicked(){
        Intent intent = new Intent(this, InputCarActivity.class);
        startActivity(intent);
    }

    private void onCardEditButtonClicked(){
        Intent intent = new Intent(this, InputCardActivity.class);
        startActivity(intent);
    }
}
