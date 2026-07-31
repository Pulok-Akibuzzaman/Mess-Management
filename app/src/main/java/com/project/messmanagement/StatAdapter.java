package com.project.messmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.ViewHolder> {

    private List<StatItem> statList;

    public StatAdapter(List<StatItem> statList) {
        this.statList = statList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (statList == null || statList.isEmpty()) return;
        StatItem item = statList.get(position);
        holder.tvLabel.setText(item.getLabel());
        holder.tvValue.setText(item.getValue());

        if (item.isUrgent()) {
            holder.tvValue.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.amount_due_red));
        } else {
            holder.tvValue.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_dark));
        }
    }

    @Override
    public int getItemCount() {
        return statList != null ? statList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_stat_label);
            tvValue = itemView.findViewById(R.id.tv_stat_value);
        }
    }
}