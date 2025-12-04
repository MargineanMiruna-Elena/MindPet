package com.mat.mindpet.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mat.mindpet.service.ScreentimeService;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class UsageUpdateWorker extends Worker {

    private final ScreentimeService screentimeService;

    @AssistedInject
    public UsageUpdateWorker(@Assisted @NonNull Context context,
                             @Assisted @NonNull WorkerParameters workerParams,
                             ScreentimeService screentimeService) {
        super(context, workerParams);
        this.screentimeService = screentimeService;
    }

    @NonNull
    @Override
    public Result doWork() {
        screentimeService.updateAllLimitsInBackground(getApplicationContext());

        return Result.success();
    }
}
