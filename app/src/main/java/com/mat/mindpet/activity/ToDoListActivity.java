package com.mat.mindpet.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.mat.mindpet.R;
import com.mat.mindpet.adapter.TaskAdapter;
import com.mat.mindpet.model.Task;
import com.mat.mindpet.service.AuthService;
import com.mat.mindpet.service.TaskService;
import com.mat.mindpet.utils.NavigationHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ToDoListActivity extends AppCompatActivity {

    @Inject
    TaskService taskService;

    @Inject
    AuthService authService;

    private RecyclerView recyclerTodo;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private ImageButton btnAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        // Setup navigation bar
        NavigationHelper.setupNavigationBar(this);

        recyclerTodo = findViewById(R.id.recyclerTodo);
        btnAddTask = findViewById(R.id.btnAddTask);

        recyclerTodo.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, taskService);
        recyclerTodo.setAdapter(taskAdapter);


        loadTasksForCurrentUser();

        btnAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void loadTasksForCurrentUser() {
        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        taskService.getTasksByUser(userId, new TaskService.TasksCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskList.clear();
                taskList.addAll(tasks);
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ToDoListActivity.this, "Error loading tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final android.view.View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        final EditText editTaskTitle = dialogView.findViewById(R.id.editTaskTitle);

        builder.setTitle("Add Task");
        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = editTaskTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Task title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = authService.getCurrentUser();
            if (currentUser == null) return;


            Task task = new Task(
                    null,
                    currentUser.getUid(),
                    title,
                    null,
                    false,
                    0,
                    LocalDate.now()
            );


            taskService.createTask(task, new TaskService.TaskCreatedCallback() {
                @Override
                public void onSuccess(Task createdTask) {
                    taskList.add(createdTask);
                    taskAdapter.notifyItemInserted(taskList.size() - 1);
                    recyclerTodo.scrollToPosition(taskList.size() - 1);
                    Toast.makeText(ToDoListActivity.this, "Task saved!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(ToDoListActivity.this, "Error saving task " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
