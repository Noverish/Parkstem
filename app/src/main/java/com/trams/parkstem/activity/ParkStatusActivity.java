package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-08.
 */
public class ParkStatusActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_state);

        try {
            ServerClient.RecentCar recentCar = ServerClient.getInstance().recentCar();
            ServerClient.ParkInfo parkInfo = ServerClient.getInstance().parkInfo(recentCar.local_id);

            TextView parkName = (TextView) findViewById(R.id.activity_park_state_park_name);
            TextView parkAddress = (TextView) findViewById(R.id.activity_park_state_park_address);
            TextView parkPriceTime = (TextView) findViewById(R.id.activity_park_state_park_price_time);
            TextView carInTime = (TextView) findViewById(R.id.activity_park_state_car_in_time);
            TextView carOutItme = (TextView)findViewById(R.id.activity_park_state_car_out_time);
            TextView totalAmount = (TextView) findViewById(R.id.activity_park_state_total_amount);

            parkName.setText(parkInfo.local_name);
            parkAddress.setText(parkInfo.local_address);
            parkPriceTime.setText(parkInfo.park_price_time + "");

            if(recentCar.in_date != null)
                carInTime.setText(Essentials.calendarToTime(recentCar.in_date));
            else
                carInTime.setText("");

            if(recentCar.out_date != null)
                carOutItme.setText(Essentials.calendarToTime(recentCar.out_date));
            else
                carOutItme.setText("");


            if(!recentCar.total.equals(""))
                totalAmount.setText(Essentials.WON_SYMBOL + Essentials.numberWithComma(recentCar.total));
            else
                totalAmount.setText(Essentials.WON_SYMBOL + "0");


        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
        }
    }
}
