package com.example.planner.utils.Notifications;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.planner.controllers.TaskDBWorker;
import com.example.planner.models.Task;

import java.util.ArrayList;

public class BootService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resetAlarms(getApplicationContext());
        stopSelf();
        return START_NOT_STICKY;
    }

    private void resetAlarms(Context context) {
        ArrayList<Task> alarms = new ArrayList<>();
        TaskDBWorker.getAllTasks(context, alarms, false);
        for (Task task : alarms) {
            AlarmManagerNot.createOrUpdateNotification(context, task);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
