package com.example.planner.utils.notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.planner.MainActivity;
import com.example.planner.R;

class TasksNot extends ContextWrapper {
    public static final String channelID = "1";
    public static final String channelName = "Задачи";
    public static final String channelDescription = "Здесь вы увидите напоминания о выполнении задач";
    private NotificationManager notificationManager;
    public TasksNot(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName,
                NotificationManager.IMPORTANCE_HIGH);
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
        Context context = getApplicationContext();
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        return new NotificationCompat.Builder(context, channelID)
                .setContentTitle("Не забудьте")
                .setContentText("Сейчас у вас запланировано: " + title)
                .setSmallIcon(R.drawable.task_list)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
    }
}
