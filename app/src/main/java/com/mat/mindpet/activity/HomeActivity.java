package com.mat.mindpet.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mat.mindpet.R;
import com.mat.mindpet.utils.NavigationHelper;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        NavigationHelper.setupNavigationBar(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://mindpet-81364-default-rtdb.europe-west1.firebasedatabase.app"
        );

        DatabaseReference ref = database.getReference("test_message");

        ref.setValue("Hello from MindPet 🐾").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseTest", "Data written successfully!");
            } else {
                Log.e("FirebaseTest", "Failed to write data", task.getException());
            }
        });
    }
}

