package com.trams.parkstem.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.Essentials;

/**
 * Created by Noverish on 2016-07-13.
 */
public class SettingActivity extends BaseBackSearchActivity {
    private RelativeLayout pushButton;

    private boolean pushOn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        pushButton = (RelativeLayout) findViewById(R.id.activity_setting_push_button);
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePushStatus();
            }
        });

        pushOn = true;

        LinearLayout clauseButton = (LinearLayout) findViewById(R.id.activity_setting_clause);
        clauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = SettingActivity.this;
                String title = context.getString(R.string.popup_setting_clause_title);
                String content = context.getString(R.string.popup_setting_clause_content);
                Essentials.makePopup(context, title, content);
            }
        });
    }

    private void changePushStatus() {
        pushOn = !pushOn;

        if(pushOn) {
            pushButton.removeAllViews();
            getLayoutInflater().inflate(R.layout.push_button_on, pushButton);
        } else {
            pushButton.removeAllViews();
            getLayoutInflater().inflate(R.layout.push_button_off, pushButton);
        }
    }
}
