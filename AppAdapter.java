package com.kogo.pixlstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private List<AppItem> appList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AppItem app);
    }

    public AppAdapter(List<AppItem> appList, OnItemClickListener listener) {
        this.appList = appList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppItem app = appList.get(position);
        
        holder.appName.setText(app.getName());
        holder.appDescription.setText(app.getDescription());
        holder.appStatus.setText(app.getStatus());
        holder.appIcon.setImageResource(app.getIconResource());

        // ステータスに応じて色を変更
        if (app.getStatus().contains("インストール済み")) {
            holder.appStatus.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.appStatus.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_orange_dark));
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(app);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView appIcon;
        TextView appName;
        TextView appDescription;
        TextView appStatus;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            appDescription = itemView.findViewById(R.id.appDescription);
            appStatus = itemView.findViewById(R.id.appStatus);
        }
    }
}