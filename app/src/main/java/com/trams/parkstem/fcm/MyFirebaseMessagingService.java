package com.trams.parkstem.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trams.parkstem.R;
import com.trams.parkstem.activity.ParkStatusActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

import java.util.Map;

/**
 * Created by Noverish on 2016-10-18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String PUSH_RECEIVE = "push_receive";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        String title = data.get("data1");
        String msg = data.get("data2");

        sendNotification();
    }

    /**
     * 실제 디바에스에 GCM으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     */
    private void sendNotification() {
        Intent intent = new Intent(getApplicationContext(), ParkStatusActivity.class);
        intent.putExtra("alert",true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent , PendingIntent.FLAG_ONE_SHOT);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        try {
            ServerClient.RecentCar recentCar = ServerClient.getInstance().recentCar();
            ServerClient.ParkInfo parkInfo = ServerClient.getInstance().parkInfo(recentCar.local_id);

            if(recentCar.in_date != null && recentCar.out_date == null) {
                Notification noti = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("차량 입차")
                        .setContentText(parkInfo.local_name + " " + Essentials.calendarToTime(recentCar.in_date) + " 입차")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker("파크스템")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .build();
                manager.notify(1, noti);
            } else if (recentCar.in_date != null){
                Notification noti = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("차량 출차")
                        .setContentText(parkInfo.local_name + " " + Essentials.calendarToTime(recentCar.out_date) + " 출차")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker("파크스템")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .build();
                manager.notify(1, noti);
            } else {
                Notification noti = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("ERROR")
                        .setContentText("ERROR - 입차시간과 출차시간이 없습니다.")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker("파크스템")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .build();
                manager.notify(1, noti);
            }

        } catch (ServerClient.ServerErrorException error) {
            error.printStackTrace();

            Notification noti = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("ERROR")
                    .setContentText("ERROR - " + error.msg)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("파크스템")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .build();
            manager.notify(1, noti);
        }

        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(PUSH_RECEIVE));
    }
}

