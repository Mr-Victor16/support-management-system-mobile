package com.example.support_management_system_mobile.ui.management.software;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.payload.response.SoftwareResponse;

public class SoftwareManageAdapter extends ListAdapter<SoftwareResponse, SoftwareManageAdapter.SoftwareViewHolder> {
    public interface OnSoftwareInteractionListener {
        void onItemClick(SoftwareResponse item);
        void onEdit(SoftwareResponse item);
        void onDelete(SoftwareResponse item);
    }

    private final OnSoftwareInteractionListener listener;
    private boolean canManage = false;

    public SoftwareManageAdapter(OnSoftwareInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public void setCanManage(boolean canManage) {
        if (this.canManage == canManage) {
            return;
        }

        this.canManage = canManage;
        notifyItemRangeChanged(0, getItemCount());
    }

    @NonNull
    @Override
    public SoftwareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management_software, parent, false);
        return new SoftwareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoftwareViewHolder holder, int position) {
        holder.bind(getItem(position), listener, canManage);
    }

    public static class SoftwareViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView, usageCountTextView;
        final ImageButton editButton, deleteButton;

        public SoftwareViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.softwareNameTextView);
            usageCountTextView = itemView.findViewById(R.id.usageCountTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final SoftwareResponse item, final OnSoftwareInteractionListener listener, boolean canManage) {
            nameTextView.setText(item.name());

            String usageText = itemView.getContext().getString(R.string.usage_count_software_format, item.useNumberTicket(), item.useNumberKnowledge());
            usageCountTextView.setText(usageText);

            int managementButtonsVisibility = canManage ? View.VISIBLE : View.GONE;
            editButton.setVisibility(managementButtonsVisibility);

            boolean isUsed = item.useNumberTicket() > 0 || item.useNumberKnowledge() > 0;
            if (canManage && !isUsed) deleteButton.setVisibility(View.VISIBLE);
            else deleteButton.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                if (!canManage) {
                    listener.onItemClick(item);
                }
            });

            editButton.setOnClickListener(v -> listener.onEdit(item));
            deleteButton.setOnClickListener(v -> listener.onDelete(item));
        }
    }

    private static final DiffUtil.ItemCallback<SoftwareResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull SoftwareResponse oldItem, @NonNull SoftwareResponse newItem) {
            return oldItem.softwareID().equals(newItem.softwareID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SoftwareResponse oldItem, @NonNull SoftwareResponse newItem) {
            return oldItem.equals(newItem);
        }
    };
}
