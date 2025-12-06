package com.mat.mindpet.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mat.mindpet.service.ProgressService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class MidnightFirebaseWorker extends Worker {

    private final ProgressService progressService;

    @AssistedInject
    public MidnightFirebaseWorker(@Assisted @NonNull Context context,
                                  @Assisted @NonNull WorkerParameters workerParams,
                                  ProgressService progressService) {
        super(context, workerParams);
        this.progressService = progressService;
    }

    @NonNull
    @Override
    public Result doWork() {
        final CountDownLatch latch = new CountDownLatch(1);

        final AtomicBoolean isSuccess = new AtomicBoolean(false);

        if (progressService == null) {
            return Result.failure();
        }

        progressService.saveProgress(
                () -> {
                    isSuccess.set(true);
                    latch.countDown();
                },
                (errorMessage) -> {
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
            return Result.success();
        } else {
            return Result.retry();
        }
    }
}
