package com.trams.parkstem.login;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Noverish on 2016-07-10.
 */
public class NaverLoginClient {
    private Activity activity;

    private OnLoginSuccessListener listener;

    private OAuthLogin mOAuthLoginModule;
    private OAuthLoginHandler mOAuthLoginHandler;
    private NaverUserProfile userProfile;
    private static final String OAUTH_CLIENT_ID = "5S_JYLsKtBe7qfx_fd7B";
    private static final String OAUTH_CLIENT_SECRET = "T_MKslJGj3";
    private static final String OAUTH_CLIENT_NAME = "파크스템";
    private String accessToken;

    private final String TAG = getClass().getSimpleName();

    private static NaverLoginClient instance;
    public static NaverLoginClient getInstance(Activity activity) {
        if(instance == null)
            instance = new NaverLoginClient(activity);

        return instance;
    }

    private NaverLoginClient(Activity activity) {
        this.activity = activity;

        /**
         * OAuthLoginHandler를 startOAuthLoginActivity() 메서드 호출 시 파라미터로 전달하거나 OAuthLoginButton
         객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다.
         */

        mOAuthLoginHandler = new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    accessToken = mOAuthLoginModule.getAccessToken(NaverLoginClient.this.activity);
                    String refreshToken = mOAuthLoginModule.getRefreshToken(NaverLoginClient.this.activity);
                    long expiresAt = mOAuthLoginModule.getExpiresAt(NaverLoginClient.this.activity);
                    String tokenType = mOAuthLoginModule.getTokenType(NaverLoginClient.this.activity);

                    extractUserProfile();
                    Log.e("NaverLoginSuccess",userProfile.toString());

                    if(listener != null) {
                        listener.onLoginSuccess(OnLoginSuccessListener.NAVER, userProfile.email);
                    }
                } else {
                    String errorCode = mOAuthLoginModule.getLastErrorCode(NaverLoginClient.this.activity).getCode();
                    String errorDesc = mOAuthLoginModule.getLastErrorDesc(NaverLoginClient.this.activity);
//                    Toast.makeText(NaverLoginClient.this.activity, "errorCode:" + errorCode
//                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                }
            };
        };

        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                activity
                ,OAUTH_CLIENT_ID
                ,OAUTH_CLIENT_SECRET
                ,OAUTH_CLIENT_NAME
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );
    }

    public void login() {
        mOAuthLoginModule.startOauthLoginActivity(activity, mOAuthLoginHandler);
    }

    public void logout(Context context) {
        mOAuthLoginModule.logout(context);
    }

    private void extractUserProfile() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con;

                try {
                    String urlStr = "https://openapi.naver.com/v1/nid/me";
                    URL url = new URL(urlStr);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Authorization", "Bearer " + accessToken);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String line;
                    String jsonStr = "";

                    while ((line = reader.readLine()) != null) {
                        jsonStr += line + "\n";
                    }
                    reader.close();

                    userProfile = new NaverUserProfile(new JSONObject(jsonStr));
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
    }

    public NaverUserProfile getUserProfile() {
        return userProfile;
    }

    public void setOnLoginSuccessListener(OnLoginSuccessListener listener) {
        this.listener = listener;
    }

    public class NaverUserProfile {
        public String id;
        public String nickName;
        public String name;
        public String email;
        public String gender;
        public String age;
        public String birthday;
        public String profile_image;

        public NaverUserProfile (JSONObject jsonObject) {
            try {
                JSONObject response = jsonObject.getJSONObject("response");

                id = response.getString("id");
                nickName = response.getString("nickname");
                name = response.getString("name");
                email = response.getString("email");
                gender = response.getString("gender");
                age = response.getString("age");
                birthday = response.getString("birthday");
                profile_image = response.getString("profile_image");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "NaverUserProfile{" +
                    "id='" + id + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", gender='" + gender + '\'' +
                    ", age='" + age + '\'' +
                    ", birthday='" + birthday + '\'' +
                    ", profile_image='" + profile_image + '\'' +
                    '}';
        }
    }
}
