package com.trams.parkstem.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-04.
 */
public class AssignActivity extends BaseBackSearchActivity {
    TextView alert;
    private ServerClient serverClient;
    private RelativeLayout assignButton;

    EditText textView;

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
        String name = textView.getText().toString();

        //boolean success = serverClient.Regbyemail();

        //if(success) {
        //    Toast.makeText(this, "회원가입이 성공했습니다", Toast.LENGTH_LONG).show();
        //}
    }
}
