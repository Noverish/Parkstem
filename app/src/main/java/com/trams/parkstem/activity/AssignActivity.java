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
import com.trams.parkstem.clause.Clause2;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.others.OnLoginSuccessListener;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-04.
 */
public class AssignActivity extends BaseBackSearchActivity {
    private TextView alert;
    private ServerClient serverClient;
    private RelativeLayout assignButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        //입력한 글자가 * 로 보임
        PasswordTransformationMethod PassWtm = new PasswordTransformationMethod();
        ((EditText) findViewById(R.id.activity_assign_password)).setTransformationMethod(PassWtm);

        serverClient = ServerClient.getInstance();

        assignButton = (RelativeLayout) findViewById(R.id.activity_assign_button);
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assign();
            }
        });


        alert = (TextView) findViewById(R.id.activity_assign_clause);
        alert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(AssignActivity.this, Clause2.class);
                startActivity(intent);
            }
        });

        emailEditText = (EditText) findViewById(R.id.activity_assign_email);
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

                    Essentials.toastMessage(handler, AssignActivity.this, "아이디에 영어, 숫자 외의 글자를 입력 할 수 없습니다");
                }
            }
        });

        passwordEditText = (EditText) findViewById(R.id.activity_assign_password);
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

                    Essentials.toastMessage(handler, AssignActivity.this, "패스워드에 한글을 입력 할 수 없습니다");
                }
            }
        });
    }

    private void assign() {
        try {
            String name = ((EditText) findViewById(R.id.activity_assign_name)).getText().toString();
            String email = ((EditText) findViewById(R.id.activity_assign_email)).getText().toString();
            String password = ((EditText) findViewById(R.id.activity_assign_password)).getText().toString();
            String phone = ((EditText) findViewById(R.id.activity_assign_phone)).getText().toString();

            if(name.equals("")) {
                Toast.makeText(this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            } else if(!email.matches("[A-Za-z0-9]+@[A-Za-z0-9]+[.][A-Za-z0-9]+")) {
                Toast.makeText(this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
            } else if(password.length() < 4) {
                Toast.makeText(this, "비밀번호는 4자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            } else if(password.matches(".*[ㄱ-ㅎ가-힣].*")) {
                Toast.makeText(this, "비밀번호는 영어 대소문자 및 특수문자의 조합이어야 합니다.", Toast.LENGTH_SHORT).show();
            } else {
                ServerClient.getInstance().register(name, email, phone, "", email, password, getIntent().getStringExtra("token"));
                Toast.makeText(this, "회원가입이 성공했습니다", Toast.LENGTH_SHORT).show();

                ServerClient.getInstance().login(OnLoginSuccessListener.PARKSTEM, email, password, getIntent().getStringExtra("token"));

                Intent intent = new Intent(this, FirstScreenActivity.class);
                startActivity(intent);
                finish();
            }

        } catch (NullPointerException nul) {
            nul.printStackTrace();
        } catch (ServerClient.ServerErrorException error) {
            Toast.makeText(this, "회원가입이 실패했습니다. - " + error.msg, Toast.LENGTH_LONG).show();
        }
    }
}
