package com.trams.parkstem.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.webview.CardRegister;
import com.trams.parkstem.webview.Mobilecertification;

import java.util.ArrayList;

/**
 * Created by Noverish on 2016-07-04.
 */
public class InputCardActivity extends BaseBackSearchActivity {
    private ServerClient serverClient;

    private EditText cardNameEditText;
    private TextView mainCardTextView;
    private LocationChangeableListView listView;
    private boolean inEditStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_car);
        setToolbarTitle("카드 등록");
        this.inEditStatus = false;
        this.serverClient = ServerClient.getInstance();

        listView = (LocationChangeableListView) findViewById(R.id.activity_input_car_list_view);
        listView.setMainItemImage(ContextCompat.getDrawable(this, R.drawable.main_car));
        listView.setDefaultMainItem(getString(R.string.action_card_register));
        listView.setOnEditCompleteListener(new LocationChangeableListView.OnEditCompleteListener() {
            @Override
            public void onEditCompleted(Pair<Long, String> mainItem) {
                try {
                    serverClient.cardSort(mainItem.first + "");
                } catch (ServerClient.ServerErrorException ex) {
                    Toast.makeText(InputCardActivity.this, ex.msg, Toast.LENGTH_SHORT).show();
                }
                refresh();
            }
        });
        listView.setOnItemRemovedListener(new LocationChangeableListView.OnItemRemovedListener() {
            @Override
            public void onItemRemoved(Pair<Long, String> removeItemList) {
                try {
                    serverClient.cardDelete(removeItemList.first + "");
                } catch (ServerClient.ServerErrorException ex) {
                    Toast.makeText(InputCardActivity.this, ex.msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        RelativeLayout inputNewCarButton = (RelativeLayout) findViewById(R.id.input_new_car_button);
        inputNewCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddCarButtonPressed();
            }
        });

        RelativeLayout addingConfirmButton = (RelativeLayout) findViewById(R.id.activity_input_car_adding_confirm);
        addingConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCarNumber();
            }
        });

        TextView addButtonText = (TextView) findViewById(R.id.activity_input_car_button_text);
        addButtonText.setText("카드추가등록");

        ImageView carImage = (ImageView) findViewById(R.id.activity_input_car_image);
        carImage.setVisibility(View.INVISIBLE);

        mainCardTextView = (TextView) findViewById(R.id.input_first_car_text);

        cardNameEditText = (EditText) findViewById(R.id.activity_input_car_add_number);
        cardNameEditText.setHint("카드 별칭");
        cardNameEditText.addTextChangedListener(new TextWatcher() {
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
                    cardNameEditText.setText(result);
                    cardNameEditText.setSelection(result.length());
                    // alert the user
                }
            }
        });
    }

    private void refresh() {
        //get card list from server
        try {
            ArrayList<ServerClient.CardInfo> infos = serverClient.cardList().data;

            ArrayList<Pair<Long, String>> data = new ArrayList<>();
            for(ServerClient.CardInfo info : infos) {
                Pair<Long, String> pair = new Pair<>((long)info.idx, info.card_name);
                if(info.sort == 0)
                    data.add(0, pair);
                else
                    data.add(pair);
            }

            listView.setListData(data);
        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
        }

        cardNameEditText.setText("");
        mainCardTextView.setText(listView.getMainItem().second);

    }

    private void onAddCarButtonPressed() {
        if(ServerClient.getInstance().isUserCertification()) {
            setEditStatus();

            //show keyboard manually
            cardNameEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(cardNameEditText, 0);
        } else {
            Toast.makeText(this, "차량 등록을 하시려면 휴대폰 인증을 하셔야 합니다", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Mobilecertification.class));
        }
    }

    private void setEditStatus() {
        inEditStatus = true;

        ImageView gray2 = (ImageView) findViewById(R.id.activity_input_car_gray2);
        ImageView gray3 = (ImageView) findViewById(R.id.activity_input_car_gray3);
        gray2.setVisibility(View.VISIBLE);
        gray3.setVisibility(View.VISIBLE);

        RelativeLayout editLayout = (RelativeLayout) findViewById(R.id.activity_input_car_editing);
        editLayout.setVisibility(View.VISIBLE);

        TextView inputCardText = (TextView) findViewById(R.id.input_first_car_text);
        inputCardText.setVisibility(View.INVISIBLE);
    }

    private void setNotEditStatus() {
        inEditStatus = false;

        ImageView gray2 = (ImageView) findViewById(R.id.activity_input_car_gray2);
        ImageView gray3 = (ImageView) findViewById(R.id.activity_input_car_gray3);
        gray2.setVisibility(View.INVISIBLE);
        gray3.setVisibility(View.INVISIBLE);

        RelativeLayout editLayout = (RelativeLayout) findViewById(R.id.activity_input_car_editing);
        editLayout.setVisibility(View.INVISIBLE);

        TextView inputCardText = (TextView) findViewById(R.id.input_first_car_text);
        inputCardText.setVisibility(View.VISIBLE);

    }

    private void addCarNumber() {
        String carNumberStr = cardNameEditText.getText().toString();
        if(carNumberStr.matches("[ㄱ-ㅎ가-힣A-Za-z]*")) {
            setNotEditStatus();

            //hide keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(cardNameEditText.getWindowToken(), 0);

            Intent intent = new Intent(this, CardRegister.class);
            intent.putExtra("cardName",cardNameEditText.getText().toString());
            startActivityForResult(intent, RESULT_OK);

            setNotEditStatus();

        } else {
            Toast.makeText(this, "카드 이름에는 한글과 영어만 사용할 수 있습니다",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if(inEditStatus)
            setNotEditStatus();
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("refresh","refresh");
        refresh();
    }
}

