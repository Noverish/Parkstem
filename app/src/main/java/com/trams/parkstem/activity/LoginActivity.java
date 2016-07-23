package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseNavigationActivity;
import com.trams.parkstem.others.FacebookLoginClient;
import com.trams.parkstem.others.KakaoLoginClient;
import com.trams.parkstem.others.NaverLoginClient;
import com.trams.parkstem.others.OnLoginSuccessListener;
import com.trams.parkstem.server.LoginDatabase;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-04.
 */
public class LoginActivity extends AppCompatActivity implements OnLoginSuccessListener {
    private FacebookLoginClient facebookLoginClient;
    private NaverLoginClient naverLoginClient;
    private KakaoLoginClient kakaoLoginClient;
    private LoginDatabase loginDatabase;

    private String gcmDeviceToken = "login_activity_token_error";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // xml을 불러 오기 전에 해야 됨
        setContentView(R.layout.activity_login);

        gcmDeviceToken = getIntent().getStringExtra("token");

        loginDatabase = LoginDatabase.getInstance(this);

        naverLoginClient = NaverLoginClient.getInstance(this);
        naverLoginClient.setOnLoginSuccessListener(this);

        facebookLoginClient = FacebookLoginClient.getInstance(this);
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
                intent.putExtra("token",gcmDeviceToken);
                startActivity(intent);
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
        kakaoLoginClient.onActivityResult(requestCode, resultCode, data);

        if(resultCode == BaseNavigationActivity.RESULT_FINISH)
            finish();
    }

    @Override
    public void onLoginSuccess(String gubun, String name, String email, String mobile, String nickName, String kakaoID, String facebookID, String naverID, String parkstemID, String parkstemPW) {
        try {
            ServerClient.getInstance().login(gubun, email, "", gcmDeviceToken);

            loginDatabase.setData(gubun, email, "");

            if (ServerClient.getInstance().login.certification) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, FirstScreenActivity.class);
                startActivity(intent);
            }

            finish();
        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(LoginActivity.this, "로그인에 실패 했습니다 - " + ex.msg, Toast.LENGTH_SHORT).show();
        }
    }
}
