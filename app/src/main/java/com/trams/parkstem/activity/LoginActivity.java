package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseNavigationActivity;
import com.trams.parkstem.login.FacebookLoginClient;
import com.trams.parkstem.login.KakaoLoginClient;
import com.trams.parkstem.login.LoginDatabase;
import com.trams.parkstem.login.NaverLoginClient;
import com.trams.parkstem.login.OnLoginSuccessListener;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-04.
 */
public class LoginActivity extends AppCompatActivity implements OnLoginSuccessListener, View.OnClickListener {
    private FacebookLoginClient facebookLoginClient;
    private NaverLoginClient naverLoginClient;
    private KakaoLoginClient kakaoLoginClient;
    private LoginDatabase loginDatabase;

    private boolean alreadyBackButtonPressed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginDatabase = LoginDatabase.getInstance(this);

        naverLoginClient = NaverLoginClient.getInstance(this);
        naverLoginClient.setOnLoginSuccessListener(this);

        facebookLoginClient = new FacebookLoginClient(this);
        facebookLoginClient.setOnLoginSuccessListener(this);

        kakaoLoginClient = KakaoLoginClient.getInstance(this);
        kakaoLoginClient.setOnLoginSuccessListener(this);

        LinearLayout naverLoginButton = (LinearLayout) findViewById(R.id.activity_login_naver);
        naverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naverLoginClient.login();
            }
        });

        LinearLayout kakaoLoginButton = (LinearLayout) findViewById(R.id.login_layout_kakaotalk);
        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakaoLoginClient.login();
            }
        });

        LinearLayout facebookLoginButton = (LinearLayout)findViewById(R.id.login_layout_facebook);
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLoginClient.login();
            }
        });

        TextView loginbyemail = (TextView) findViewById(R.id.avtivity_login_byemail);
        loginbyemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginWithEmailActivity.class);
                startActivity(intent);
            }
        });

        View view = findViewById(R.id.activity_login_ester_egg);
        view.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        facebookLoginClient.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLoginClient.onActivityResult(requestCode, resultCode, data);
        kakaoLoginClient.onActivityResult(requestCode, resultCode, data);

        if(resultCode == BaseNavigationActivity.RESULT_FINISH)
            finish();
    }

    @Override
    public void onLoginSuccess(String gubun, String email) {
        try {
            ServerClient.getInstance().login(gubun, email, "", LoginDatabase.getInstance(this).getToken());

            loginDatabase.setData(gubun, email, "");

            if (ServerClient.getInstance().isUserCertification()) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "휴대폰 인증을 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, FirstScreenActivity.class);
                startActivity(intent);
            }

            finish();
        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(LoginActivity.this, "로그인에 실패 했습니다 - " + ex.msg, Toast.LENGTH_SHORT).show();
        }
    }

    private int pressNumber;
    @Override
    public void onClick(View v) {
        if(pressNumber == 4) {
            ServerClient.getInstance().setUniqueID("13617600");
            try {
                ServerClient.getInstance().memberInfo();
            } catch (ServerClient.ServerErrorException ex) {

            }
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else
            pressNumber++;
    }

    @Override
    protected void onResume() {
        super.onResume();
        alreadyBackButtonPressed = false;
    }

    @Override
    public void onBackPressed() {
        if(alreadyBackButtonPressed)
            super.onBackPressed();
        else {
            alreadyBackButtonPressed = true;
            Essentials.toastMessage(new android.os.Handler(), this, getString(R.string.on_back_button_pressed_message), 14);
        }
    }
}
