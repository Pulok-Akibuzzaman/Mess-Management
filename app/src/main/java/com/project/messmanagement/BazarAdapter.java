package com.project.messmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class BazarAdapter extends RecyclerView.Adapter<BazarAdapter.ViewHolder> {

    public interface OnBazarLongClickListener {
        void onLongClick(Bazar item);
    }

    public interface OnBazarClickListener {
        void onItemClick(Bazar item);
    }

    private List<Bazar> items;
    private final OnBazarLongClickListener longClickListener;
    private final OnBazarClickListener clickListener;

    public BazarAdapter(List<Bazar> items, OnBazarLongClickListener longClickListener,
                        OnBazarClickListener clickListener) {
        this.items = items;
        this.longClickListener = longClickListener;
        this.clickListener = clickListener;
    }

    /** Call this after mutating the underlying list (e.g. after a DB reload). */
    public void setItems(List<Bazar> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemDetails, tvPrice;

        public ViewHolder(View view) {
            super(view);
            tvItemName    = view.findViewById(R.id.tv_item_name);
            tvItemDetails = view.findViewById(R.id.tv_item_details);
            tvPrice       = view.findViewById(R.id.tv_price);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_purchase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bazar item = items.get(position);

        holder.tvItemName.setText(item.name);
        holder.tvItemDetails.setText(item.date);
        holder.tvPrice.setText(formatAmount(item.amount));

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(item);
            }
            return true; // consume the long click so it doesn't also fire a normal click
        });
    }

    private String formatAmount(Double amount) {
        if (amount == null) return "৳0";
        if (amount == Math.floor(amount)) {
            return String.format(Locale.getDefault(), "৳%,.0f", amount);
        }
        return String.format(Locale.getDefault(), "৳%,.2f", amount);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}