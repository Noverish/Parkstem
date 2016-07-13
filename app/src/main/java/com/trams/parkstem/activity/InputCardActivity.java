package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.custom_view.LocationChangeableListView;

/**
 * Created by Noverish on 2016-07-04.
 */
public class InputCardActivity extends BaseBackSearchActivity {
    private LocationChangeableListView listView;
    private TextView mainCardTextView;
    private RelativeLayout inputNewCardButton;

    RelativeLayout register_card;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_card);
        setToolbarTitle("카드 등록");

        mainCardTextView = (TextView) findViewById(R.id.input_first_card);
        
        listView = (LocationChangeableListView) findViewById(R.id.activity_input_card_list_view);
        listView.setMainItemImage(ContextCompat.getDrawable(this, R.drawable.main_card));
        listView.setOnMainItemChangeListener(new LocationChangeableListView.OnMainItemChangeListener() {
            @Override
            public void onMainItemChanged() {
                mainCardTextView.setText(listView.getMainItemContent());
            }
        });

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
