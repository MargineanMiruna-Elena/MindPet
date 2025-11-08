package com.mat.mindpet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mat.mindpet.R;
import com.mat.mindpet.service.AuthService;
import com.mat.mindpet.utils.NavigationHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountActivity extends AppCompatActivity {

    @Inject
    AuthService authService;

    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        NavigationHelper.setupNavigationBar(this);

        logoutButton = findViewById(R.id.btnLogout);

        logoutButton.setOnClickListener(v -> {
            authService.logout();
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            finish();
        });
    }
}

