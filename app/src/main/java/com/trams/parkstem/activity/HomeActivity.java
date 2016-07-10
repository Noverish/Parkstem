package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseNavigationActivity;
import com.trams.parkstem.server.ServerClient;

public class HomeActivity extends BaseNavigationActivity {
    private RelativeLayout hipassButton;
    private boolean hipassOn;

    private final String TAG = getClass().getSimpleName();
    ServerClient SC = ServerClient.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

    }

    private void onHipassButtonClicked() {
        ImageView human = (ImageView) findViewById(R.id.activity_highpass_human_image);
        hipassButton.removeAllViews();

        if(hipassOn) {
            getLayoutInflater().inflate(R.layout.hipass_button_off, hipassButton);
            SC.hipassOn("Y");
            human.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_home_person));

        } else {
            SC.hipassOn("N");
            getLayoutInflater().inflate(R.layout.hipass_button_on, hipassButton);
            human.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_home_person_2));

        }

        hipassOn = !hipassOn;
    }


    private void movefromHipasstoCarRegister(){
        Intent intent = new Intent(this, InputCarActivityBase.class);
        startActivity(intent);
    }
    private void movefromHipasstoCardRegister(){
        Intent intent = new Intent(this, InputCardActivityBase.class);
        startActivity(intent);
    }



}
