package com.project.messmanagement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    private List<Notice> list;

    public NoticeAdapter(List<Notice> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetails, tvPriority;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDetails = view.findViewById(R.id.tvDetails);
            tvPriority = view.findViewById(R.id.tvPriority);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notice notice = list.get(position);
        holder.tvTitle.setText(notice.title);
        holder.tvDetails.setText(notice.details);
        holder.tvPriority.setText(notice.priority);

        if (notice.priority.equals("High")) {
            holder.tvPriority.setBackgroundResource(R.drawable.bg_badge_away);
            holder.tvPriority.setTextColor(Color.parseColor("#C0392B"));
        } else {
            holder.tvPriority.setBackgroundResource(R.drawable.bg_icon_orange);
            holder.tvPriority.setTextColor(Color.parseColor("#B85C00"));
        }
    }

    @Override
    public int getItemCount() { return list.size(); }
}
