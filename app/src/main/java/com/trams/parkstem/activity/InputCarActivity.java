package com.trams.parkstem.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.custom_view.LocationChangeableListView;

/**
 * Created by Noverish on 2016-07-04.
 */
public class InputCarActivity extends BaseBackSearchActivity {
    private EditText carNumberEditText;
    private TextView mainCarNumberTextView;
    private LocationChangeableListView listView;
    private Context context;
    private boolean inEditStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_car);
        setToolbarTitle("차량 등록");
        this.inEditStatus = false;

        context = this;
        listView = (LocationChangeableListView) findViewById(R.id.activity_input_car_list_view);
        listView.setMainItemImage(ContextCompat.getDrawable(this, R.drawable.main_car));
        listView.setOnMainItemChangeListener(new LocationChangeableListView.OnMainItemChangeListener() {
            @Override
            public void onMainItemChanged() {
                refresh();
            }
        });

        RelativeLayout inputNewCarButton = (RelativeLayout) findViewById(R.id.input_new_car_button);
        inputNewCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditStatus();

                //show keyboard manually

                carNumberEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(carNumberEditText, 0);
            }
        });

        RelativeLayout addingConfirmButton = (RelativeLayout) findViewById(R.id.activity_input_car_adding_confirm);
        addingConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCarNumber();
            }
        });

        mainCarNumberTextView = (TextView) findViewById(R.id.input_first_car_text);

        carNumberEditText = (EditText) findViewById(R.id.activity_input_car_add_number);
        carNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    carNumberEditText.setText(result);
                    carNumberEditText.setSelection(result.length());
                    // alert the user
                }
            }
        });

        refresh();
    }

    private void refresh() {
        carNumberEditText.setText("");
        mainCarNumberTextView.setText(listView.getMainItemContent());

    }

    private void setEditStatus() {
        inEditStatus = true;

        ImageView gray1 = (ImageView) findViewById(R.id.activity_input_car_gray1);
        ImageView gray2 = (ImageView) findViewById(R.id.activity_input_car_gray2);
        ImageView gray3 = (ImageView) findViewById(R.id.activity_input_car_gray3);
        gray1.setVisibility(View.VISIBLE);
        gray2.setVisibility(View.VISIBLE);
        gray3.setVisibility(View.VISIBLE);

        RelativeLayout editLayout = (RelativeLayout) findViewById(R.id.activity_input_car_editing);
        editLayout.setVisibility(View.VISIBLE);

        TextView inputCardText = (TextView) findViewById(R.id.input_first_car_text);
        inputCardText.setVisibility(View.INVISIBLE);
    }

    private void setNotEditStatus() {
        inEditStatus = false;

        ImageView gray1 = (ImageView) findViewById(R.id.activity_input_car_gray1);
        ImageView gray2 = (ImageView) findViewById(R.id.activity_input_car_gray2);
        ImageView gray3 = (ImageView) findViewById(R.id.activity_input_car_gray3);
        gray1.setVisibility(View.INVISIBLE);
        gray2.setVisibility(View.INVISIBLE);
        gray3.setVisibility(View.INVISIBLE);

        RelativeLayout editLayout = (RelativeLayout) findViewById(R.id.activity_input_car_editing);
        editLayout.setVisibility(View.INVISIBLE);

        TextView inputCardText = (TextView) findViewById(R.id.input_first_car_text);
        inputCardText.setVisibility(View.VISIBLE);

    }

    private void addCarNumber() {
        String carNumberStr = carNumberEditText.getText().toString();
        if(carNumberStr.matches("\\d{2}\\D\\d{4}")) {
            setNotEditStatus();

            //hide keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(carNumberEditText.getWindowToken(), 0);

            listView.addData(carNumberStr);
            refresh();
/*
            if(ServerClient.getInstance().RegisterCar(carNumberStr))
                Toast.makeText(this, "차량번호 " + carNumberStr + "가 정상적으로 등록되었습니다",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "차량번호 등록이 실패하였습니다 다시 시도해 주세요",Toast.LENGTH_SHORT).show();*/
        } else {
            Toast.makeText(this, "잘못된 차량번호 입니다.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if(inEditStatus)
            setNotEditStatus();
        else
            super.onBackPressed();
    }
}
