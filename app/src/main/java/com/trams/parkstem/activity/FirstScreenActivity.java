package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.webview.Mobilecertification;

/**
 * Created by Noverish on 2016-07-12.
 */
public class FirstScreenActivity extends BaseBackSearchActivity {
    RelativeLayout btnToMobileCertificate, btnToCarRegister, btnToCardRegister;
    TextView mobileStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        setBackEnable(false);

        btnToMobileCertificate = (RelativeLayout) findViewById(R.id.activity_first_moblie_certificate);
        btnToMobileCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMobileCerticication();
            }
        });

        btnToCarRegister = (RelativeLayout) findViewById(R.id.activity_first_car_register);
        btnToCarRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCarRegister();
            }
        });

        btnToCardRegister = (RelativeLayout) findViewById(R.id.activity_first_card_register);
        btnToCardRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCardRegister();
            }
        });


        RelativeLayout skipButton = (RelativeLayout) findViewById(R.id.activity_first_screen_skip);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (ServerClient.getInstance().memberInfo().certification) {
                mobileStatus = (TextView) findViewById(R.id.activity_first_mobile_status);
                mobileStatus.setText("휴대폰 인증 완료");
            }
        } catch (ServerClient.ServerErrorException ex) {
            ex.printStackTrace();
        }
    }

    public void toMobileCerticication(){
        try {
            if (ServerClient.getInstance().memberInfo().certification) {
                Toast.makeText(this, "휴대폰 인증이 이미 완료되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, Mobilecertification.class);
                startActivity(intent);
            }
        } catch (ServerClient.ServerErrorException ex) {
            ex.printStackTrace();
        }
    }
    public void toCarRegister(){
        Intent intent = new Intent(this, InputCarActivity.class);
        startActivity(intent);
    }
    public void toCardRegister(){
        Intent intent = new Intent(this, InputCardActivity.class);
        startActivity(intent);
    }

    private void skip() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
