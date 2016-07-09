package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.custom_view.CustomView;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by JaeHyo on 2016-07-06.
 */


public class TicketMobileListActivity extends AppCompatActivity{
    LinearLayout listLayout;
    /*
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_mobile_list);

        ServerClient.getInstance().listOfTicket();

        ArrayList<MobileTicket> list;

        for(MobileTicket ticket : list) {
            listLayout.addView(new CustomView(this, ticket);
        }
    }*/
}
