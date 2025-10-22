package com.mat.mindpet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mat.mindpet.R;
import com.mat.mindpet.domain.AppUsage;

import java.util.List;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.ViewHolder> {

    private List<AppUsage> appUsageList;

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
        int h = app.getMinutesUsed()/60;
        int m = app.getMinutesUsed() - h*60;
        int gh = app.getGoalMinutes()/60;
        int gm = app.getGoalMinutes() - gh*60;
        holder.tvAppDetails.setText("Used " + h + "h " + m + "min today • Limit: " + gh + "h " + gm + "min");
    }

    @Override
    public int getItemCount() {
        return appUsageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAppIcon;
        TextView tvAppName, tvAppDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvAppDetails = itemView.findViewById(R.id.tvAppDetails);
        }
    }
}

