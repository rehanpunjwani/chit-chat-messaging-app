package com.example.chitchat.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import com.example.chitchat.R;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;



@RequiresApi(api = Build.VERSION_CODES.O)
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private  String CHANNEL_ID = "personal_notifications";
    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,"personal_notifications",NotificationManager.IMPORTANCE_DEFAULT);


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_message = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();


            String user_id = remoteMessage.getData().get("user_id");
            String key = remoteMessage.getData().get("key");


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.chitchat_icon)
                            .setContentTitle(notification_title)
                            .setAutoCancel(true)
                            .setContentText(notification_message);


            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("user_id", user_id);
            resultIntent.putExtra("key",key);



            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);


            int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(CHANNEL_ID);
        }

        mNotificationManager.notify(mNotificationId,mBuilder.build());
    }
}


