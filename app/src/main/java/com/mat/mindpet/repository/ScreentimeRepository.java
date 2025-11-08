package com.mat.mindpet.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mat.mindpet.model.Screentime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ScreentimeRepository {

    private final DatabaseReference screentimeRef;

    @Inject
    public ScreentimeRepository(DatabaseReference databaseReference) {
        this.screentimeRef = databaseReference.child("screentimes");
    }

    public interface ScreentimeCallback {
        void onSuccess(Screentime screentime);

        void onFailure(DatabaseError error);
    }

    public interface ScreentimesCallback {
        void onSuccess(List<Screentime> screentimes);

        void onFailure(DatabaseError error);
    }

    /**
     * Saves a new screentime entry to the database
     */
    public void saveScreentime(Screentime screentime) {
        String key = screentimeRef.push().getKey();
        screentime.setScreentimeId(key);
        screentimeRef.child(key).setValue(screentime);
    }

    /**
     * Updates an existing screentime entry in the database
     */
    public void updateScreentime(Screentime screentime) {
        screentimeRef.child(screentime.getScreentimeId()).setValue(screentime);
    }

    /**
     * Updates a specific field of a screentime entry
     */
    public void updateScreentimeField(String screentimeId, String fieldName, Object value) {
        screentimeRef.child(screentimeId).child(fieldName).setValue(value);
    }

    /**
     * Deletes a screentime entry from the database
     */
    public void deleteScreentime(String screentimeId) {
        screentimeRef.child(screentimeId).removeValue();
    }

    /**
     * Retrieves a screentime entry by its ID
     */
    public void getScreentimeById(String id, ScreentimeCallback callback) {
        screentimeRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Screentime screentime = snapshot.getValue(Screentime.class);
                callback.onSuccess(screentime);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Retrieves all screentime entries for a specific user
     */
    public void getScreentimesByUser(String userId, ScreentimesCallback callback) {
        screentimeRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Screentime> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Screentime s = child.getValue(Screentime.class);
                    list.add(s);
                }
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error);
            }
        });
    }
}
