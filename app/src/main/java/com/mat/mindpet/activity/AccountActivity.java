package com.mat.mindpet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.mat.mindpet.R;
import com.mat.mindpet.model.User;
import com.mat.mindpet.repository.UserRepository;
import com.mat.mindpet.service.AuthService;
import com.mat.mindpet.utils.NavigationHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountActivity extends AppCompatActivity {

    @Inject
    AuthService authService;

    private Button logoutButton;
    private TextView userNameText;
    private TextView userEmailText;

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        NavigationHelper.setupNavigationBar(this);

        logoutButton = findViewById(R.id.btnLogout);
        userNameText = findViewById(R.id.userName);
        userEmailText = findViewById(R.id.userEmail);

        userRepository = new UserRepository(FirebaseDatabase.getInstance().getReference());


        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userEmailText.setText(currentUser.getEmail());


        } else {
            userNameText.setText("User");

        }

        logoutButton.setOnClickListener(v -> {
            authService.logout();
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            finish();
        });
    }
}
