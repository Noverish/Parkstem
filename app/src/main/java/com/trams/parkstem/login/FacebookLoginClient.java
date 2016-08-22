package com.trams.parkstem.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Noverish on 2016-07-20.
 */
public class FacebookLoginClient  {
    private Activity activity;

    private OnLoginSuccessListener listener;

    private CallbackManager callbackManager = null;
    private AccessTokenTracker accessTokenTracker = null;
    private AccessToken accessToken;
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        private ProfileTracker profileTracker;

        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
//            Toast.makeText(activity, loginResult.getAccessToken().getUserId(), Toast.LENGTH_LONG).show();
            // Toast.makeText(getActivity().getApplicationContext(), loginResult.getAccessToken().getToken(), Toast.LENGTH_LONG).show();


            if(profile == null) {
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        Log.e("facebook - profile", profile2.getFirstName());
                        profileTracker.stopTracking();
                    }
                };
                // no need to call startTracking() on mProfileTracker
                // because it is called by its constructor, internally.
                profileTracker.startTracking();
            }

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.e("GraphRequest", response.toString());
                            Log.e("GraphRequest", object.toString());

                            // Application code
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();

            if (profile != null) {
                Log.e("FacebookLoginSuccess", "Id : " + profile.getId() + ", name : " + profile.getName() + ", email : " + parameters.getString("email"));
            }

            if (listener != null)
                listener.onLoginSuccess(OnLoginSuccessListener.FACEBOOK, parameters.getString("email"));
        }

        @Override
        public void onCancel() {
//            Toast.makeText(activity, "User sign in canceled!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FacebookException e) {
            e.printStackTrace();
        }
    };

    private static FacebookLoginClient instance;
    public static FacebookLoginClient getInstance(Activity activity) {
        if(instance == null)
            instance = new FacebookLoginClient(activity);

        return instance;
    }

    private FacebookLoginClient(Activity activity) {
        this.activity = activity;

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
// App code
                Log.e("eNuri", "Current Token : " + currentAccessToken);
            }
        };

        accessTokenTracker.startTracking();
    }

    public void login() {
        LoginManager.getInstance().registerCallback(callbackManager, callback);
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "user_friends", "email"));
    }

    public void logout() {
        LoginManager.getInstance().logOut();
    }

    public void stopTracking() {
        accessTokenTracker.stopTracking();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setOnLoginSuccessListener(OnLoginSuccessListener onLoginSuccessListener) {
        listener = onLoginSuccessListener;
    }


}
