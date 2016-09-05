package com.trams.parkstem.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.trams.parkstem.R;
import com.trams.parkstem.activity.ParkStatusActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-07-18.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    public static final String PUSH_RECEIVE = "push_receive";

    /**
     *
     * @param from SenderID 값을 받아온다.
     * @param data Set형태로 GCM으로 받은 데이터 payload이다.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String message = data.getString("message");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "data: " + data.toString());

        // GCM으로 받은 메세지를 디바이스에 알려주는 sendNotification()을 호출한다.
        sendNotification(title, message);
    }


    /**
     * 실제 디바에스에 GCM으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     * @param title
     * @param message
     */
    private void sendNotification(String title, String message) {
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