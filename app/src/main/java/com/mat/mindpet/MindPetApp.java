package com.mat.mindpet;

import android.app.Application;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.mat.mindpet.utils.MidnightFirebaseWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MindPetApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        scheduleMidnightWorker();
    }

    private void scheduleMidnightWorker() {
        long delay = calculateDelayUntilMidnight();

        PeriodicWorkRequest saveRequest = new PeriodicWorkRequest.Builder(
                MidnightFirebaseWorker.class,
                24,
                TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("midnight_save")
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "MidnightSaveWork",
                ExistingPeriodicWorkPolicy.KEEP,
                saveRequest
        );
    }

    private long calculateDelayUntilMidnight() {
        Calendar currentTime = Calendar.getInstance();
        Calendar dueTime = Calendar.getInstance();

        dueTime.set(Calendar.HOUR_OF_DAY, 0);
        dueTime.set(Calendar.MINUTE, 0);
        dueTime.set(Calendar.SECOND, 0);
        dueTime.set(Calendar.MILLISECOND, 0);

        if (dueTime.before(currentTime)) {
            dueTime.add(Calendar.HOUR_OF_DAY, 24);
        }

        return dueTime.getTimeInMillis() - currentTime.getTimeInMillis();
    }
}
