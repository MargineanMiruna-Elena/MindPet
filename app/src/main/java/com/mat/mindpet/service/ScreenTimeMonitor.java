package com.mat.mindpet.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.hilt.work.HiltWorker;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mat.mindpet.R;
import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.repository.ScreentimeRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class ScreenTimeMonitor extends Worker {

    private final ScreentimeRepository screentimeRepository;

    @AssistedInject
    public ScreenTimeMonitor(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters params,
            ScreentimeRepository repository
    ) {
        super(context, params);
        this.screentimeRepository = repository;
    }

    public static void schedule(Context context) {
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(ScreenTimeMonitor.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "ScreenTimeMonitorWorker",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
        );
    }

    public static void cancel(Context context) {
        WorkManager.getInstance(context)
                .cancelUniqueWork("ScreenTimeMonitorWorker");
    }

    @NonNull
    @Override
    public Result doWork() {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return Result.success();

        boolean pushEnabled = getApplicationContext()
                .getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getBoolean("push_enabled", false);

        if (!pushEnabled) return Result.success();

        screentimeRepository.getScreentimesByUser(uid, new ScreentimeRepository.ScreentimesCallback() {
            @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
            @Override
            public void onSuccess(List<Screentime> list) {

                for (Screentime s : list) {
                    if (s.getMinutesUsed() > s.getGoalMinutes()) {
                        sendNotification(s.getAppName(), s.getMinutesUsed(), s.getGoalMinutes());
                    }
                }
            }

            @Override
            public void onFailure(com.google.firebase.database.DatabaseError error) {}
        });

        return Result.success();
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void sendNotification(String appName, long used, long limit) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "screen_time_alerts",
                    "Screen Time Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager =
                    getApplicationContext().getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "screen_time_alerts")
                        .setSmallIcon(R.drawable.ic_paw)
                        .setContentTitle(appName + " limit exceeded!")
                        .setContentText("Used " + used + " min / Limit " + limit + " min. Your pet is sad ")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(getApplicationContext())
                .notify(appName.hashCode(), builder.build());
    }
}
