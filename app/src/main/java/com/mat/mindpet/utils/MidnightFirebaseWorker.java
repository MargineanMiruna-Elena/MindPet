package com.mat.mindpet.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.service.ProgressService;
import com.mat.mindpet.service.ScreentimeService;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class MidnightFirebaseWorker extends Worker {

    private final ProgressService progressService;
    private final ScreentimeService screentimeService;

    @AssistedInject
    public MidnightFirebaseWorker(@Assisted @NonNull Context context,
                                  @Assisted @NonNull WorkerParameters workerParams,
                                  ProgressService progressService,
                                  ScreentimeService screentimeService) {
        super(context, workerParams);
        this.progressService = progressService;
        this.screentimeService = screentimeService;
    }

    @NonNull
    @Override
    public Result doWork() {
        final CountDownLatch latch = new CountDownLatch(2);
        final AtomicBoolean isProgressSuccess = new AtomicBoolean(false);
        final AtomicBoolean isScreentimeSuccess = new AtomicBoolean(false);

        if (progressService == null || screentimeService == null) {
            return Result.failure();
        }

        progressService.saveProgress(
                () -> {
                    isProgressSuccess.set(true);
                    latch.countDown();
                },
                (errorMessage) -> {
                    isProgressSuccess.set(false);
                    latch.countDown();
                }
        );

        screentimeService.getUserLimits(
                screentimes -> {
                    for (Screentime s : screentimes) {
                        screentimeService.updateNotificationSent(s.getScreentimeId(), false);
                    }
                    isScreentimeSuccess.set(true);
                    latch.countDown();
                },
                error -> {
                    isScreentimeSuccess.set(false);
                    latch.countDown();
                }
        );

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return Result.retry();
        }

        if (isProgressSuccess.get() && isScreentimeSuccess.get()) {
            scheduleNextMidnight();
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    private void scheduleNextMidnight() {
        long delay = calculateDelayUntilNextMidnight();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(MidnightFirebaseWorker.class)
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork("MidnightSaveWork", ExistingWorkPolicy.REPLACE, request);
    }

    private long calculateDelayUntilNextMidnight() {
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
