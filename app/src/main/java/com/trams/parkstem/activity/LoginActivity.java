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
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.trams.parkstem.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Noverish on 2016-07-04.
 */
public class LoginActivity extends AppCompatActivity {
    private OAuthLogin mOAuthLoginModule;
    private OAuthLoginHandler mOAuthLoginHandler;
    private static final String OAUTH_CLIENT_ID = "5S_JYLsKtBe7qfx_fd7B";
    private static final String OAUTH_CLIENT_SECRET = "T_MKslJGj3";
    private static final String OAUTH_CLIENT_NAME = "파크스템";

    private Activity mContext = this;

    private LinearLayout naverLoginButton;

    private JSONObject naverUserProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView loginbyemail;

        /**
         * OAuthLoginHandler를 startOAuthLoginActivity() 메서드 호출 시 파라미터로 전달하거나 OAuthLoginButton
         객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다.
         */

        mOAuthLoginHandler = new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                    String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                    long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                    String tokenType = mOAuthLoginModule.getTokenType(mContext);
                    //mOauthAT.setText(accessToken);
                    //mOauthRT.setText(refreshToken);
                    //mOauthExpires.setText(String.valueOf(expiresAt));
                    //mOauthTokenType.setText(tokenType);
                    //mOAuthState.setText(mOAuthLoginModule.getState(mContext).toString());
                    Toast.makeText(mContext, "로그인에 성공했습니다", Toast.LENGTH_SHORT).show();

                    Log.e("Asdf",accessToken);

                    getUserProfile(accessToken);
                } else {
                    String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                    String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                    Toast.makeText(mContext, "errorCode:" + errorCode
                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                }
            };
        };

        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                LoginActivity.this
                ,OAUTH_CLIENT_ID
                ,OAUTH_CLIENT_SECRET
                ,OAUTH_CLIENT_NAME
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );

        naverLoginButton = (LinearLayout) findViewById(R.id.activity_login_naver);
        naverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOAuthLoginModule.startOauthLoginActivity(mContext, mOAuthLoginHandler);
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
        Intent intent = new Intent(this, AssignActivityBase.class);
        startActivity(intent);
    }

    public void getUserProfile(final String accessToken) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = "https://openapi.naver.com/v1/nid/me";
                    URL url = new URL(urlStr);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Authorization", "Bearer " + accessToken);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String line;
                    String jsonStr = "";

                    while ((line = reader.readLine()) != null) {
                        jsonStr += line + "\n";
                    }
                    reader.close();

                    naverUserProfile = new JSONObject(jsonStr);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.e("asdf",naverUserProfile.toString());
    }
}
