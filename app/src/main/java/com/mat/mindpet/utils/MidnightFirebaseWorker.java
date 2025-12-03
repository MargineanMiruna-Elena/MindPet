package com.mat.mindpet.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mat.mindpet.service.ProgressService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;


public class MidnightFirebaseWorker extends Worker {

    private final ProgressService progressService;

    @Inject
    public MidnightFirebaseWorker(@NonNull Context context, @NonNull WorkerParameters params, ProgressService progressService) {
        super(context, params);
        this.progressService = progressService;
    }

    @NonNull
    @Override
    public Result doWork() {
        final CountDownLatch latch = new CountDownLatch(1);

        final AtomicBoolean isSuccess = new AtomicBoolean(false);

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
