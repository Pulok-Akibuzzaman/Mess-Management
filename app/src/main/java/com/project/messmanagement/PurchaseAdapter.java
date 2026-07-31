package com.project.messmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {

    private List<PurchaseItem> purchaseList;

    public PurchaseAdapter(List<PurchaseItem> purchaseList) {
        this.purchaseList = purchaseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (purchaseList == null || purchaseList.isEmpty()) return;
        PurchaseItem item = purchaseList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDetails.setText(item.getDate());
        holder.tvPrice.setText(item.getPrice());
    }

    @Override
    public int getItemCount() {
        return purchaseList != null ? purchaseList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvDetails = itemView.findViewById(R.id.tv_item_details);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}