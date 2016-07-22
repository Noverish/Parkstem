package com.trams.parkstem.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.clause.Clause1;
import com.trams.parkstem.clause.Clause2;
import com.trams.parkstem.clause.Clause3;
import com.trams.parkstem.clause.Clause4;
import com.trams.parkstem.clause.Clause5;
import com.trams.parkstem.others.GlobalApplication;
import com.trams.parkstem.server.ServerClient;

import java.util.Calendar;

/**
 * Created by Noverish on 2016-07-04.
 */
public class InputNewCardActivity extends BaseBackSearchActivity {
    private static final int CARD_TYPE_PERSON = 0;
    private static final int CARD_TYPE_COMPANY = 1;
    private int cardType;

    private ServerClient serverClient = new ServerClient();
    private TextView clause1, clause2, clause3, clause4, clause5;
    private String clause;
    Context context = this;

    private HidePasswordView hidePasswordView;
    private Spinner selectMonthSpinner;
    private Spinner selectYearSpinner;

    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_new_card);

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

        hidePasswordView = (HidePasswordView) findViewById(R.id.activity_input_new_card_hide_password_view);

        initDurationSelectSpinners();

        clause1 = (TextView) findViewById(R.id.input_card_clause_1);
        clause2 = (TextView) findViewById(R.id.input_card_clause_2);
        clause3 = (TextView) findViewById(R.id.input_card_clause_3);
        clause4 = (TextView) findViewById(R.id.input_card_clause_4);
        clause5 = (TextView) findViewById(R.id.input_card_clause_5);

        clause1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputNewCardActivity.this , Clause1.class);
                startActivity(intent);
            }
        });
        clause2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputNewCardActivity.this , Clause2.class);
                startActivity(intent);
            }
        });
        clause3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputNewCardActivity.this , Clause3.class);
                startActivity(intent);
            }
        });
        clause4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputNewCardActivity.this , Clause4.class);
                startActivity(intent);
            }
        });
        clause5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputNewCardActivity.this , Clause5.class);
                startActivity(intent);
            }
        });
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

    private void initDurationSelectSpinners() {
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        for(int i = 1;i<=12;i++) {
            monthAdapter.add(i + "월");
        }

        selectMonthSpinner = (Spinner) findViewById(R.id.input_new_card_duration_month);
        selectMonthSpinner.setAdapter(monthAdapter);

        Calendar now = Calendar.getInstance();
        int nowYear = now.get(Calendar.YEAR);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        for(int i = 0;i<=11;i++) {
            yearAdapter.add((nowYear + i) + "년");
        }

        selectYearSpinner = (Spinner) findViewById(R.id.input_new_card_duration_year);
        selectYearSpinner.setAdapter(yearAdapter);
    }

    /**
     * Created by Noverish on 2016-07-08.
     */
    public static class HidePasswordView extends RelativeLayout {
        private Context context;
        private final String TAG = getClass().getSimpleName();

        private String password = "";
        private final static int PASSWORD_LENGTH = 4;

        private EditText passwordEditText;
        private ImageView[] digit = new ImageView[PASSWORD_LENGTH];
        private Drawable typed, notTyped;

        public HidePasswordView(Context context) {
            super(context);
            if(!isInEditMode())
                init(context);
        }

        public HidePasswordView(Context context, AttributeSet attrs) {
            super(context, attrs);
            if(!isInEditMode())
                init(context);
        }

        private void init(Context context) {
            this.context = context;

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.hide_password_view, this);

            passwordEditText = (EditText) findViewById(R.id.hide_password_view_edittext);
            passwordEditText.setText(" ");
            passwordEditText.setSelection(passwordEditText.getText().length()); //move cursor end of EditText
            passwordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString();

                    if(!text.equals(" ")) {
                        if (text.equals(""))
                            deleteExistNumber();
                        else
                            inputNewNumber(text.substring(1, 2));
                        passwordEditText.setText(" ");
                        passwordEditText.setSelection(passwordEditText.getText().length()); //move cursor end of EditText
                    }

                }
            });

            digit[0] = (ImageView) findViewById(R.id.hide_password_view_digit0);
            digit[1] = (ImageView) findViewById(R.id.hide_password_view_digit1);
            digit[2] = (ImageView) findViewById(R.id.hide_password_view_digit2);
            digit[3] = (ImageView) findViewById(R.id.hide_password_view_digit3);

            typed = ContextCompat.getDrawable(context, R.drawable.img_ground);
            notTyped = ContextCompat.getDrawable(context, R.drawable.img_round);
        }

        private void inputNewNumber(String number) {
            if(password.length() < PASSWORD_LENGTH) {
                password += number;
                refreshImage();
            } else {
            }
        }

        private void deleteExistNumber() {
            password = password.substring(0, password.length() - 1);
            refreshImage();
        }

        //change image of 4 digit image view
        private void refreshImage() {
            int i = 0;
            for(;i<password.length();i++) {
                digit[i].setImageDrawable(typed);
            }
            for(; i< PASSWORD_LENGTH; i++) {
                digit[i].setImageDrawable(notTyped);
            }
        }

        public String getPassword() {
            return password;
        }
    }


}
