package com.trams.parkstem.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.trams.parkstem.R;
import com.trams.parkstem.gcm.QuickstartPreferences;
import com.trams.parkstem.gcm.RegistrationIntentService;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.LoginDatabase;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-04.
 */
public class SplashActivity extends AppCompatActivity{
    private String gcmDeviceToken = "splash_activity_token_error";
    private boolean backgroundProcessDone = false;
    private boolean sleepDone = false;
    private boolean goToLoginActivity = true;
    private String autoLoginFailMessage = null;

    private AutoLoginThread autoLoginThread;
    private SleepThread sleepThread;

    private android.os.Handler handler = new android.os.Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        registBroadcastReceiver();
        getInstanceIdToken();

        SleepThread thread = new SleepThread(1500);
    }

    private void changeActivity() {
        if(autoLoginFailMessage != null)
            Essentials.toastMessage(handler, this, autoLoginFailMessage);


        if (goToLoginActivity) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.putExtra("token",gcmDeviceToken);
            SplashActivity.this.startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
        } else {
            Essentials.toastMessage(handler, this, "구글 플레이 서비스를 이용할 수 없습니다.\n푸쉬알림을 못 받을 수도 있습니다.");
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
                } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                    // 액션이 GENERATING일 경우
                } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    // 액션이 COMPLETE일 경우
                    String token = intent.getStringExtra("token");
                    Log.e("GCM","COMPLETE - " + token);
                    gcmDeviceToken = token;

                    if(isNetworkAvailable()) {
                        autoLoginThread = new AutoLoginThread();
                    } else {
                        autoLoginFailMessage = "네트워크에 연결되어 있지 않습니다.";
                        LoginDatabase.getInstance(SplashActivity.this).clearDatabase();
                        goToLoginActivity = true;
                        backgroundProcessDone = true;
                    }

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
                Toast.makeText(this, "구글 플레이 서비스를 이용할 수 없는 기기입니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }



    private class AutoLoginThread extends Thread {
        public AutoLoginThread() {
            start();
        }

        @Override
        public void run() {
            super.run();

            LoginDatabase loginDatabase = LoginDatabase.getInstance(SplashActivity.this);
            if(!loginDatabase.isDatabaseClear()) {
                try {
                    ServerClient.getInstance().login(loginDatabase.getGubun(), loginDatabase.getId(), loginDatabase.getPw(), gcmDeviceToken);

                    goToLoginActivity = false;
                } catch (ServerClient.ServerErrorException ex) {
                    autoLoginFailMessage = "자동 로그인에 실패했습니다 - " + ex.msg;
                }
            }

            backgroundProcessDone = true;
            if(sleepDone) {
                changeActivity();
            }
        }
    }

    private class SleepThread extends Thread {
        private long sleepMillis;

        private SleepThread(long sleepMillis) {
            this.sleepMillis = sleepMillis;
            start();
        }

        @Override
        public void run() {
            super.run();

            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException ex) {

            }

            sleepDone = true;
            if(backgroundProcessDone) {
                changeActivity();
            }
        }
    }
}
