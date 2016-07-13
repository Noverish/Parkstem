package com.trams.parkstem.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseNavigationActivity;
import com.trams.parkstem.server.ServerClient;

public class HomeActivity extends BaseNavigationActivity {
    private RelativeLayout hipassButton;
    private boolean hipassOn;
    private Context context;

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

        RelativeLayout move1, move2;
        move1 = (RelativeLayout) findViewById(R.id.activity_hipass_tocarregister);
        move1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                movefromHipasstoCarRegister();
            }
        });
        move2 = (RelativeLayout) findViewById(R.id.activity_hipass_tocardregister);
        move2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                movefromHipasstoCardRegister();
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

    }

    private void onHipassButtonClicked() {
        ImageView human = (ImageView) findViewById(R.id.activity_highpass_human_image);
        hipassButton.removeAllViews();

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
    }


    private void movefromHipasstoCarRegister(){
        Intent intent = new Intent(this, InputCarActivity.class);
        startActivity(intent);
    }

    private void movefromHipasstoCardRegister(){
        Intent intent = new Intent(this, InputCardActivity.class);
        startActivity(intent);
    }



}
