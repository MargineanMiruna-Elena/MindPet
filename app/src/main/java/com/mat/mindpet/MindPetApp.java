package com.mat.mindpet;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.mat.mindpet.utils.MidnightFirebaseWorker;
import com.mat.mindpet.utils.ScreentimeSyncWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MindPetApp extends Application implements Configuration.Provider {

    @Inject
    HiltWorkerFactory workerFactory;

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        scheduleMidnightWorker();
        scheduleScreentimeSync();
    }

    private void scheduleMidnightWorker() {
        long delay = calculateDelayUntilMidnight();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(MidnightFirebaseWorker.class)
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .addTag("midnight_save")
                        .build();

        WorkManager.getInstance(this).enqueueUniqueWork(
                "MidnightSaveWork",
                ExistingWorkPolicy.REPLACE,
                request
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
            dueTime.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dueTime.getTimeInMillis() - currentTime.getTimeInMillis();
    }

    private void scheduleScreentimeSync() {

        PeriodicWorkRequest screentimeRequest =
                new PeriodicWorkRequest.Builder(
                        ScreentimeSyncWorker.class,
                        15,
                        TimeUnit.MINUTES
                )
                        .addTag("screentime_sync")
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "ScreentimeSyncWork",
                ExistingPeriodicWorkPolicy.KEEP,
                screentimeRequest
        );
    }
}
