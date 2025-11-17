package com.mat.mindpet.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mat.mindpet.R;
import com.mat.mindpet.model.Pet;
import com.mat.mindpet.repository.UserRepository;
import com.mat.mindpet.service.PetService;
import com.mat.mindpet.utils.NavigationHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    @Inject
    PetService petService;

    private ImageView imageAnimal;
    private TextView textAnimalName;
    private TextView textMood;
    private ProgressBar progressLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        NavigationHelper.setupNavigationBar(this);

        imageAnimal = findViewById(R.id.imageAnimal);
        textAnimalName = findViewById(R.id.textAnimalName);
        textMood = findViewById(R.id.textMood);
        progressLevel = findViewById(R.id.progressLevel);

        petService.getPetForCurrentUser(new UserRepository.PetCallback() {
            @Override
            public void onSuccess(Pet pet) {
                if (pet != null) {
                    updatePetUI(pet);
                } else {
                    Toast.makeText(HomeActivity.this, "No pet found. Adopt one!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error loading pet: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePetUI(Pet pet) {
        textAnimalName.setText(pet.getPetName());
        textMood.setText("Mood: " + pet.getMood().name());

        progressLevel.setProgress(pet.getLevel());

        int imageRes = getPetImage(pet.getPetType());
        imageAnimal.setImageResource(imageRes);
    }

    private int getPetImage(com.mat.mindpet.model.enums.PetType type) {
        switch (type) {
            case HUSKEY:
                return R.drawable.husky_front;
            case BROWN_DOG:
                return R.drawable.brown_front;
            case MIX_DOG:
                return R.drawable.mix_front;
            case GRAY_CAT:
                return R.drawable.grey_cat_sitting_down;
            case BROWN_CAT:
                return R.drawable.brown_with_stripes_sitting_down;
            case ORANGE_CAT:
                return R.drawable.orange_sitting_down;
            default:
                return R.drawable.husky_front;
        }
    }
}

