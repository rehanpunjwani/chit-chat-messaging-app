package com.example.chitchat.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.chitchat.MainActivity;
import com.example.chitchat.R;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ChatMessaginService extends com.google.firebase.messaging.FirebaseMessagingService {

    private  String CHANNEL_ID = "message_notifications";
    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,"message_notifications", NotificationManager.IMPORTANCE_HIGH);


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_message = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();


        String user_id = remoteMessage.getData().get("user_id");
        String user_name = remoteMessage.getData().get("user_name");
        String GROUP_KEY_CHIT_CHAT = "com.android.example.Chit_Chat";


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.chitchat_icon)
                        .setContentTitle(notification_title)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setGroup(GROUP_KEY_CHIT_CHAT)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setContentText(notification_message);


        if (Build.VERSION.SDK_INT >= 21) mBuilder.setVibrate(new long[0]);
        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("user_id", user_id);
        resultIntent.putExtra("user_name",user_name);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        stackBuilder.addParentStack(MainActivity.class);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);


        int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel.setShowBadge(true);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(CHANNEL_ID);
        }

        mNotificationManager.notify(mNotificationId,mBuilder.build());
    }
}
