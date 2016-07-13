package com.trams.parkstem.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trams.parkstem.R;

/**
 * Created by monc2 on 2016-07-09.
 */
public class BelowBar extends LinearLayout {
    TextView car;
    RelativeLayout sef;

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

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.below_bar_layout, this);


    }


}
