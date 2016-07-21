package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
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
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47, getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23, getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams rlayoutParams = new RelativeLayout.LayoutParams(width, height);
            image.setLayoutParams(rlayoutParams);

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



    }
}
