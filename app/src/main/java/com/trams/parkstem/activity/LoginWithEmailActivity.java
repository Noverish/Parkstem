package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.OnLoginSuccessListener;
import com.trams.parkstem.server.LoginDatabase;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-21.
 */
public class LoginWithEmailActivity extends BaseBackSearchActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_email);

        RelativeLayout LoginButton = (RelativeLayout) findViewById(R.id.activity_login_by_email_login_button);
        TextView EmailRegister = (TextView) findViewById(R.id.activity_login_by_email_register);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClicked();
            }
        });

        EmailRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmailRegisterClicked();
            }
        });
    }

    private void onLoginButtonClicked() {
        try {
            String email = ((EditText) findViewById(R.id.activity_login_by_email_address)).getText().toString();
            String password = ((EditText) findViewById(R.id.activity_login_by_email_password)).getText().toString();


            if(!email.matches(".*@.*[.].*")) {
                Toast.makeText(this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
            } else if(password.length() < 4) {
                Toast.makeText(this, "비밀번호는 4자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            } else {
                ServerClient.getInstance().login(OnLoginSuccessListener.PARKSTEM, email, password, getIntent().getStringExtra("token"));

                LoginDatabase.getInstance(this).setData(OnLoginSuccessListener.PARKSTEM, email, password, getIntent().getStringExtra("token"));

                Toast.makeText(this, "로그인에 성공했습니다.",Toast.LENGTH_SHORT).show();

                if(ServerClient.getInstance().memberInfo().certification) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                } else  {
                    Intent intent = new Intent(this, FirstScreenActivity.class);
                    startActivity(intent);
                }
            }
        } catch (ServerClient.ServerErrorException ex){
            ex.printStackTrace();
            Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
        }
    }

    private  void onEmailRegisterClicked(){
        Intent intent = new Intent(this, AssignActivity.class);
        intent.putExtra("token",getIntent().getStringExtra("token"));
        startActivity(intent);
        finish();
    }
}
