package com.trams.parkstem.activity;

import android.content.Context;
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
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-04.
 */
public class AssignActivity extends BaseBackSearchActivity {
    private TextView alert;
    private ServerClient serverClient;
    private RelativeLayout assignButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

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
                Context context = AssignActivity.this;
                String title = context.getString(R.string.popup_clause_title);
                String content = context.getString(R.string.popup_clause_content);
                Essentials.makePopup(context, title, content);
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
            } else if(!email.matches(".*@.*[.].*")) {
                Toast.makeText(this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
            } else if(password.length() < 4) {
                Toast.makeText(this, "비밀번호는 4자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            } else {
                //ServerClient.getInstance().register(name, email, phone, "", email, password, getIntent().getStringExtra("token"));
                Toast.makeText(this, "회원가입이 성공했습니다", Toast.LENGTH_SHORT).show();

                ServerClient.getInstance().login(email, password, getIntent().getStringExtra("token"));

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
