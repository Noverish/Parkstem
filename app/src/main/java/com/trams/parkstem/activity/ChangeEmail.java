package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;

/**
 * Created by monc2 on 2016-08-24.
 */
public class ChangeEmail extends BaseBackSearchActivity {
    private RelativeLayout assignButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        assignButton = (RelativeLayout) findViewById(R.id.activity_change_email_button);
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *
                 이메일 변경 요청보내는 API
                 */
            }
        });
    }
}
