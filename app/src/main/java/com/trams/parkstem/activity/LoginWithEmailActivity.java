package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.others.OnLoginSuccessListener;
import com.trams.parkstem.server.LoginDatabase;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-21.
 */
public class LoginWithEmailActivity extends BaseBackSearchActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_email);

        //입력 한 글자가 * 로 보임
        PasswordTransformationMethod PassWtm = new PasswordTransformationMethod();
        ((EditText) findViewById(R.id.activity_login_by_email_password)).setTransformationMethod(PassWtm);

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

        emailEditText = (EditText) findViewById(R.id.activity_login_by_email_address);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("[^0-9A-Za-z.@]", "");
                if (!s.toString().equals(result)) {
                    emailEditText.setText(result);
                    emailEditText.setSelection(result.length());
                    // alert the user

                    Essentials.toastMessage(handler, LoginWithEmailActivity.this, "아이디에 영어, 숫자 외의 글자를 입력 할 수 없습니다");
                }
            }
        });

        passwordEditText = (EditText) findViewById(R.id.activity_login_by_email_password);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("[ㄱ-ㅎ가-힣]", "");
                if (!s.toString().equals(result)) {
                    passwordEditText.setText(result);
                    passwordEditText.setSelection(result.length());
                    // alert the user

                    Essentials.toastMessage(handler, LoginWithEmailActivity.this, "패스워드에 한글을 입력 할 수 없습니다");
                }
            }
        });
    }

    private void onLoginButtonClicked() {
        try {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(!email.matches("[A-Za-z]*@[A-Za-z]*[.][A-Za-z]*")) {
                Toast.makeText(this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
            } else if(password.length() < 4) {
                Toast.makeText(this, "비밀번호는 4자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            } else if(password.matches(".*[ㄱ-ㅎ가-힣].*")) {
                Toast.makeText(this, "비밀번호는 영어 대소문자 및 특수문자의 조합이어야 합니다.", Toast.LENGTH_SHORT).show();
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
