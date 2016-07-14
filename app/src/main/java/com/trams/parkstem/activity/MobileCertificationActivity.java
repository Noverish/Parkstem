package com.trams.parkstem.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;

/**
 * Created by Noverish on 2016-07-04.
 */
public class MobileCertificationActivity extends BaseBackSearchActivity {
    boolean isItMan;
    ImageView man, woman;
    RelativeLayout movetocar, movetocard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_certification);

        isItMan = true;

        man = (ImageView) findViewById(R.id.activity_certification_man);
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSex(true);
            }
        });
        woman = (ImageView) findViewById(R.id.activity_certification_woman);
        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSex(false);
            }
        });

    }

    private void changeSex(boolean isItMan) {
        this.isItMan = isItMan;

        if(isItMan){
            Drawable man1 = ContextCompat.getDrawable(this, R.drawable.btn_m_1);
            man.setImageDrawable(man1);
            Drawable woman1 = ContextCompat.getDrawable(this, R.drawable.btn_w_2);
            woman.setImageDrawable(woman1);
        }
        else{
            Drawable man1 = ContextCompat.getDrawable(this, R.drawable.btn_m_2);
            man.setImageDrawable(man1);
            Drawable woman1 = ContextCompat.getDrawable(this, R.drawable.btn_w_1);
            woman.setImageDrawable(woman1);
        }
    }
}
