package com.project.messmanagement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoomRequestAdapter extends RecyclerView.Adapter<RoomRequestAdapter.ViewHolder> {

    private List<RoomRequest> list;

    public RoomRequestAdapter(List<RoomRequest> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName, tvRoomDate, tvIssue, tvStatus;

        public ViewHolder(View view) {
            super(view);
            tvMemberName = view.findViewById(R.id.tvMemberName);
            tvRoomDate   = view.findViewById(R.id.tvRoomDate);
            tvIssue      = view.findViewById(R.id.tvIssue);
            tvStatus     = view.findViewById(R.id.tvStatus);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RoomRequest req = list.get(position);
        holder.tvMemberName.setText(req.memberName);
        holder.tvRoomDate.setText(req.room + " · " + req.date);
        holder.tvIssue.setText(req.issue);
        holder.tvStatus.setText(req.status);

        switch (req.status) {
            case "In Progress":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_icon_blue);
                holder.tvStatus.setTextColor(Color.parseColor("#1A4F8C"));
                break;
            case "Completed":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_active);
                holder.tvStatus.setTextColor(Color.parseColor("#1B6B3A"));
                break;
            default: // Pending
                holder.tvStatus.setBackgroundResource(R.drawable.bg_icon_orange);
                holder.tvStatus.setTextColor(Color.parseColor("#B85C00"));
                break;
        }
    }

    @Override
    public int getItemCount() { return list.size(); }
}