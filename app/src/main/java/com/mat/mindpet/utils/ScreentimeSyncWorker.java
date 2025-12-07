package com.mat.mindpet.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkerParameters;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.service.AuthService;
import com.mat.mindpet.service.ScreentimeService;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class ScreentimeSyncWorker extends Worker {

    private final ScreentimeService screentimeService;
    private final AuthService authService;

    @AssistedInject
    public ScreentimeSyncWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters params,
            ScreentimeService screentimeService,
            AuthService authService
    ) {
        super(context, params);
        this.screentimeService = screentimeService;
        this.authService = authService;
    }

    @NonNull
    @Override
    public Result doWork() {

        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean isSuccess = new AtomicBoolean(false);

        screentimeService.getUserLimits(
                screentimes -> {
                    int exceeded = 0;
                    int total = screentimes.size();
                    Map<String, Integer> usageNow = UsageStatsHelper.getUsage(getApplicationContext());

                    for (Screentime s : screentimes) {

                        int usedToday = usageNow.getOrDefault(s.getAppName(), 0);

                        screentimeService.updateUsedMinutes(
                                s.getScreentimeId(),
                                usedToday
                        );

                        if (usedToday > s.getGoalMinutes()) {
                            exceeded++;
                        }
                    }

                    int percentMet = total == 0 ? 0 : ((total - exceeded) * 100) / total;

                    screentimeService.updateScreenGoalsMetToday(
                            authService.getCurrentUser().getUid(),
                            percentMet
                    );

                    isSuccess.set(true);
                    latch.countDown();
                },
                error -> {
                    isSuccess.set(false);
                    latch.countDown();
                }
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            return Result.retry();
        }

        if (isSuccess.get()) {
            scheduleScreentimeSync();
            return Result.success();
        } else {
            return Result.retry();
        }
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

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
                "ScreentimeSyncWork",
                ExistingPeriodicWorkPolicy.KEEP,
                screentimeRequest
        );
    }
}
