package com.trams.parkstem.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.others.HttpImageThread;
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

            ImageView imageView = (ImageView) findViewById(R.id.activity_park_state_image);

            TextView parkName = (TextView) findViewById(R.id.activity_park_state_park_name);
            TextView parkAddress = (TextView) findViewById(R.id.activity_park_state_park_address);
            TextView baseMinute = (TextView) findViewById(R.id.activity_park_state_base_minute);
            TextView basePrice = (TextView) findViewById(R.id.activity_park_state_base_price);
            TextView parkPriceTime1 = (TextView) findViewById(R.id.activity_park_state_park_price_time1);
            TextView parkPrice = (TextView) findViewById(R.id.activity_park_state_park_price);

            TextView parkPriceTime2 = (TextView) findViewById(R.id.activity_park_state_park_price_time2);

            TextView carInTime = (TextView) findViewById(R.id.activity_park_state_car_in_time);
            TextView carOutTime = (TextView)findViewById(R.id.activity_park_state_car_out_time);
            TextView totalAmount = (TextView) findViewById(R.id.activity_park_state_total_amount);

            try {
                HttpImageThread thread = new HttpImageThread(parkInfo.local_photo1);
                imageView.setBackground(new BitmapDrawable(getResources(), thread.getImage()));
            } catch (NullPointerException ex) {

            }

            parkName.setText(parkInfo.local_name);
            parkAddress.setText(parkInfo.local_address);

            baseMinute.setText(parkInfo.base_minute + "");
            basePrice.setText(parkInfo.base_price);

            parkPriceTime1.setText(parkInfo.park_price_time + "");
            parkPriceTime2.setText(parkInfo.park_price_time + "");
            parkPrice.setText(parkInfo.park_price);

            if(recentCar.in_date != null)
                carInTime.setText(Essentials.calendarToTime(recentCar.in_date));
            else
                carInTime.setText("");

            if(recentCar.out_date != null)
                carOutTime.setText(Essentials.calendarToTime(recentCar.out_date));
            else
                carOutTime.setText("");


            if(!recentCar.total.equals(""))
                totalAmount.setText(Essentials.WON_SYMBOL + Essentials.numberWithComma(recentCar.total));
            else
                totalAmount.setText(Essentials.WON_SYMBOL + "0");


        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        if(intent != null) {
            boolean alert = intent.getBooleanExtra("alert", false);

            if(alert) {
                Essentials.alertParkState(this);
            }
        }
    }
}
