package com.project.messmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GuestMealsAdapter extends RecyclerView.Adapter<GuestMealsAdapter.ViewHolder> {

    public interface OnGuestMealClickListener {
        void onItemClick(GuestMeal item);
        void onLongClick(GuestMeal item);
    }

    private List<GuestMeal> items;
    private final OnGuestMealClickListener listener;

    public GuestMealsAdapter(List<GuestMeal> items, OnGuestMealClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setItems(List<GuestMeal> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guest_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GuestMeal item = items.get(position);
        holder.tvGuestName.setText(item.guestName);
        holder.tvGuestDetails.setText(String.format("%s · %s · %s", item.memberName, item.mealType, item.date));
        holder.tvMealCount.setText(String.format("%d Meals", item.mealCount));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGuestName, tvGuestDetails, tvMealCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuestName = itemView.findViewById(R.id.tvGuestName);
            tvGuestDetails = itemView.findViewById(R.id.tvGuestDetails);
            tvMealCount = itemView.findViewById(R.id.tvMealCount);
        }
    }
}
