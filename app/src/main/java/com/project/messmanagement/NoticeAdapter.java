package com.project.messmanagement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    public interface OnNoticeClickListener {
        void onItemClick(Notice notice);
        void onItemLongClick(Notice notice);
    }

    private final List<Notice> notices;
    private final OnNoticeClickListener listener;

    public NoticeAdapter(List<Notice> notices, OnNoticeClickListener listener) {
        this.notices = notices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notice notice = notices.get(position);
        holder.tvTitle.setText(notice.title);
        holder.tvContent.setText(notice.content);
        holder.tvDate.setText(notice.date);
        holder.tvAudience.setText("For: " + notice.audience);
        
        holder.tvPriority.setText(notice.priority.toUpperCase());
        if (notice.priority.equalsIgnoreCase("High")) {
            holder.tvPriority.setBackgroundResource(R.drawable.bg_badge_active);
            holder.tvPriority.setTextColor(Color.WHITE);
            holder.tvPriority.getBackground().setTint(Color.parseColor("#E53E3E"));
        } else if (notice.priority.equalsIgnoreCase("Medium")) {
            holder.tvPriority.setBackgroundResource(R.drawable.bg_badge_active);
            holder.tvPriority.setTextColor(Color.WHITE);
            holder.tvPriority.getBackground().setTint(Color.parseColor("#3182CE"));
        } else {
            holder.tvPriority.setBackgroundResource(R.drawable.bg_badge_active);
            holder.tvPriority.setTextColor(Color.WHITE);
            holder.tvPriority.getBackground().setTint(Color.parseColor("#718096"));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(notice));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(notice);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPriority, tvTitle, tvContent, tvDate, tvAudience;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPriority = itemView.findViewById(R.id.tvPriorityBadge);
            tvTitle = itemView.findViewById(R.id.tvNoticeTitle);
            tvContent = itemView.findViewById(R.id.tvNoticeContent);
            tvDate = itemView.findViewById(R.id.tvNoticeDate);
            tvAudience = itemView.findViewById(R.id.tvNoticeAudience);
        }
    }
}
