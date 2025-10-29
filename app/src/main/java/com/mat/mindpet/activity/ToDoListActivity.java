package com.mat.mindpet.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mat.mindpet.R;
import com.mat.mindpet.utils.NavigationHelper;

public class ToDoListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        NavigationHelper.setupNavigationBar(this);
    }
}

