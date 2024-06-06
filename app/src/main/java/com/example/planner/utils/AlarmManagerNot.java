package com.example.planner.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.example.planner.models.Task;
import java.util.Calendar;

public class AlarmManagerNot {
    public static void createOrUpdateNotification(Context context, Task task) {
        if (task.getTaskTime() == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, task.getTaskDate().getYear());
        calendar.set(Calendar.MONTH, task.getTaskDate().getMonthValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, task.getTaskDate().getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, task.getTaskTime().getHour());
        calendar.set(Calendar.MINUTE, task.getTaskTime().getMinute());
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra("title", task.getTitle());
        intent.putExtra("id", task.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void deleteNotification(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}
