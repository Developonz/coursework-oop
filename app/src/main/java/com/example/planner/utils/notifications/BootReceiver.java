package com.example.planner.utils.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.planner.controllers.tasks.TaskDBWorker;
import com.example.planner.models.Task;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent serviceIntent = new Intent(context, BootService.class);
            context.startForegroundService(serviceIntent);
        } else {
            resetAlarms(context);
        }
    }
    private void resetAlarms(Context context) {
        ArrayList<Task> alarms = new ArrayList<>();
        TaskDBWorker.getAllTasks(context, alarms, false);
        for (Task task : alarms) {
            ManagerAlarm.createOrUpdateNotification(context, task);
        }
    }
}
