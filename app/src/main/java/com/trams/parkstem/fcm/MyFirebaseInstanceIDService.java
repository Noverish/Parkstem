package com.trams.parkstem.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Noverish on 2016-10-18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public static OnTokenRefreshedListener onTokenRefreshedListener;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);

        if(onTokenRefreshedListener != null)
            onTokenRefreshedListener.onTokenRefreshed(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {

    }

    public interface OnTokenRefreshedListener {
        void onTokenRefreshed(String token);
    }
}
