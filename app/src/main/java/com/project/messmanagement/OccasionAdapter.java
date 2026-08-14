package com.project.messmanagement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class OccasionAdapter extends RecyclerView.Adapter<OccasionAdapter.ViewHolder> {

    public interface OnOccasionClickListener {
        void onItemClick(Occasion item);
        void onLongClick(Occasion item);
    }

    private List<Occasion> items;
    private final OnOccasionClickListener listener;

    public OccasionAdapter(List<Occasion> items, OnOccasionClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setItems(List<Occasion> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_occasion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Occasion item = items.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvType.setText(item.type);
        holder.tvCost.setText(String.format(Locale.US, "৳%,.0f", item.totalCost));
        holder.tvDetails.setText(String.format(Locale.US, "%s · %d members", item.date, item.memberCount));
        
        double perHead = item.memberCount > 0 ? item.totalCost / item.memberCount : 0;
        holder.tvPerHead.setText(String.format(Locale.US, "Per head: ৳%,.0f", perHead));
        holder.tvAddedBy.setText("Added by: " + (item.addedBy != null ? item.addedBy : "System"));

        // Color based on type
        if ("Festival".equalsIgnoreCase(item.type)) {
            holder.tvType.setTextColor(Color.parseColor("#9C27B0"));
            holder.tvType.getBackground().setTint(Color.parseColor("#F5E6FF"));
        } else if ("Social".equalsIgnoreCase(item.type)) {
            holder.tvType.setTextColor(Color.parseColor("#1B7A9E"));
            holder.tvType.getBackground().setTint(Color.parseColor("#D1E9F6"));
        } else if ("Birthday".equalsIgnoreCase(item.type)) {
            holder.tvType.setTextColor(Color.parseColor("#E91E63"));
            holder.tvType.getBackground().setTint(Color.parseColor("#FFE4F2"));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvType, tvCost, tvDetails, tvPerHead, tvAddedBy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvOccasionTitle);
            tvType = itemView.findViewById(R.id.tvOccasionType);
            tvCost = itemView.findViewById(R.id.tvOccasionCost);
            tvDetails = itemView.findViewById(R.id.tvOccasionDetails);
            tvPerHead = itemView.findViewById(R.id.tvPerHead);
            tvAddedBy = itemView.findViewById(R.id.tvOccasionAddedBy);
        }
    }
}
