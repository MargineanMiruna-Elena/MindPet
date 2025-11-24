package com.mat.mindpet.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.mat.mindpet.R;
import com.mat.mindpet.adapter.TaskAdapter;
import com.mat.mindpet.model.Task;
import com.mat.mindpet.repository.TaskRepository;
import com.mat.mindpet.service.AuthService;
import com.mat.mindpet.service.TaskService;
import com.mat.mindpet.utils.NavigationHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ToDoListActivity extends AppCompatActivity {

    @Inject TaskService taskService;
    @Inject AuthService authService;

    private RecyclerView recyclerTasks;
    private ImageButton btnAddTask;
    private MaterialButtonToggleGroup navbarFilter;
    private MaterialButton btnPast, btnUpcoming, btnOverdue;

    private final List<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        NavigationHelper.setupNavigationBar(this);

        recyclerTasks = findViewById(R.id.recyclerTasks);
        btnAddTask = findViewById(R.id.btnAddTask);
        navbarFilter = findViewById(R.id.navbarFilter);
        btnPast = findViewById(R.id.btnPast);
        btnUpcoming = findViewById(R.id.btnUpcoming);
        btnOverdue = findViewById(R.id.btnOverdue);

        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));

        loadTasksForCurrentUser();

        btnAddTask.setOnClickListener(v -> showAddTaskDialog());

        navbarFilter.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == btnPast.getId()) {
                    showSection("Past");
                } else if (checkedId == btnUpcoming.getId()) {
                    showSection("Upcoming");
                } else if (checkedId == btnOverdue.getId()) {
                    showSection("Overdue");
                }
            }
        });

        btnUpcoming.setChecked(true);
        showSection("Upcoming");
    }

    private void loadTasksForCurrentUser() {
        if (authService.getCurrentUser() == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        taskService.getTasksByUser(new TaskRepository.TasksCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                runOnUiThread(() -> {
                    taskList.clear();
                    if (tasks != null) taskList.addAll(tasks);
                    if (btnPast.isChecked()) showSection("Past");
                    else if (btnUpcoming.isChecked()) showSection("Upcoming");
                    else if (btnOverdue.isChecked()) showSection("Overdue");
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(ToDoListActivity.this, "Error loading tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void showSection(String section) {
        recyclerTasks.setVisibility(View.GONE);

        switch (section) {
            case "Past":
                recyclerTasks.setVisibility(View.VISIBLE);
                recyclerTasks.setAdapter(new TaskAdapter(getPastTasks(), taskService));
                break;
            case "Upcoming":
                recyclerTasks.setVisibility(View.VISIBLE);
                recyclerTasks.setAdapter(new TaskAdapter(getUpcomingTasks(), taskService));
                break;
            case "Overdue":
                recyclerTasks.setVisibility(View.VISIBLE);
                recyclerTasks.setAdapter(new TaskAdapter(getOverdueTasks(), taskService));
                break;
        }
    }

    private List<Task> getPastTasks() {
        List<Task> list = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Task t : taskList) {
            if (t.getDeadline() < now && t.getIsCompleted()) list.add(t);
        }
        list.sort(Comparator.comparingLong(Task::getDeadline));
        return list;
    }

    private List<Task> getUpcomingTasks() {
        List<Task> list = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Task t : taskList) {
            if (t.getDeadline() >= now) list.add(t);
        }
        list.sort(Comparator.comparingLong(Task::getDeadline));
        return list;
    }

    private List<Task> getOverdueTasks() {
        List<Task> list = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Task t : taskList) {
            if (t.getDeadline() < now && !t.getIsCompleted()) list.add(t);
        }
        list.sort(Comparator.comparingLong(Task::getDeadline));
        return list;
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        EditText editTaskTitle = dialogView.findViewById(R.id.editTaskTitle);
        EditText editTaskDeadline = dialogView.findViewById(R.id.editTaskDeadline);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);

        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.task_priority_options,
                android.R.layout.simple_spinner_item
        );
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        editTaskDeadline.setFocusable(false);
        editTaskDeadline.setClickable(true);
        editTaskDeadline.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ToDoListActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        editTaskDeadline.setText(formattedDate);
                    },
                    year, month, day
            );

            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        builder.setTitle("Add Task");
        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = editTaskTitle.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();
            String deadlineText = editTaskDeadline.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Task title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            long deadlineMillis = System.currentTimeMillis();
            if (!deadlineText.isEmpty()) {
                String[] parts = deadlineText.split("/");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int year = Integer.parseInt(parts[2]);
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day, 0, 0, 0);
                deadlineMillis = cal.getTimeInMillis();
            }

            if (authService.getCurrentUser() == null) return;

            Task task = new Task(
                    null,
                    authService.getCurrentUser().getUid(),
                    title,
                    deadlineMillis,
                    false,
                    priority,
                    System.currentTimeMillis()
            );

            taskService.createTask(task);
            loadTasksForCurrentUser();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
