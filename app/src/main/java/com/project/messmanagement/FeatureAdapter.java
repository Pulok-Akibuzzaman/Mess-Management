package com.project.messmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {

    private List<FeatureItem> featureList;
    private OnFeatureClickListener listener;

    public interface OnFeatureClickListener {
        void onFeatureClick(String featureName);
    }

    public FeatureAdapter(List<FeatureItem> featureList, OnFeatureClickListener listener) {
        this.featureList = featureList;
        this.listener = listener;
    }

    public FeatureAdapter(List<FeatureItem> featureList) {
        this.featureList = featureList;
        this.listener = null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (featureList == null || featureList.isEmpty()) return;
        FeatureItem item = featureList.get(position);
        holder.tvLabel.setText(item.getLabel());
        holder.ivIcon.setImageResource(item.getIconRes());
        holder.iconBg.setBackgroundResource(item.getBgRes());

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onFeatureClick(item.getLabel()));
        }
    }

    @Override
    public int getItemCount() {
        return featureList != null ? featureList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel;
        ImageView ivIcon;
        View iconBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_label);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            iconBg = itemView.findViewById(R.id.icon_bg);
        }
    }
}