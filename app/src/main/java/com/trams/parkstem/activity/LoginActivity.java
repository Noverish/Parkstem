package com.trams.parkstem.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.others.NaverLoginClient;

/**
 * Created by Noverish on 2016-07-04.
 */
public class LoginActivity extends AppCompatActivity {
    private Activity thisActivity;
    private LinearLayout naverLoginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.thisActivity = this;
        TextView loginbyemail;

        naverLoginButton = (LinearLayout) findViewById(R.id.activity_login_naver);
        naverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("naver","naverLogin");
                NaverLoginClient naverLoginClient = new NaverLoginClient(thisActivity);
                naverLoginClient.login();
            }
        });

        loginbyemail = (TextView) findViewById(R.id.avtivity_login_byemail);
        loginbyemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetoregister();
            }
        });
    }

    private void movetoregister(){
        Intent intent = new Intent(this, AssignActivity.class);
        startActivity(intent);
    }

}
