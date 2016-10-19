package com.trams.parkstem.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.trams.parkstem.R;
import com.trams.parkstem.fcm.MyFirebaseInstanceIDService;
import com.trams.parkstem.login.LoginDatabase;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-04.
 */
public class SplashActivity extends AppCompatActivity{
    private boolean backgroundProcessDone = false;
    private boolean sleepDone = false;

    private boolean goToLoginActivity = true;
    private String autoLoginFailMessage = null;

    private android.os.Handler handler = new android.os.Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(!isNetworkAvailable()) {
            autoLoginFailMessage = "인터넷에 연결되어있지 않습니다.";
            changeActivity();
        } else {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.i("token",token);
            if (token != null) {
                LoginDatabase.getInstance(this).setToken(token);
                new AutoLoginThread();
            } else {
                MyFirebaseInstanceIDService.onTokenRefreshedListener = new MyFirebaseInstanceIDService.OnTokenRefreshedListener() {
                    @Override
                    public void onTokenRefreshed(String token) {
                        Log.i("token",token);
                        LoginDatabase.getInstance(SplashActivity.this).setToken(token);
                        new AutoLoginThread();
                    }
                };
            }

            new SleepThread(1500);
        }
    }

    private void changeActivity() {
        if(autoLoginFailMessage != null)
            Essentials.toastMessage(handler, this, autoLoginFailMessage);

        if (goToLoginActivity) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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
                    ServerClient.getInstance().login(loginDatabase.getGubun(), loginDatabase.getId(), loginDatabase.getPw(), loginDatabase.getToken());

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
