package com.mat.mindpet.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mat.mindpet.model.Progress;
import com.mat.mindpet.model.enums.Mood;

import java.util.Calendar;
import java.util.List;

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
        String key = progressRef.push().getKey();
        progress.setProgressId(key);
        progressRef.child(key).setValue(progress);

        petRepository.updatePetField(progress.getUserId(), "mood", getMoodFromProgress(progress.getScreenGoalsMet()));
    }

    public void updateProgress(Progress progress) {
        progressRef.child(progress.getProgressId()).setValue(progress);
    }

    public void updateProgressField(String progressId, String userId, String fieldName, Object value) {
        progressRef.child(progressId).child(fieldName).setValue(value);

        if (fieldName.equals("screenGoalsMet")) {
            petRepository.updatePetField(userId, "mood", getMoodFromProgress((int) value));
        }
    }

    public void deleteProgress(String progressId) {
        progressRef.child(progressId).removeValue();
    }

    public void getProgressById(String progressId, ProgressCallback callback) {
        progressRef.child(progressId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Progress progress = dataSnapshot.getValue(Progress.class);
                callback.onSuccess(progress);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError);
            }
        });
    }

    public void getAllProgressByUser(String userId, ProgressListCallback callback) {
        progressRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Progress> progressList = new java.util.ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Progress progress = snapshot.getValue(Progress.class);
                    progressList.add(progress);
                }
                callback.onSuccess(progressList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError);
            }
        });
    }

    public void getProgressInRange(String userId, long startMillis, long endMillis, ProgressCallback callback) {

        progressRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Progress last = null;

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Progress p = child.getValue(Progress.class);
                            if (p == null || p.getDate() == 0) continue;

                            long date = p.getDate();
                            if (date >= startMillis && date <= endMillis) {
                                if (last == null || date > last.getDate()) {
                                    last = p;
                                }
                            }
                        }

                        callback.onSuccess(last);
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
