package com.mat.mindpet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mat.mindpet.R;
import com.mat.mindpet.model.Task;
import com.mat.mindpet.service.TaskService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> tasks;
    private final TaskService taskService;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public TaskAdapter(List<Task> tasks, TaskService taskService) {
        this.tasks = tasks;
        this.taskService = taskService;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        Date deadline = new Date(task.getDeadline());

        holder.textTaskTitle.setText(task.getTitle() != null ? task.getTitle() : "");

        holder.textTaskDate.setText(sdf.format(deadline));

        holder.checkTask.setOnCheckedChangeListener(null);
        holder.checkTask.setChecked(task.getIsCompleted());
        holder.checkTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setIsCompleted(isChecked);
            if (task.getTaskId() != null) {
                taskService.updateTaskField(task.getTaskId(), "isCompleted", isChecked);
                if (isChecked) {
                    long completedAt = System.currentTimeMillis();
                    task.setCompletedAt(completedAt);
                    taskService.updateTaskField(task.getTaskId(), "completedAt", completedAt);
                } else {
                    taskService.updateTaskField(task.getTaskId(), "completedAt", null);
                }
            }
        });

        int color;
        switch (task.getPriority()) {
            case "Urgent":
                color = holder.itemView.getResources().getColor(R.color.lilac_shadow);
                break;
            case "Important":
                color = holder.itemView.getResources().getColor(R.color.french_lilac);
                break;
            case "Normal":
                color = holder.itemView.getResources().getColor(R.color.wisteria);
                break;
            case "Nice-to-have":
                color = holder.itemView.getResources().getColor(R.color.icy_lilac);
                break;
            default:
                color = holder.itemView.getResources().getColor(R.color.gray);
                break;
        }
        holder.importanceIndicator.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textTaskTitle, textTaskDate;
        CheckBox checkTask;
        View importanceIndicator;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTaskTitle = itemView.findViewById(R.id.textTaskTitle);
            textTaskDate = itemView.findViewById(R.id.textTaskDate);
            checkTask = itemView.findViewById(R.id.checkTask);
            importanceIndicator = itemView.findViewById(R.id.importanceIndicator);
        }
    }
}
