package com.trams.parkstem.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.base_activity.BaseNavigationActivity;
import com.trams.parkstem.login.FacebookLoginClient;
import com.trams.parkstem.login.KakaoLoginClient;
import com.trams.parkstem.login.LoginDatabase;
import com.trams.parkstem.login.NaverLoginClient;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.webview.ClauseWebView;

/**
 * Created by Noverish on 2016-07-13.
 */
public class SettingActivity extends BaseBackSearchActivity {
    private RelativeLayout pushButton;

    private boolean pushOn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        pushButton = (RelativeLayout) findViewById(R.id.activity_setting_push_button);
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePushStatus();
            }
        });

        pushOn = ServerClient.getInstance().isUserPush();
        pushButton.removeAllViews();
        if(pushOn) {
            getLayoutInflater().inflate(R.layout.push_button_on, pushButton);
        } else {
            getLayoutInflater().inflate(R.layout.push_button_off, pushButton);
        }

        LinearLayout clauseButton = (LinearLayout) findViewById(R.id.activity_setting_clause);
        clauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ClauseWebView.class);
                intent.putExtra(ClauseWebView.CLAUSE_EXTRA, 1);
                startActivity(intent);
            }
        });

        LinearLayout logoutButton = (LinearLayout) findViewById(R.id.activity_setting_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        LinearLayout signOutButton = (LinearLayout)findViewById(R.id.activity_setting_sign_out);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void changePushStatus() {
        pushOn = !pushOn;

        if(pushOn) {
            try {
                ServerClient.getInstance().push("Y");
                pushButton.removeAllViews();
                getLayoutInflater().inflate(R.layout.push_button_on, pushButton);
            } catch (ServerClient.ServerErrorException ex) {
                Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
            }

        } else {
            try {
                ServerClient.getInstance().push("N");
                pushButton.removeAllViews();
                getLayoutInflater().inflate(R.layout.push_button_off, pushButton);
            } catch (ServerClient.ServerErrorException ex) {
                Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        FacebookLoginClient facebookLoginClient = FacebookLoginClient.getInstance(this);
        NaverLoginClient naverLoginClient = NaverLoginClient.getInstance(this);
        KakaoLoginClient kakaoLoginClient = KakaoLoginClient.getInstance(this);

        facebookLoginClient.logout();
        naverLoginClient.logout(this);
        kakaoLoginClient.logout();

        LoginDatabase.getInstance(this).clearDatabase();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        setResult(BaseNavigationActivity.RESULT_FINISH);
        finish();
    }

    private void signOut() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.popup_sign_out_title))
                .setMessage(getString(R.string.popup_sign_out_content))
                .setPositiveButton("탈퇴", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ServerClient.getInstance().memberDelete();
                            Toast.makeText(SettingActivity.this, "회원탈퇴에 성공하였습니다.",Toast.LENGTH_SHORT).show();

                            logout();

                            KakaoLoginClient kakaoLoginClient = KakaoLoginClient.getInstance(SettingActivity.this);
                            kakaoLoginClient.signOut();
                        } catch (ServerClient.ServerErrorException error) {
                            error.printStackTrace();
                            Toast.makeText(SettingActivity.this, "회원탈퇴에 실패하였습니다. - " + error.msg,Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", null)
                .show();

    }
}
