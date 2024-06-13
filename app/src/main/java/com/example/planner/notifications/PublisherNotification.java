package com.example.planner.notifications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class PublisherNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TasksNot notificationHelper = new TasksNot(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(intent.getStringExtra("title"));
        notificationHelper.getManager().notify(1, nb.build());
        nb.setDefaults(Notification.DEFAULT_VIBRATE);
        nb.setDefaults(Notification.DEFAULT_SOUND);
        nb.build();
    }
}
