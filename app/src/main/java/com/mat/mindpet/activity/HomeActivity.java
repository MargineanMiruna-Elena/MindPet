package com.mat.mindpet.activity;

import android.animation.ValueAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseError;
import com.mat.mindpet.R;
import com.mat.mindpet.domain.AppUsage;
import com.mat.mindpet.model.Pet;
import com.mat.mindpet.model.Progress;
import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.model.enums.Mood;
import com.mat.mindpet.model.enums.PetType;
import com.mat.mindpet.repository.UserRepository;
import com.mat.mindpet.service.AuthService;
import com.mat.mindpet.service.PetService;
import com.mat.mindpet.service.ProgressService;
import com.mat.mindpet.service.ScreentimeService;
import com.mat.mindpet.utils.MidnightFirebaseWorker;
import com.mat.mindpet.utils.NavigationHelper;
import com.mat.mindpet.utils.UsageStatsHelper;

import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    @Inject
    PetService petService;

    @Inject
    ProgressService progressService;
    @Inject
    ScreentimeService screentimeService;
    @Inject
    AuthService authService;

    private ImageView imageAnimal;
    private TextView petName, petMood, petMoodDescription, percentText;
    private ProgressBar progressLevel;
    private AnimationDrawable petAnim;
    private MaterialButton playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        NavigationHelper.setupNavigationBar(this);

        saveProgress();
    }

    private void saveProgress() {
        screentimeService.getUserLimits(
                screentimes -> {
                    int exceeded = 0;
                    int total = screentimes.size();
                    Map<String, Integer> usageNow = UsageStatsHelper.getUsage(this);

                    for (Screentime s : screentimes) {

                        int usedToday = usageNow.getOrDefault(s.getAppName(), 0);

                        screentimeService.updateUsedMinutes(
                                s.getScreentimeId(),
                                usedToday
                        );

                        if (usedToday > s.getGoalMinutes()) {
                            exceeded++;
                        }

                        AppUsage appUsage = new AppUsage(
                                s.getScreentimeId(),
                                s.getAppName(),
                                usedToday,
                                s.getGoalMinutes()
                        );

                    }

                    int percentMet = total == 0 ? 0 : ((total - exceeded) * 100) / total;
                    screentimeService.updateScreenGoalsMetToday(
                            authService.getCurrentUser().getUid(),
                            percentMet
                    );

                    loadUI();
                },
                error -> Toast.makeText(this, "Error loading limits: " + error, Toast.LENGTH_SHORT).show()
        );
    }

    private void loadUI() {
        imageAnimal = findViewById(R.id.imageAnimal);
        petName = findViewById(R.id.petName);
        petMood = findViewById(R.id.petMood);
        petMoodDescription = findViewById(R.id.petMoodDescription);
        progressLevel = findViewById(R.id.progressLevel);
        percentText = findViewById(R.id.textProgress);
        playButton = findViewById(R.id.playBtn);

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
        petName.setText(pet.getPetName());
        petMood.setText(pet.getMood().getDisplayName());
        petMoodDescription.setText(pet.getMood().getDescription());

        int animResId = getPetAnimationResource(pet.getPetType(), pet.getMood());

        imageAnimal.setBackgroundResource(animResId);
        imageAnimal.post(() -> {
            petAnim = (AnimationDrawable) imageAnimal.getBackground();
            petAnim.setOneShot(false);
        });

        ValueAnimator breatheAnimator = ValueAnimator.ofFloat(1f, 1.03f);
        breatheAnimator.setDuration(2200);
        breatheAnimator.setRepeatCount(ValueAnimator.INFINITE);
        breatheAnimator.setRepeatMode(ValueAnimator.REVERSE);
        breatheAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            imageAnimal.setScaleX(value);
            imageAnimal.setScaleY(value);
        });
        breatheAnimator.start();

        playButton.setOnClickListener(v -> {
            if (petAnim != null) {
                if (petAnim.isRunning()) {
                    petAnim.stop();
                    playButton.setIconResource(R.drawable.ic_play);
                } else {
                    petAnim.start();
                    playButton.setIconResource(R.drawable.ic_pause);
                }
            }
        });

        progressService.getTodayProgress(new ProgressService.ProgressCallback() {
            @Override
            public void onSuccess(Progress progress) {
                if (progress != null) {
                    int percent = progress.getScreenGoalsMet();
                    progressLevel.setProgress(percent);
                    percentText.setText(percent + "%");
                } else {
                    progressLevel.setProgress(0);
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error loading progress: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private int getPetAnimationResource(PetType type, Mood mood) {
        switch (type) {
            case HUSKEY:
                switch (mood) {
                    case NEUTRAL:
                        return R.drawable.huskey_idle_diadown_neutral_anim;
                    case HAPPY:
                        return R.drawable.huskey_idle_happy_anim;
                    case SAD:
                        return R.drawable.huskey_idle_sad_anim;
                }
            case BROWN_DOG:
                switch (mood) {
                    case NEUTRAL:
                        return R.drawable.brown_idle_diadown_neutral_anim;
                    case HAPPY:
                        return R.drawable.brown_idle_happy_anim;
                    case SAD:
                        return R.drawable.brown_idle_sad_anim;
                }
            case MIX_DOG:
                switch (mood) {
                    case NEUTRAL:
                        return R.drawable.mix_idle_diadown_neutral_anim;
                    case HAPPY:
                        return R.drawable.mix_idle_happy_anim;
                    case SAD:
                        return R.drawable.mix_idle_sad_anim;
                }
            case GRAY_CAT:
                switch (mood) {
                    case NEUTRAL:
                        return R.drawable.grey_cat_sitting_down_neutral_anim;
                    case HAPPY:
                        return R.drawable.grey_cat_idle_happy_anim;
                    case SAD:
                        return R.drawable.grey_cat_idle_sad_anim;
                }
            case BROWN_CAT:
                switch (mood) {
                    case NEUTRAL:
                        return R.drawable.brown_with_stripes_sitting_down_neutral_anim;
                    case HAPPY:
                        return R.drawable.brown_with_stripes_idle_happy_anim;
                    case SAD:
                        return R.drawable.brown_with_stripes_idle_sad_anim;
                }
            case ORANGE_CAT:
                switch (mood) {
                    case NEUTRAL:
                        return R.drawable.orange_sitting_down_neutral_anim;
                    case HAPPY:
                        return R.drawable.orange_idle_happy_anim;
                    case SAD:
                        return R.drawable.orange_idle_sad_anim;
                }
            default:
                return R.drawable.huskey_idle_diadown_neutral_anim;
        }
    }
}

