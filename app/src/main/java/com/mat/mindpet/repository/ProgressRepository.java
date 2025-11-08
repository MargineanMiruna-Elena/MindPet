package com.mat.mindpet.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mat.mindpet.model.Progress;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProgressRepository {

    private final DatabaseReference progressRef;

    @Inject
    public ProgressRepository(DatabaseReference databaseReference) {
        this.progressRef = databaseReference.child("progress");
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
    }

    public void updateProgress(Progress progress) {
        progressRef.child(progress.getProgressId()).setValue(progress);
    }

    public void updateProgressField(String progressId, String fieldName, Object value) {
        progressRef.child(progressId).child(fieldName).setValue(value);
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
}
