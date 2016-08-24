package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-08-24.
 */
public class FindPasswordActivity extends BaseBackSearchActivity {
    private EditText email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);
        setTitle("비밀번호찾기");

        email = (EditText) findViewById(R.id.activity_find_pw_email);

        RelativeLayout findPWButton = (RelativeLayout) findViewById(R.id.activity_find_pw_button);
        if(findPWButton != null) {
            findPWButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ServerClient.getInstance().findPassword(email.getText().toString());
                        Essentials.toastMessage(handler, FindPasswordActivity.this, "임시 비밀번호를 전송했습니다");
                    } catch (ServerClient.ServerErrorException ex) {
                        Essentials.toastMessage(handler, FindPasswordActivity.this, "임시 비밀번호를 전송하는데 실패했습니다. - " + ex.msg);
                    }
                }
            });
        }

    }
}
