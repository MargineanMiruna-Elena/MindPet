package com.mat.mindpet.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mat.mindpet.R;
import com.mat.mindpet.model.Pet;
import com.mat.mindpet.model.enums.Mood;
import com.mat.mindpet.model.enums.PetType;
import com.mat.mindpet.service.PetService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChoosePetActivity extends AppCompatActivity {

    private ImageView imagePet, arrowLeft, arrowRight;
    private EditText editTextPetName;
    private TextView textPetType;
    private Button btnAdopt;

    private String[] petTypes = { "Husky", "Brown Dog", "Mix Dog", "Grey Cat", "Brown Cat", "Orange Cat" };
    private int[] petImages = { R.drawable.husky_front,R.drawable.brown_front,R.drawable.mix_front ,R.drawable.grey_cat_sitting_down,R.drawable.brown_with_stripes_sitting_down,R.drawable.orange_sitting_down };
    private int currentPetIndex = 0;
    private GestureDetector gestureDetector;

    @Inject
    PetService petService;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pet);

        imagePet = findViewById(R.id.imagePet);
        textPetType = findViewById(R.id.textPetType);
        editTextPetName = findViewById(R.id.editTextPetName);
        btnAdopt = findViewById(R.id.btnAdopt);
        arrowLeft = findViewById(R.id.arrowLeft);
        arrowRight = findViewById(R.id.arrowRight);

        updatePetDisplay(false);

        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        imagePet.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        btnAdopt.setOnClickListener(v -> {
            String petType = petTypes[currentPetIndex];
            String petName = editTextPetName.getText().toString().trim();

            if (petName.isEmpty()) {
                Toast.makeText(this, "Please enter a name for your pet", Toast.LENGTH_SHORT).show();
                return;
            }

            Pet pet = new Pet(
                    petName,
                    PetType.getPetTypeFromString(petType),
                    0,
                    Mood.NEUTRAL
            );
            petService.adoptPet(pet);
            Toast.makeText(this, "You adopted a new pet." , Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ChoosePetActivity.this, HomeActivity.class));
            finish();
        });


        arrowLeft.setOnClickListener(v -> showPreviousPet());
        arrowRight.setOnClickListener(v -> showNextPet());
    }

    private void updatePetDisplay(boolean fromLeft) {
        float startTranslation = fromLeft ? -imagePet.getWidth() : imagePet.getWidth();

        imagePet.animate()
                .translationX(startTranslation)
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    imagePet.setImageResource(petImages[currentPetIndex]);
                    textPetType.setText(petTypes[currentPetIndex]);

                    imagePet.setTranslationX(-startTranslation);
                    imagePet.animate()
                            .translationX(0)
                            .alpha(1f)
                            .setDuration(250)
                            .start();
                })
                .start();
    }

    private void showNextPet() {
        currentPetIndex = (currentPetIndex + 1) % petTypes.length;
        updatePetDisplay(false);
    }

    private void showPreviousPet() {
        currentPetIndex = (currentPetIndex - 1 + petTypes.length) % petTypes.length;
        updatePetDisplay(true);
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(velocityY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) showPreviousPet();
                    else showNextPet();
                    return true;
                }
            }
            return false;
        }
    }
}