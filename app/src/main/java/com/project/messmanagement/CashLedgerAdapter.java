package com.project.messmanagement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class CashLedgerAdapter extends RecyclerView.Adapter<CashLedgerAdapter.ViewHolder> {

    public interface OnTransactionClickListener {
        void onItemClick(int position, CashTransaction transaction);
    }

    public interface OnTransactionLongClickListener {
        void onItemLongClick(int position, CashTransaction transaction);
    }

    private List<CashTransaction> transactions;
    private final OnTransactionClickListener clickListener;
    private final OnTransactionLongClickListener longClickListener;

    public CashLedgerAdapter(List<CashTransaction> transactions,
                             OnTransactionClickListener clickListener,
                             OnTransactionLongClickListener longClickListener) {
        this.transactions = transactions;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    /** Call this after mutating the underlying list (e.g. after a DB reload). */
    public void setTransactions(List<CashTransaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvDesc, tvDate, tvAmount, tvPerformed;

        public ViewHolder(View view) {
            super(view);
            ivIcon   = view.findViewById(R.id.iv_transaction_icon);
            tvDesc   = view.findViewById(R.id.tv_transaction_desc);
            tvDate   = view.findViewById(R.id.tv_transaction_date);
            tvAmount = view.findViewById(R.id.tv_transaction_amount);
            tvPerformed = view.findViewById(R.id.tv_performed_by);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CashTransaction t = transactions.get(position);
        boolean isIn = "IN".equalsIgnoreCase(t.type);

        holder.tvDesc.setText(t.description);
        holder.tvDate.setText(t.date);
        holder.tvAmount.setText(formatAmount(t.amount, isIn));
        holder.tvPerformed.setText("Recorded by: " + t.performedBy);

        if (isIn) {
            holder.ivIcon.setColorFilter(Color.parseColor("#4CAF50"));
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.ivIcon.setColorFilter(Color.parseColor("#F44336"));
            holder.tvAmount.setTextColor(Color.parseColor("#F44336"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(holder.getAdapterPosition(), t);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(holder.getAdapterPosition(), t);
            }
            return true; // consume the long click so it doesn't also fire a normal click
        });
    }

    private String formatAmount(double amount, boolean isIn) {
        return String.format(Locale.getDefault(), "%s৳%,d", isIn ? "+" : "-", (int) amount);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}