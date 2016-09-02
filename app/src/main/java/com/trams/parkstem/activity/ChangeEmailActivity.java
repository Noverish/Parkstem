package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-08-24.
 */
public class ChangeEmailActivity extends BaseBackSearchActivity {
    private EditText email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        setToolbarTitle("이메일 수정");

        email = (EditText) findViewById(R.id.activity_change_email_email);

        RelativeLayout button = (RelativeLayout) findViewById(R.id.activity_change_email_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });
    }

    private void changeEmail() {
        if(email == null) {
            Log.e("ERROR", "Email EditText is null");
            return;
        }

        String emailStr = email.getText().toString();

        if(!emailStr.matches("[A-Za-z0-9]+@[A-Za-z0-9]+[.][A-Za-z0-9]+"))
            Toast.makeText(this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
        else {
            try {
                String memberGubun = ServerClient.getInstance().getMemberGubun();
                ServerClient.getInstance().changeEmail(memberGubun, emailStr);

                Toast.makeText(this, "이메일 변경에 성공했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } catch (ServerClient.ServerErrorException ex) {

                Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
