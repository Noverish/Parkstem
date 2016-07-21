package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.trams.parkstem.R;

/**
 * Created by Noverish on 2016-07-04.
 */
public class SplashActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("run","run");

                    Thread.sleep(3000);

                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(intent);
                    finish();
                } catch (Exception ex) {

                }
            }
        });

        thread.start();
    }
}
