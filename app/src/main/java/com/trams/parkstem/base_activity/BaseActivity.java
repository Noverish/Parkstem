package com.trams.parkstem.base_activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.trams.parkstem.fcm.MyFirebaseMessagingService;
import com.trams.parkstem.login.GlobalApplication;
import com.trams.parkstem.others.Essentials;

/**
 * Created by Noverish on 2016-07-23.
 */
public class BaseActivity extends AppCompatActivity {
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean shouldAlert = false;
    private Activity activity;
    protected android.os.Handler handler = new android.os.Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        registBroadcastReceiver();
    }


    @Override
    protected void onResume() {
        super.onResume();
        GlobalApplication.setCurrentActivity(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MyFirebaseMessagingService.PUSH_RECEIVE));

        if(shouldAlert) {
            Essentials.alertParkState(BaseActivity.this);
            shouldAlert = !shouldAlert;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                Log.i("Broadcast","Receive");
                if(action.equals(MyFirebaseMessagingService.PUSH_RECEIVE)){
                    Essentials.alertParkState(BaseActivity.this);
                }
            }
        };
    }
}
