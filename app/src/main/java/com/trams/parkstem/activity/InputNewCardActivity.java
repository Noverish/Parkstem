package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trams.parkstem.R;

/**
 * Created by Noverish on 2016-07-04.
 */
public class InputNewCardActivity extends AppCompatActivity {

    private final int CARD_TYPE_PERSON = 0;
    private final int CARD_TYPE_COMPANY = 1;
    private int cardType;

    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_new_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout cardTypeCompanyRadioButton = (LinearLayout) findViewById(R.id.activity_input_new_card_radio_company);
        cardTypeCompanyRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonOnClick((LinearLayout) v);
            }
        });
        LinearLayout cardTypePersonRadioButton = (LinearLayout) findViewById(R.id.activity_input_new_card_radio_person);
        cardTypePersonRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonOnClick((LinearLayout) v);
            }
        });
        cardType = CARD_TYPE_PERSON;
    }

    private void radioButtonOnClick(LinearLayout view) {
        ImageView companyImage = (ImageView) findViewById(R.id.activity_input_new_card_radio_company_image);
        TextView companyText = (TextView) findViewById(R.id.activity_input_new_card_radio_company_text);
        ImageView personImage = (ImageView) findViewById(R.id.activity_input_new_card_radio_person_image);
        TextView peresonText = (TextView) findViewById(R.id.activity_input_new_card_radio_person_text);

        if(view.getId() == R.id.activity_input_new_card_radio_company && cardType == CARD_TYPE_PERSON) {
            cardType = CARD_TYPE_COMPANY;
            companyImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_ground));
            companyText.setTextColor(ContextCompat.getColor(this, R.color.BLACK));
            personImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_round));
            peresonText.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }
        if(view.getId() == R.id.activity_input_new_card_radio_person && cardType == CARD_TYPE_COMPANY){
            cardType = CARD_TYPE_PERSON;
            companyImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_round));
            companyText.setTextColor(ContextCompat.getColor(this, R.color.gray));
            personImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_ground));
            peresonText.setTextColor(ContextCompat.getColor(this, R.color.BLACK));
        }
    }
}
