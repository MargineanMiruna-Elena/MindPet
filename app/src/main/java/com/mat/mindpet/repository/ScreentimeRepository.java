package com.mat.mindpet.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mat.mindpet.model.Screentime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void saveScreentime(Screentime screentime) {
        String key = screentimeRef.push().getKey();
        screentime.setScreentimeId(key);

        Map<String, Object> map = new HashMap<>();
        map.put("screentimeId", screentime.getScreentimeId());
        map.put("userId", screentime.getUserId());
        map.put("appName", screentime.getAppName());
        map.put("date", screentime.getDate().toString());
        map.put("minutesUsed", screentime.getMinutesUsed());
        map.put("goalMinutes", screentime.getGoalMinutes());
        map.put("exceededGoalBy", screentime.getExceededGoalBy());

        screentimeRef.child(key).setValue(map);
    }

    public void getScreentimesByUser(String userId, ScreentimesCallback callback) {
        screentimeRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Screentime> list = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            Screentime s = mapToScreentime(map);
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

    public void checkIfLimitExists(String userId, String appName, ScreentimeCallback callback) {
        screentimeRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            Screentime s = mapToScreentime(map);
                            if (s != null && s.getAppName().equals(appName)) {
                                callback.onSuccess(s);
                                return;
                            }
                        }
                        callback.onSuccess(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onFailure(error);
                    }
                });
    }

    private Screentime mapToScreentime(Map<String, Object> map) {
        if (map == null) return null;
        String id = (String) map.get("screentimeId");
        String userId = (String) map.get("userId");
        String appName = (String) map.get("appName");
        String dateStr = (String) map.get("date");
        LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
        int minutesUsed = map.get("minutesUsed") != null ? ((Long) map.get("minutesUsed")).intValue() : 0;
        int goalMinutes = map.get("goalMinutes") != null ? ((Long) map.get("goalMinutes")).intValue() : 0;
        int exceededGoalBy = map.get("exceededGoalBy") != null ? ((Long) map.get("exceededGoalBy")).intValue() : 0;

        return new Screentime(id, userId, appName, date, minutesUsed, goalMinutes, exceededGoalBy);
    }
}
