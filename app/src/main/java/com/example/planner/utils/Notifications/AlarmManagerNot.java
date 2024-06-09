package com.example.planner.utils.Notifications;

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
        calendar.set(Calendar.YEAR, task.getTaskDateBegin().getYear());
        calendar.set(Calendar.MONTH, task.getTaskDateBegin().getMonthValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, task.getTaskDateBegin().getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, task.getTaskTime().getHour());
        calendar.set(Calendar.MINUTE, task.getTaskTime().getMinute());
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.setAction("taskNot " + task.getId());
        intent.putExtra("title", task.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void deleteNotification(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.setAction("taskNot " + task.getId());
        intent.putExtra("title", task.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}
