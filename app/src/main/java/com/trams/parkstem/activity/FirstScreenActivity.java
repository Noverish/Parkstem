package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseNavigationActivity;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-12.
 */
public class FirstScreenActivity extends BaseNavigationActivity {
    RelativeLayout btnToMobileCertificate, btnToCarRegister, btnToCardRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

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

    public void toMobileCerticication(){
        Intent intent = new Intent(this, MobileCertificationActivity.class);
        startActivity(intent);
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
        try {
            if (ServerClient.getInstance().memberInfo().certification) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "휴대폰 인증이 되어 있지 않아 휴대폰 인증 페이지로 넘어가겠습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MobileCertificationActivity.class);
                startActivity(intent);
            }
        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
        }
    }
}
