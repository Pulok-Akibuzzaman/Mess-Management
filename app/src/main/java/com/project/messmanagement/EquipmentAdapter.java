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

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {

    public interface OnEquipmentClickListener {
        void onItemClick(Equipment equipment);
        void onItemLongClick(Equipment equipment);
    }

    private final List<Equipment> equipmentList;
    private final OnEquipmentClickListener listener;

    public EquipmentAdapter(List<Equipment> equipmentList, OnEquipmentClickListener listener) {
        this.equipmentList = equipmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equipment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Equipment item = equipmentList.get(position);
        holder.tvName.setText(item.name);
        holder.tvLocation.setText(item.location);
        holder.tvDate.setText(item.date);
        holder.tvPrice.setText(String.format(Locale.US, "৳%,.0f", item.price));
        
        holder.tvStatus.setText(item.status);
        if (item.status.equalsIgnoreCase("Good Condition") || item.status.equalsIgnoreCase("Working")) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_active);
            holder.tvStatus.setTextColor(Color.parseColor("#1B6B3A"));
        } else if (item.status.equalsIgnoreCase("Need Repair")) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_away);
            holder.tvStatus.setTextColor(Color.parseColor("#B85C00"));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_away);
            holder.tvStatus.setTextColor(Color.RED);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation, tvStatus, tvPrice, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEquipmentName);
            tvLocation = itemView.findViewById(R.id.tvEquipmentLocation);
            tvStatus = itemView.findViewById(R.id.tvEquipmentStatus);
            tvPrice = itemView.findViewById(R.id.tvEquipmentPrice);
            tvDate = itemView.findViewById(R.id.tvEquipmentDate);
        }
    }
}
