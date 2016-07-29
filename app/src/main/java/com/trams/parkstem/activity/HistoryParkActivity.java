package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;
import com.trams.parkstem.view.HistoryParkView;

/**
 * Created by Noverish on 2016-07-13.
 */
public class HistoryParkActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_park);

        LinearLayout content = (LinearLayout) findViewById(R.id.activity_history_park_content_layout);


        ServerClient.ParkHistoryList list;

        try {
            list = ServerClient.getInstance().parkHistory();
            int count=0;

            if(list.data.size() != 0) {
                TextView textView = (TextView) findViewById(R.id.activity_history_park_no_item);
                ((ViewGroup)textView.getParent()).removeView(textView);
            }

            for(ServerClient.ParkHistory parkHistory : list.data){
                HistoryParkView historyParkView = new HistoryParkView(this, parkHistory);

                if(count%2==0)
                    historyParkView.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_3));

                if(count==0){
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                    historyParkView.setLayoutParams(layoutParams);
                }

                content.addView(historyParkView);

                if(count==0)
                    LayoutInflater.from(this).inflate(R.layout.history_payment_item_bar, content);

                count++;
            }
        } catch (ServerClient.ServerErrorException ex) {
            Log.e("error!",ex.msg);
        }
    }
}
