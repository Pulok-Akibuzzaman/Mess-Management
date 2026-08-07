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

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder> {

    public interface OnLoanClickListener {
        void onItemClick(Loan loan);
        void onItemLongClick(Loan loan);
    }

    private final List<Loan> loans;
    private final OnLoanClickListener listener;

    public LoanAdapter(List<Loan> loans, OnLoanClickListener listener) {
        this.loans = loans;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loan_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Loan item = loans.get(position);
        holder.tvLender.setText(item.lender);
        holder.tvDate.setText(item.date);
        holder.tvAmount.setText(String.format(Locale.US, "৳%,.0f", item.amount));
        
        holder.tvStatus.setText(item.status);
        if (item.status.equalsIgnoreCase("Paid")) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_active);
            holder.tvStatus.setTextColor(Color.parseColor("#1B6B3A"));
        } else if (item.status.equalsIgnoreCase("Urgent")) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_away);
            holder.tvStatus.setTextColor(Color.RED);
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_away);
            holder.tvStatus.setTextColor(Color.parseColor("#E53E3E"));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLender, tvDate, tvStatus, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLender = itemView.findViewById(R.id.tvLenderName);
            tvDate = itemView.findViewById(R.id.tvLoanDate);
            tvStatus = itemView.findViewById(R.id.tvLoanStatus);
            tvAmount = itemView.findViewById(R.id.tvLoanAmount);
        }
    }
}
