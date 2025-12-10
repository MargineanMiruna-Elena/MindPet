package com.mat.mindpet.repository;

import static com.mat.mindpet.utils.UsageStatsHelper.getStartOfDay;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mat.mindpet.model.Progress;
import com.mat.mindpet.model.enums.Mood;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProgressRepository {

    private final DatabaseReference progressRef;
    private final UserRepository petRepository;


    @Inject
    public ProgressRepository(DatabaseReference databaseReference, UserRepository petRepository) {
        this.progressRef = databaseReference.child("progress");
        this.petRepository = petRepository;
    }

    public interface ProgressCallback {
        void onSuccess(Progress progress);
        void onFailure(DatabaseError error);
    }

    public interface ProgressListCallback {
        void onSuccess(List<Progress> progressList);
        void onFailure(DatabaseError error);
    }

    public void saveProgress(Progress progress) {
        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("dailyScore", progress.getDailyScore());
        dataToSave.put("screenGoalsMet", progress.getScreenGoalsMet());
        dataToSave.put("streak", progress.getStreak());
        dataToSave.put("tasksCompleted", progress.getTasksCompleted());

        long startOfDay = getStartOfDay(0);
        dataToSave.put("date", startOfDay);

        progressRef.child(progress.getUserId()).child(String.valueOf(startOfDay)).setValue(dataToSave).addOnCompleteListener(e -> {
            petRepository.updatePetField(progress.getUserId(), "mood", getMoodFromProgress(progress.getScreenGoalsMet()));
        });
    }

    public void updateProgressField(String userId, String fieldName, Object value) {
        long startOfDay = getStartOfDay(0);

        progressRef.child(userId).child(String.valueOf(startOfDay)).child(fieldName).setValue(value);

        if (fieldName.equals("screenGoalsMet")) {
            if (value.equals(100)) {
                progressRef.child(userId).child(String.valueOf(startOfDay)).child("streak").setValue(true);
            } else {
                progressRef.child(userId).child(String.valueOf(startOfDay)).child("streak").setValue(false);
            }
            petRepository.updatePetField(userId, "mood", getMoodFromProgress((int) value));
        }
    }

    public void getAllProgressByUser(String userId, ProgressListCallback callback) {
        progressRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Progress> progressList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Progress progress = snapshot.getValue(Progress.class);

                    if (progress != null) {
                        progress.setUserId(userId);
                        progressList.add(progress);
                    }
                }
                callback.onSuccess(progressList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure(databaseError);
            }
        });
    }

    public void getProgressInRange(String userId, long startMillis, long endMillis, ProgressCallback callback) {
        progressRef.child(userId)
                .orderByKey()
                .startAt(String.valueOf(startMillis))
                .endAt(String.valueOf(endMillis))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Progress lastProgress = null;
                        long maxDate = -1;

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Progress p = child.getValue(Progress.class);
                            if (p != null) {
                                long dateKey = Long.parseLong(child.getKey());
                                p.setDate(dateKey);
                                p.setUserId(userId);

                                if (dateKey > maxDate) {
                                    maxDate = dateKey;
                                    lastProgress = p;
                                }
                            }
                        }

                        callback.onSuccess(lastProgress);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error);
                    }
                });
    }

    private Mood getMoodFromProgress(int progress) {
        if (progress >= 75) {
            return Mood.HAPPY;
        } else if (progress >= 50) {
            return Mood.NEUTRAL;
        } else {
            return Mood.SAD;
        }
    }

}
