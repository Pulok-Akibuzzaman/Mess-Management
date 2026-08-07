package com.project.messmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    public interface OnFieldClickListener {
        void onFieldClick(ProfileField field);
    }

    private final List<ProfileField> fields;
    private final OnFieldClickListener listener;

    public ProfileAdapter(List<ProfileField> fields, OnFieldClickListener listener) {
        this.fields = fields;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_field, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProfileField field = fields.get(position);
        holder.tvLabel.setText(field.label);
        holder.tvValue.setText(field.value);
        holder.ivIcon.setImageResource(field.iconRes);
        
        if (field.isEditable) {
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v -> listener.onFieldClick(field));
        } else {
            holder.ivEdit.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon, ivEdit;
        TextView tvLabel, tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivFieldIcon);
            tvLabel = itemView.findViewById(R.id.tvFieldLabel);
            tvValue = itemView.findViewById(R.id.tvFieldValue);
            ivEdit = itemView.findViewById(R.id.ivEditIcon);
        }
    }
}
