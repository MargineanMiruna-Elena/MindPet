package com.mat.mindpet.adapter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mat.mindpet.R;
import com.mat.mindpet.domain.AppUsage;

import java.util.List;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.ViewHolder> {

    private List<AppUsage> appUsageList;

    public interface OnEditClickListener {
        void onEdit(AppUsage appUsage);
    }

    public interface OnDeleteClickListener {
        void onDelete(AppUsage appUsage);
    }

    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public AppUsageAdapter(List<AppUsage> appUsageList) {
        this.appUsageList = appUsageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_usage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppUsage app = appUsageList.get(position);

        holder.tvAppName.setText(app.getAppName());

        try {
            PackageManager pm = holder.itemView.getContext().getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);

            for (ResolveInfo info : apps) {
                String label = info.loadLabel(pm).toString();
                if (label.equals(app.getAppName())) {
                    holder.ivAppIcon.setImageDrawable(info.loadIcon(pm));
                    break;
                }
            }
        } catch (Exception e) {
            holder.ivAppIcon.setImageResource(R.drawable.default_app_icon);
        }

        int usedH = app.getMinutesUsed() / 60;
        int usedM = app.getMinutesUsed() % 60;

        int limitH = app.getGoalMinutes() / 60;
        int limitM = app.getGoalMinutes() % 60;

        holder.tvAppDetails.setText(
                "Used " + usedH + "h " + usedM + "min • Limit: " + limitH + "h " + limitM + "min"
        );

        if (app.getMinutesUsed() < app.getGoalMinutes()) {
            holder.tvAppDetails.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvAppDetails.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEdit(app);
            }
        });
    }


    @Override
    public int getItemCount() {
        return appUsageList.size();
    }

    public void removeItem(AppUsage app) {
        int index = appUsageList.indexOf(app);
        if (index != -1) {
            appUsageList.remove(index);
            notifyItemRemoved(index);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAppName, tvAppDetails;
        ImageButton btnEdit;
        ImageView ivAppIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvAppDetails = itemView.findViewById(R.id.tvAppDetails);
            btnEdit = itemView.findViewById(R.id.btnEditLimit);
            ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
        }
    }
}
