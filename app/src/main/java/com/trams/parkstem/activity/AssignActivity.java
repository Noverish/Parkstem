package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-04.
 */
public class AssignActivity extends AppCompatActivity {
    private ServerClient serverClient;
    private RelativeLayout assignButton;

    EditText textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serverClient = ServerClient.getInstance();

        assignButton = (RelativeLayout) findViewById(R.id.activity_assign_button);
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assign();
            }
        });
    }

    private void assign() {
        String name = textView.getText().toString();

        //boolean success = serverClient.Regbyemail();

        //if(success) {
        //    Toast.makeText(this, "회원가입이 성공했습니다", Toast.LENGTH_LONG).show();
        //}
    }
}
