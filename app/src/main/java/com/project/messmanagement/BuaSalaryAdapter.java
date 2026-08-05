package com.project.messmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class BuaSalaryAdapter extends RecyclerView.Adapter<BuaSalaryAdapter.ViewHolder> {

    private List<BuaSalary> items;

    public BuaSalaryAdapter(List<BuaSalary> items) {
        this.items = items;
    }

    public void setItems(List<BuaSalary> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salary_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BuaSalary item = items.get(position);
        holder.tvMonth.setText(item.monthYear);
        holder.tvAmount.setText(String.format(Locale.US, "৳%,.0f", item.amount));
        holder.tvPaidDate.setText("Paid on " + item.paidDate);
        holder.tvStatus.setText(item.status);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvAmount, tvPaidDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPaidDate = itemView.findViewById(R.id.tvPaidDate);
            tvStatus = itemView.findViewById(R.id.tvPayStatus);
        }
    }
}
