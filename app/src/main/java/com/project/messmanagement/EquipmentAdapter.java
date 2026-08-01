package com.project.messmanagement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {

    private List<Equipment> list;

    public EquipmentAdapter(List<Equipment> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation, tvStatus;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvLocation = view.findViewById(R.id.tvLocation);
            tvStatus = view.findViewById(R.id.tvStatus);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Equipment eq = list.get(position);
        holder.tvName.setText(eq.name);
        holder.tvLocation.setText(eq.location);
        holder.tvStatus.setText(eq.status);

        switch (eq.status) {
            case "Available":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_active);
                holder.tvStatus.setTextColor(Color.parseColor("#1B6B3A"));
                break;
            case "Damaged":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_away);
                holder.tvStatus.setTextColor(Color.parseColor("#C0392B"));
                break;
            default: // In Use
                holder.tvStatus.setBackgroundResource(R.drawable.bg_icon_blue);
                holder.tvStatus.setTextColor(Color.parseColor("#1A4F8C"));
                break;
        }
    }

    @Override
    public int getItemCount() { return list.size(); }
}
