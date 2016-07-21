package com.trams.parkstem.gcm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.trams.parkstem.R;

import java.io.IOException;

/**
 * Created by Noverish on 2016-07-18.
 */
public class GetRegistrationToken extends Thread {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final String TAG = getClass().getSimpleName();
    private GetTokenErrorException exception;

    private Activity activity;
    private String token;

    public GetRegistrationToken(Activity activity) {
        this.activity = activity;

        start();
    }

    /**
     * GCM을 위한 Instance ID의 토큰을 생성하여 가져온다.
     */
    @SuppressLint("LongLogTag")
    @Override
    public void run() {
        if(checkPlayServices()) {
            // GCM Instance ID의 토큰을 가져오는 작업이 시작되면 LocalBoardcast로 GENERATING 액션을 알려 ProgressBar가 동작하도록 한다.

            // GCM을 위한 Instance ID를 가져온다.
            InstanceID instanceID = InstanceID.getInstance(activity);
            String token = null;
            try {
                synchronized (this) {
                    // GCM 앱을 등록하고 획득한 설정파일인 google-services.json을 기반으로 SenderID를 자동으로 가져온다.
                    String default_senderId = activity.getString(R.string.gcm_defaultSenderId);
                    // GCM 기본 scope는 "GCM"이다.
                    String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;
                    // Instance ID에 해당하는 토큰을 생성하여 가져온다.
                    token = instanceID.getToken(default_senderId, scope, null);

                    Log.e("asdf", "GCM Registration Token: " + token);
                }
            } catch (IOException e) {
                e.printStackTrace();
                exception = new GetTokenErrorException("토큰을 가져오는데 에러가 발생했습니다.");
            }

            this.token = token;
        }
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
                exception = new GetTokenErrorException("Google Play Service를 사용 할 수 없습니다. - ResultCode : " + resultCode);
            } else {
                exception = new GetTokenErrorException("Google Play Service를 사용 할 수 없습니다. - 지원하지 않는 기기 입니다.");
            }
            return false;
        }
        return true;
    }

    public String getToken() throws GetTokenErrorException{
        try {
            join();
        } catch (InterruptedException ex) {
            throw new GetTokenErrorException("InterruptedException occurred!e");
        }

        if (token != null) {
            return token;
        } else {
            if(exception != null)
                throw exception;
            else
                throw new GetTokenErrorException("알 수 없는 오류");
        }
    }

    public class GetTokenErrorException extends Exception {
        public String msg;

        public GetTokenErrorException(String msg) {
            this.msg = msg;
        }
    }
}