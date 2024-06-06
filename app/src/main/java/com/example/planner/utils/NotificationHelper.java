package com.example.planner.utils;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.planner.R;

class NotificationHelper extends ContextWrapper {
    public static final String channelID = "1";
    public static final String channelName = "Задачи";
    public static final String channelDescription = "Здесб вы увидите напоминания о выполнении задач";
    private NotificationManager notificationManager;
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(channelDescription);
        getManager().createNotificationChannel(channel);
    }
    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
    public NotificationCompat.Builder getChannelNotification(String title) {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Не забудьте")
                .setContentText("Сейчас у вас запланировано: " + title)
                .setSmallIcon(R.drawable.alarm);
    }
}
