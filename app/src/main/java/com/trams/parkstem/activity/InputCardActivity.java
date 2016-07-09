package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;

/**
 * Created by Noverish on 2016-07-04.
 */
public class InputCardActivity extends AppCompatActivity {
    RelativeLayout register_card;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        register_card = (RelativeLayout) findViewById(R.id.activity_input_card_register);
        register_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_new_card();
            }
        });
    }

    private void register_new_card(){
        Intent intent = new Intent(this, InputNewCardActivity.class);
        startActivity(intent);
    }
}
