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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> tasks;
    private final TaskService taskService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

        holder.textTaskTitle.setText(task.getTitle());


        if (task.getCreatedAt() != null) {
            holder.textTaskDate.setText(task.getCreatedAt().format(formatter));
        } else {
            holder.textTaskDate.setText("");
        }


        holder.checkTask.setOnCheckedChangeListener(null);
        holder.checkTask.setChecked(task.isCompleted());


        holder.checkTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);


            taskService.updateTaskField(task.getTaskId(), "isCompleted", isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textTaskTitle, textTaskDate;
        CheckBox checkTask;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTaskTitle = itemView.findViewById(R.id.textTaskTitle);
            textTaskDate = itemView.findViewById(R.id.textTaskDate);
            checkTask = itemView.findViewById(R.id.checkTask);
        }
    }
}
