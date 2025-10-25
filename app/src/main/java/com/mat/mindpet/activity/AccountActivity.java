package com.mat.mindpet.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mat.mindpet.R;
import com.mat.mindpet.utils.NavigationHelper;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

       NavigationHelper.setupNavigationBar(this);
    }
}

