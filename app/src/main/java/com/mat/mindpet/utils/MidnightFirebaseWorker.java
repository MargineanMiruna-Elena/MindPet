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
        Log.d("WorkerDebug", "Worker-ul a pornit!"); // <--- Log 1
        final CountDownLatch latch = new CountDownLatch(1);

        final AtomicBoolean isSuccess = new AtomicBoolean(false);

        if (progressService == null) {
            Log.e("WorkerDebug", "EROARE CRITICĂ: ProgressService este NULL! Hilt nu merge.");
            return Result.failure();
        }

        Log.d("WorkerDebug", "Încercăm salvarea...");

        progressService.saveProgress(
                () -> {
                    Log.d("WorkerDebug", "SUCCES: Datele au fost salvate!"); // <--- Log 2
                    isSuccess.set(true);
                    latch.countDown();
                },
                (errorMessage) -> {
                    Log.e("WorkerDebug", "EROARE FIREBASE: " + errorMessage); // <--- Log 3
                    isSuccess.set(false);
                    latch.countDown();
                }
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e("WorkerDebug", "Latch întrerupt");
            return Result.retry();
        }

        if (isSuccess.get()) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }
}
