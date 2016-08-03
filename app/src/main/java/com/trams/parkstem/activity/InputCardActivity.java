package com.trams.parkstem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
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

    private LocationChangeableListView listView;
    private TextView mainCardTextView;
    private RelativeLayout inputNewCardButton;

    RelativeLayout register_card;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_card);
        setToolbarTitle("카드 등록");
        this.serverClient = ServerClient.getInstance();

        mainCardTextView = (TextView) findViewById(R.id.input_first_card);

        listView = (LocationChangeableListView) findViewById(R.id.activity_input_card_list_view);
        listView.setMainItemImage(ContextCompat.getDrawable(this, R.drawable.main_card));
        listView.setDefaultMainItem(getString(R.string.action_card_register));
        listView.setOnEditCompleteListener(new LocationChangeableListView.OnEditCompleteListener() {
            @Override
            public void onEditCompleted(Pair<Long, String> mainItem) {
                refresh();
                try {
                    serverClient.cardSort(mainItem.first + "");
                } catch (ServerClient.ServerErrorException ex) {
                    Toast.makeText(InputCardActivity.this, ex.msg, Toast.LENGTH_SHORT).show();
                }
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

        register_card = (RelativeLayout) findViewById(R.id.activity_input_card_register);
        register_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_new_card();
            }
        });

        refresh();
    }

    private void register_new_card(){
        if(ServerClient.getInstance().isUserCertification()) {
            Intent intent = new Intent(this, CardRegister.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "카드 등록을 하시려면 휴대폰 인증을 하셔야 합니다", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Mobilecertification.class));
        }
    }

    private void refresh() {
        //get card list from server
        try {
            ArrayList<ServerClient.CardInfo> infos = serverClient.cardList().data;

            ArrayList<Pair<Long, String>> data = new ArrayList<>();
            for(ServerClient.CardInfo info : infos) {
                Log.e("info",info.idx + " " + info.card_name);
                Pair<Long, String> pair = new Pair<>((long)info.idx, info.card_name);
                data.add(pair);
            }

            listView.setListData(data);
        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
        }

        mainCardTextView.setText(listView.getMainItem().second);
    }
}

