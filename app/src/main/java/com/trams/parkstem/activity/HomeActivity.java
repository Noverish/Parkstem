package com.trams.parkstem.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseNavigationActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.HistoryParkView;

public class HomeActivity extends BaseNavigationActivity {
    private ServerClient client = ServerClient.getInstance();

    private RelativeLayout hipassButton;
    private boolean hipassOn;
    private Context context;

    private final String TAG = getClass().getSimpleName();

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

        try{
            ServerClient.ParkHistoryList parkHistoryList = ServerClient.getInstance().parkHistory();
            if(parkHistoryList.data.size()>0){
                HistoryParkView historyParkView = new HistoryParkView(this, parkHistoryList.data.get(0));
                historyParkView.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_3));
                ((LinearLayout) findViewById(R.id.activity_home_park_list_1)).addView(historyParkView);
            }

            if(parkHistoryList.data.size()>1){
                HistoryParkView historyParkView = new HistoryParkView(this, parkHistoryList.data.get(1));
                historyParkView.setBackgroundColor(ContextCompat.getColor(this, R.color.WHITE));
                ((LinearLayout) findViewById(R.id.activity_home_park_list_2)).addView(historyParkView);
            }

        } catch (ServerClient.ServerErrorException ex){
            Log.e("ERROr!",ex.toString());
        }

        ImageView alert = (ImageView) findViewById(R.id.activity_home_about_hipass); //팝업버튼선언
        alert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.e("asdf","popup");
                Context context = HomeActivity.this;
                Essentials.makeHipassPopup(context, (ViewGroup) HomeActivity.this.findViewById(R.id.content_main_layout));
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

        RelativeLayout moreButton = (RelativeLayout) findViewById(R.id.activity_home_more_button);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HistoryParkActivity.class);
                startActivity(intent);
            }
        });


    }

    private void onHipassButtonClicked() {
        ImageView human = (ImageView) findViewById(R.id.activity_highpass_human_image);

        hipassOn = !hipassOn;

        if(hipassOn) {
            try {
                client.hipassOn("Y");
                hipassButton.removeAllViews();
                getLayoutInflater().inflate(R.layout.hipass_button_on, hipassButton);
                human.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_home_person_2));
            } catch (ServerClient.ServerErrorException ex) {
                Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                client.hipassOn("N");
                hipassButton.removeAllViews();
                getLayoutInflater().inflate(R.layout.hipass_button_off, hipassButton);
                human.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_home_person));
            } catch (ServerClient.ServerErrorException ex) {
                Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("TAG",requestCode + " " + resultCode);

        if(resultCode == RESULT_FINISH)
            finish();
    }
}
