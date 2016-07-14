package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;

/**
 * Created by Noverish on 2016-07-12.
 */
public class FirstScreenActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        setBackEnable(false);

        RelativeLayout skipButton = (RelativeLayout) findViewById(R.id.activity_first_screen_skip);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstScreenActivity.this, HomeActivity.class);
                startActivity(intent);
                FirstScreenActivity.this.finish();
            }
        });

        LinearLayout mobileButton = (LinearLayout) findViewById(R.id.activity_first_screen_mobile);
        mobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstScreenActivity.this, MobileCertificationActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout carButton = (LinearLayout) findViewById(R.id.activity_first_screen_car);
        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstScreenActivity.this, InputCarActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout cardButton = (LinearLayout) findViewById(R.id.activity_first_screen_card);
        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstScreenActivity.this, InputCardActivity.class);
                startActivity(intent);
            }
        });
    }
}
