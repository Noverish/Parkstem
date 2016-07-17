package com.trams.parkstem.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseNavigationActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

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

        alert = (ImageView) findViewById(R.id.activity_home_about_hipass);
        alert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Context context = HomeActivity.this;
                String title = context.getString(R.string.popup_hipass_explain_title);
                String content = context.getString(R.string.popup_hipass_explain_content);
                //Essentials.makePopup(context, title, content);
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

        if(hipassOn) {
            try {
                client.hipassOn("Y");
                hipassButton.removeAllViews();
                getLayoutInflater().inflate(R.layout.hipass_button_off, hipassButton);
                human.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_home_person));
            } catch (ServerClient.ServerErrorException ex) {
                Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                client.hipassOn("N");
                hipassButton.removeAllViews();
                getLayoutInflater().inflate(R.layout.hipass_button_on, hipassButton);
                human.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_home_person_2));
            } catch (ServerClient.ServerErrorException ex) {
                Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
            }
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
