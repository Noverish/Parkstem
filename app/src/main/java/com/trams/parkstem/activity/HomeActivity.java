package com.trams.parkstem.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseNavigationActivity;
import com.trams.parkstem.server.ServerClient;

import org.json.JSONObject;

import java.util.Iterator;

public class HomeActivity extends BaseNavigationActivity {
    ImageView alert; //팝업버튼선언
    private RelativeLayout hipassButton;
    private boolean hipassOn;
    private Context context;
    RelativeLayout movetocar, movetocard;

    private final String TAG = getClass().getSimpleName();
    ServerClient client = ServerClient.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.context = this;

        hipassOn = false;
        hipassButton = (RelativeLayout) findViewById(R.id.activity_hipass_on_off_button);
        hipassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHipassButtonClicked();
            }
        });


        movetocar = (RelativeLayout) findViewById(R.id.activity_certification_input_car);
        movetocar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                movetocarRegister();
            }
        });
        movetocard = (RelativeLayout) findViewById(R.id.activity_certification_input_card);
        movetocard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                movetocardRegister();
            }
        });

        alert = (ImageView) findViewById(R.id.activity_home_about_hipass);
        alert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                popup_clause();
            }
        });

        LinearLayout ticketMobile = (LinearLayout) findViewById(R.id.activity_home_ticket_mobile_button);
        ticketMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TicketMobileListActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout ticketPurchase = (LinearLayout) findViewById(R.id.activity_home_ticket_purchase_button);
        ticketPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TicketPurchaseListActivity.class);
                startActivity(intent);
            }
        });

        test();
    }


    private void test(){
        try{
            ServerClient serverClient = ServerClient.getInstance();

            JSONObject result = serverClient.login("hongid1234", "hongpw1234");

            Iterator<String> iter = result.keys();
            while(iter.hasNext()){
                String key = iter.next();
                String str = result.getString(key);
                Log.e("JSON",key + ":" + str);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void onHipassButtonClicked() {
        ImageView human = (ImageView) findViewById(R.id.activity_highpass_human_image);
        hipassButton.removeAllViews();

        try{

            if(hipassOn) {
                getLayoutInflater().inflate(R.layout.hipass_button_off, hipassButton);
                client.hipassOn("Y");
                human.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_home_person));

            } else {
                client.hipassOn("N");
                getLayoutInflater().inflate(R.layout.hipass_button_on, hipassButton);
                human.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_home_person_2));

            }

            hipassOn = !hipassOn;
        }catch(ServerClient.ServerErrorException ex){
            ex.printStackTrace();
        }
    }


    private void movetocarRegister(){
        Intent intent = new Intent(this, InputCarActivity.class);
        startActivity(intent);
    }
    private void movetocardRegister(){
        Intent intent = new Intent(this, InputCardActivity.class);
        startActivity(intent);
    }

    public void popup_clause(){
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        //R.layout.dialog는 xml 파일명이고  R.id.popup은 보여줄 레이아웃 아이디
        View layout = inflater.inflate(R.layout.popup_hipass, (ViewGroup) findViewById(R.id.popup_hipass_content));
        AlertDialog.Builder aDialog = new AlertDialog.Builder(this);

        aDialog.setTitle("하이패스란 무엇인가"); //타이틀바 제목
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
