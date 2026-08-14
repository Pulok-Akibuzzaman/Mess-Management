package com.project.messmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    public interface OnComplaintClickListener {
        void onItemLongClick(Complaint complaint);
    }

    private final List<Complaint> complaints;
    private final OnComplaintClickListener listener;

    public ComplaintAdapter(List<Complaint> complaints, OnComplaintClickListener listener) {
        this.complaints = complaints;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complaint_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Complaint item = complaints.get(position);
        holder.tvMessage.setText(item.message);
        holder.tvDate.setText(item.date);
        holder.tvAddedBy.setText("Added by: " + (item.addedBy != null ? item.addedBy : "Anonymous"));
        
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvDate, tvAddedBy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvComplaintMessage);
            tvDate = itemView.findViewById(R.id.tvComplaintDate);
            tvAddedBy = itemView.findViewById(R.id.tvComplaintAddedBy);
        }
    }
}
