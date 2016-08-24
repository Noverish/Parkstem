package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by monc2 on 2016-08-24.
 */
public class Mypage extends BaseBackSearchActivity {
    private RelativeLayout changeEmail;
    private RelativeLayout changePw;
    ServerClient serverClient = ServerClient.getInstance();
    ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        changeEmail = (RelativeLayout) findViewById(R.id.activity_mypage_change_email);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mypage.this, ChangeEmail.class);
                startActivity(intent);
            }
        });

        changePw = (RelativeLayout) findViewById(R.id.activity_mypage_change_pw);
        changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mypage.this, ChangePw.class);
                startActivity(intent);
            }
        });

        refresh();
    }

    public void refresh() {
        RelativeLayout logo = (RelativeLayout) findViewById(R.id.activity_mypage_login_logo);
        TextView logined = (TextView) findViewById(R.id.activity_mypage_logined_by);
        TextView name = (TextView) findViewById(R.id.activity_mypage_name);
        TextView phone = (TextView) findViewById(R.id.activity_mypage_phone);

        /**이후 로고와 텍스트를 바꾸는 함수를 채워야 함
         *
         */

        String memberName = serverClient.getUserName();
        if (memberName.equals(""))
            memberName = "홍길동";

        name.setText(memberName);

        String memberphone = serverClient.getUserMobile();
        if (memberphone.equals(""))
            memberphone = "010-1234-5678";

        phone.setText(memberphone);

    }
}
