package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.trams.parkstem.R;
import com.trams.parkstem.others.FacebookLoginClient;
import com.trams.parkstem.others.NaverLoginClient;

/**
 * Created by Noverish on 2016-07-04.
 */
public class LoginActivity extends AppCompatActivity {
    private FacebookLoginClient facebookLoginClient;
    private NaverLoginClient naverLoginClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // xml을 불러 오기 전에 해야 됨
        setContentView(R.layout.activity_login);

        naverLoginClient = NaverLoginClient.getInstance(LoginActivity.this);
        naverLoginClient.setOnLoginSuccessListener(new OnLoginSuccessListener() {
            @Override
            public void onLoginSuccess(int gubun, String name, String email, String mobile, String nickName, String kakaoID, String facebookID, String naverID, String parkstemID, String parkstemPW) {

            }
        });
        facebookLoginClient = FacebookLoginClient.getInstance(LoginActivity.this);
        facebookLoginClient.setOnLoginSuccessListener(new OnLoginSuccessListener() {
            @Override
            public void onLoginSuccess(int gubun, String name, String email, String mobile, String nickName, String kakaoID, String facebookID, String naverID, String parkstemID, String parkstemPW) {

            }
        });

        LinearLayout naverLoginButton = (LinearLayout) findViewById(R.id.activity_login_naver);
        naverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naverLoginClient.login();
            }
        });

        TextView loginbyemail = (TextView) findViewById(R.id.avtivity_login_byemail);
        loginbyemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AssignActivity.class);
                startActivity(intent);
            }
        });


        LinearLayout loginButton = (LinearLayout)findViewById(R.id.login_layout_facebook);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLoginClient.login();
            }
        });

        LinearLayout logoutButton = (LinearLayout)findViewById(R.id.login_layout_kakaotalk);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
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

    }

    public interface OnLoginSuccessListener {
        int NAVER = 0;
        int FACEBOOK = 1;
        int KAKAO = 2;
        int PARKSTEM = 3;

        void onLoginSuccess(
                int gubun, String name, String email, String mobile, String nickName,
                String kakaoID, String facebookID, String naverID, String parkstemID, String parkstemPW);
    }
}
