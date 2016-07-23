package com.trams.parkstem.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseNavigationActivity;
import com.trams.parkstem.gcm.QuickstartPreferences;
import com.trams.parkstem.gcm.RegistrationIntentService;
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

    private String gcmDeviceToken = "token";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // xml을 불러 오기 전에 해야 됨
        setContentView(R.layout.activity_login);

        registBroadcastReceiver();
        getInstanceIdToken();

        loginDatabase = LoginDatabase.getInstance(this);
        if(!loginDatabase.isDatabaseClear()) {
            try {
                ServerClient.getInstance().login(loginDatabase.getGubun(), loginDatabase.getId(), loginDatabase.getPw(), gcmDeviceToken);

                Toast.makeText(this, "자동 로그인 되었습니다!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } catch (ServerClient.ServerErrorException ex) {
                Toast.makeText(this, "자동 로그인에 실패 했습니다 - " + ex.msg, Toast.LENGTH_SHORT).show();
            }
        }

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




    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String TAG = getClass().getSimpleName();

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                    // 액션이 READY일 경우
                    Log.e("GCM","READY");
                } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                    // 액션이 GENERATING일 경우
                    Log.e("GCM","GENERATING");
                } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    // 액션이 COMPLETE일 경우
                    String token = intent.getStringExtra("token");
                    Log.e("GCM","COMPLETE - " + token);
                    gcmDeviceToken = token;
                }
            }
        };
    }

    /**
     * 앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
     */
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
