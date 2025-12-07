package com.mat.mindpet.repository;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mat.mindpet.model.Pet;
import com.mat.mindpet.model.Progress;
import com.mat.mindpet.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository {

    private final DatabaseReference usersRef;

    @Inject
    public UserRepository(DatabaseReference databaseReference) {
        this.usersRef = databaseReference.child("users");
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(DatabaseError error);
    }

    public interface UsersCallback {
        void onSuccess(List<User> users);
        void onFailure(DatabaseError error);
    }

    public interface PetCallback {
        void onSuccess(Pet pet);
        void onFailure(DatabaseError error);
    }

    /**
     * Creates a new user in the database
     */
    public void createUser(User user) {
        usersRef.child(user.getUserId()).setValue(user);
    }

    /**
     * Updates an existing user in the database
     */
    public void updateUser(User user) {
        usersRef.child(user.getUserId()).setValue(user);
    }

    /**
     * Updates a specific field of a user
     */
    public void updateUserField(String userId, String fieldName, Object value) {
        usersRef.child(userId).child(fieldName).setValue(value);
    }

    /**
     * Deletes a user from the database
     */
    public void deleteUser(String userId) {
        usersRef.child(userId).removeValue();
    }

    /**
     * Creates or updates a pet for a user
     */
    public void createOrUpdatePetForUser(String userId, Pet pet) {
        usersRef.child(userId).child("pet").setValue(pet);
    }

    /**
     * Updates a specific field of a user's pet
     */
    public void updatePetField(String userId, String fieldName, Object value) {
        usersRef.child(userId).child("pet").child(fieldName).setValue(value);
        Log.d("UserRepository", "Updated pet field: " + fieldName + " with value: " + value);
    }

    /**
     * Deletes the pet associated with a user
     */
    public void deletePet(String userId) {
        usersRef.child(userId).child("pet").setValue(null);
    }

    /**
     * Retrieves a user by their ID
     */
    public void getUserById(String userId, UserCallback callback) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                callback.onSuccess(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Retrieves all users from the database
     */
    public void getAllUsers(UsersCallback callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new java.util.ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Retrieves the pet associated with a user
     */
    public void getPetByUserId(String userId, PetCallback callback) {
        usersRef.child(userId).child("pet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Pet pet = snapshot.getValue(Pet.class);
                callback.onSuccess(pet);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error);
            }
        });
    }
}
