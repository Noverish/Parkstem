package com.trams.parkstem.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trams.parkstem.R;


/**
 * Created by JaeHyo on 2016-07-09.
 */

public class CustomView extends LinearLayout {
    TextView name;

    public CustomView(Context context  /*, MobileTicket ticket  */) {
        super(context);
/*
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.ticket_mobile_item, this);

        name = (TextView) findViewById(R.id.ticket_mobile_item_name);
        name.setText(ticket.name);*/
    }
}
