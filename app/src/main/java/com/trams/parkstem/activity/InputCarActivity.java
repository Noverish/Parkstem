package com.trams.parkstem.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.custom_view.LocationChangeableListView;
import com.trams.parkstem.server.ServerClient;

import java.util.ArrayList;

/**
 * Created by Noverish on 2016-07-04.
 */
public class InputCarActivity extends AppCompatActivity {
    private EditText carNumber;
    private LocationChangeableListView listView;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_car);

        context = this;
        listView = (LocationChangeableListView) findViewById(R.id.activity_input_car_list_view);
        listView.setMainItemImage(ContextCompat.getDrawable(this, R.drawable.main_car));

        ArrayList<Pair<Long, String>> listData = new ArrayList<>();
        for(int i = 0;i<10;i++) {
            listData.add(new Pair<>((long)i, "item " + i));
        }
        listView.setListData(listData);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RelativeLayout inputNewCarButton = (RelativeLayout) findViewById(R.id.input_new_car_button);
        inputNewCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditStatus();
            }
        });

        RelativeLayout addingConfirmButton = (RelativeLayout) findViewById(R.id.activity_input_car_adding_confirm);
        addingConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCarNumber();
                setNotEditStatus();
            }
        });

        carNumber = (EditText) findViewById(R.id.activity_input_car_add_number);
        carNumber.addTextChangedListener(new TextWatcher() {
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
                    carNumber.setText(result);
                    carNumber.setSelection(result.length());
                    // alert the user
                }
            }
        });


    }

    private void setEditStatus() {
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
        String carNumberStr = carNumber.getText().toString();

        if(ServerClient.getInstance().regcar(carNumberStr))
            Toast.makeText(this, "차량번호 " + carNumberStr + "가 정상적으로 등록되었습니다",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "차량번호 등록이 실패하였습니다 다시 시도해 주세요",Toast.LENGTH_SHORT).show();

    }

}
