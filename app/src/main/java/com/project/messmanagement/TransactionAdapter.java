package com.project.messmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<TransactionItem> transactionList;

    public TransactionAdapter(List<TransactionItem> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (transactionList == null || transactionList.isEmpty()) return;
        TransactionItem item = transactionList.get(position);
        holder.tvDesc.setText(item.getDescription());
        holder.tvDate.setText(item.getDate());
        holder.tvAmount.setText(item.getAmount());

        if (item.isIncoming()) {
            holder.iconBg.setBackgroundResource(R.drawable.transaction_icon_incoming);
            holder.ivIcon.setImageResource(R.drawable.ic_arrow_up_right);
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.amount_positive));
        } else {
            holder.iconBg.setBackgroundResource(R.drawable.transaction_icon_outgoing);
            holder.ivIcon.setImageResource(R.drawable.ic_arrow_down_left);
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.amount_negative));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList != null ? transactionList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDesc, tvDate, tvAmount;
        View iconBg;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tv_transaction_desc);
            tvDate = itemView.findViewById(R.id.tv_transaction_date);
            tvAmount = itemView.findViewById(R.id.tv_transaction_amount);
            iconBg = itemView.findViewById(R.id.icon_bg);
            ivIcon = itemView.findViewById(R.id.iv_transaction_icon);
        }
    }
}