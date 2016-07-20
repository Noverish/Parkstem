package com.trams.parkstem.activity;

import android.content.Context;
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
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

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

        pushOn = true;

        LinearLayout clauseButton = (LinearLayout) findViewById(R.id.activity_setting_clause);
        clauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = SettingActivity.this;
                String title = context.getString(R.string.popup_setting_clause_title);
                String content = context.getString(R.string.popup_setting_clause_content);
                //Essentials.makePopup(context, title, content);
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
            pushButton.removeAllViews();
            getLayoutInflater().inflate(R.layout.push_button_on, pushButton);
        } else {
            pushButton.removeAllViews();
            getLayoutInflater().inflate(R.layout.push_button_off, pushButton);
        }
    }

    private void logout() {

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
                            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                            startActivity(intent);
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
