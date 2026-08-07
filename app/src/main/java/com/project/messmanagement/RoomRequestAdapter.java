package com.project.messmanagement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoomRequestAdapter extends RecyclerView.Adapter<RoomRequestAdapter.ViewHolder> {

    public interface OnRequestClickListener {
        void onItemClick(RoomRequest request);
        void onItemLongClick(RoomRequest request);
    }

    private final List<RoomRequest> requests;
    private final OnRequestClickListener listener;

    public RoomRequestAdapter(List<RoomRequest> requests, OnRequestClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_request_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomRequest item = requests.get(position);
        holder.tvIssue.setText(item.issue);
        holder.tvInfo.setText("Room " + item.roomNo + " · Requested by " + item.memberName);
        holder.tvDate.setText(item.date);
        
        holder.tvPriority.setText(item.priority.toUpperCase());
        if (item.priority.equalsIgnoreCase("High") || item.priority.equalsIgnoreCase("Urgent")) {
            holder.tvPriority.setBackgroundResource(R.drawable.bg_badge_away);
            holder.tvPriority.setTextColor(Color.RED);
        } else {
            holder.tvPriority.setBackgroundResource(R.drawable.bg_badge_away);
            holder.tvPriority.setTextColor(Color.parseColor("#718096"));
        }

        holder.tvStatus.setText(item.status);
        if (item.status.equalsIgnoreCase("Completed") || item.status.equalsIgnoreCase("Solved")) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_active);
            holder.tvStatus.setTextColor(Color.parseColor("#1B6B3A"));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_away);
            holder.tvStatus.setTextColor(Color.parseColor("#B85C00"));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPriority, tvStatus, tvIssue, tvInfo, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPriority = itemView.findViewById(R.id.tvRequestPriority);
            tvStatus = itemView.findViewById(R.id.tvRequestStatus);
            tvIssue = itemView.findViewById(R.id.tvRequestIssue);
            tvInfo = itemView.findViewById(R.id.tvRequesterInfo);
            tvDate = itemView.findViewById(R.id.tvRequestDate);
        }
    }
}
