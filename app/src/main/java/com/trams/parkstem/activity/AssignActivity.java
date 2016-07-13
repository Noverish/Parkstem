package com.trams.parkstem.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
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
                popup_clause();
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

    public void popup_clause(){
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        //R.layout.dialog는 xml 파일명이고  R.id.popup은 보여줄 레이아웃 아이디
        View layout = inflater.inflate(R.layout.popup_clause, (ViewGroup) findViewById(R.id.popup_clause_content));
        AlertDialog.Builder aDialog = new AlertDialog.Builder(this);

        aDialog.setTitle("약관"); //타이틀바 제목
        aDialog.setView(layout); //dialog.xml 파일을 뷰로 셋팅

        //그냥 닫기버튼을 위한 부분
        aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        //팝업창 생성
        AlertDialog ad = aDialog.create();
        ad.show();//보여줌!
    }
}
