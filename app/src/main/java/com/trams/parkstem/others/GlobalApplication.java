package com.trams.parkstem.others;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.kakao.auth.KakaoSDK;

/**
 * Created by Noverish on 2016-07-22.
 */
public class GlobalApplication extends Application {
    private static GlobalApplication mInstance;
    private static volatile Activity currentActivity = null;

    public static Activity getCurrentActivity() {
        Log.e("TAG", "++ getCurrentActivity : " + (currentActivity != null ? currentActivity.getClass().getSimpleName() : "null"));
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        Log.e("TAG", "++ setCurrentActivity : " + (currentActivity != null ? currentActivity.getClass().getSimpleName() : "null"));
        GlobalApplication.currentActivity = currentActivity;
    }

    /**
     * singleton
     * @return singleton
     */
    public static GlobalApplication getGlobalApplicationContext() {
        if(mInstance == null)
            throw new IllegalStateException("this application does not inherit GlobalApplication");
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        KakaoSDK.init(new KakaoSDKAdapter());

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/HelveticaNeueLTStd-LtEx.otf");
    }
}