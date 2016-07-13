package com.trams.parkstem.custom_view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.activity.InputCarActivity;
import com.trams.parkstem.activity.InputCardActivity;

/**
 * Created by monc2 on 2016-07-09.
 */
public class BelowBar extends LinearLayout {
    private Context context;

    public BelowBar(Context context) {
        super(context);

        if(!isInEditMode())
            init(context);
    }

    public BelowBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(!isInEditMode())
            init(context);
    }

    private void init(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.below_bar_layout, this);

        RelativeLayout carButton = (RelativeLayout) findViewById(R.id.below_bar_car_button);
        carButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InputCarActivity.class);
                context.startActivity(intent);
            }
        });

        RelativeLayout cardButton = (RelativeLayout) findViewById(R.id.below_bar_card_button);
        cardButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InputCardActivity.class);
                context.startActivity(intent);
            }
        });




    }

}
