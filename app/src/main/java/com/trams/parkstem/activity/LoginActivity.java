package com.trams.parkstem.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.trams.parkstem.R;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.e("activity_mobile_ticket","onCreate");

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
                Log.e("activity_mobile_ticket","naver");
                mOAuthLoginModule.startOauthLoginActivity(mContext, mOAuthLoginHandler);
            }
        });
    }
}
