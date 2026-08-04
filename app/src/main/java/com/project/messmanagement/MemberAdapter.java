package com.project.messmanagement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    public interface OnMemberLongClickListener {
        void onLongClick(Member member);
    }

    private List<Member> members;
    private final OnMemberLongClickListener longClickListener;

    public MemberAdapter(List<Member> members, OnMemberLongClickListener longClickListener) {
        this.members = members;
        this.longClickListener = longClickListener;
    }

    /** Call this after mutating the underlying list (e.g. after a DB reload). */
    public void setMembers(List<Member> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials, tvName, tvStatus, tvRoomPhone, tvMeals, tvDue;

        public ViewHolder(View view) {
            super(view);
            tvInitials  = view.findViewById(R.id.tvInitials);
            tvName      = view.findViewById(R.id.tvName);
            tvStatus    = view.findViewById(R.id.tvStatus);
            tvRoomPhone = view.findViewById(R.id.tvRoomPhone);
            tvMeals     = view.findViewById(R.id.tvMeals);
            tvDue       = view.findViewById(R.id.tvDue);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_membercard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Member member = members.get(position);

        holder.tvInitials.setText(member.initials);
        holder.tvName.setText(member.name);
        holder.tvStatus.setText(member.status);
        holder.tvRoomPhone.setText(member.room + " · " + member.phone);
        holder.tvMeals.setText(String.valueOf(member.meals));
        holder.tvDue.setText(member.due);

        if ("Active".equalsIgnoreCase(member.status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_active);
            holder.tvStatus.setTextColor(Color.parseColor("#1B6B3A"));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_away);
            holder.tvStatus.setTextColor(Color.parseColor("#B85C00"));
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(member);
            }
            return true; // consume the long click so it doesn't also fire a normal click
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}