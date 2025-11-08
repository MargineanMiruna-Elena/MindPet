package com.mat.mindpet.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mat.mindpet.R;


public class ChoosePetActivity extends AppCompatActivity {

    private ImageView imagePet, arrowLeft, arrowRight;
    private TextView textPetName;
    private Button btnAdopt;

    private String[] petNames = { "Husky","Brown Dog","Mix Dog" ,"Grey Cat","Brown  Cat", "Orange Cat" };
    private int[] petImages = { R.drawable.husky_front,R.drawable.brown_front,R.drawable.mix_front ,R.drawable.grey_cat_sitting_down,R.drawable.brown_with_stripes_sitting_down,R.drawable.orange_sitting_down };
    private int currentPetIndex = 0;

    private GestureDetector gestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pet);




        imagePet = findViewById(R.id.imagePet);
        textPetName = findViewById(R.id.textPetName);
        btnAdopt = findViewById(R.id.btnAdopt);
        arrowLeft = findViewById(R.id.arrowLeft);
        arrowRight = findViewById(R.id.arrowRight);

        updatePetDisplay(false);


        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        imagePet.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));


        btnAdopt.setOnClickListener(v -> {
            String pet = petNames[currentPetIndex];
            Toast.makeText(this, "You adopted " + pet , Toast.LENGTH_SHORT).show();
            // TODO: save in DB
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
                    textPetName.setText(petNames[currentPetIndex]);

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
        currentPetIndex = (currentPetIndex + 1) % petNames.length;
        updatePetDisplay(false);
    }

    private void showPreviousPet() {
        currentPetIndex = (currentPetIndex - 1 + petNames.length) % petNames.length;
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

