package com.example.support_management_system_mobile.ui.management.status;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.payload.response.StatusResponse;

public class StatusAdapter extends ListAdapter<StatusResponse, StatusAdapter.StatusViewHolder> {
    public interface OnStatusInteractionListener {
        void onEdit(StatusResponse item);
        void onDelete(StatusResponse item);
    }

    private final StatusAdapter.OnStatusInteractionListener listener;
    private boolean canManage = false;

    public StatusAdapter(StatusAdapter.OnStatusInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public void setCanManage(boolean canManage) {
        boolean needsUpdate = this.canManage != canManage;
        this.canManage = canManage;

        if (needsUpdate) {
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public StatusAdapter.StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management_status, parent, false);
        return new StatusAdapter.StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.StatusViewHolder holder, int position) {
        holder.bind(getItem(position), listener, canManage);
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, usageCountTextView;
        ImageView closesTicketIcon, defaultStatusIcon;
        ImageButton editButton, deleteButton;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.statusNameTextView);
            usageCountTextView = itemView.findViewById(R.id.usageCountTextView);
            closesTicketIcon = itemView.findViewById(R.id.closesTicketIcon);
            defaultStatusIcon = itemView.findViewById(R.id.defaultStatusIcon);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final StatusResponse item, final StatusAdapter.OnStatusInteractionListener listener, boolean canManage) {
            nameTextView.setText(item.getName());

            String usageText = itemView.getContext().getString(R.string.usage_count_status_format, item.getUseNumber());
            usageCountTextView.setText(usageText);

            closesTicketIcon.setVisibility(item.isCloseTicket() ? View.VISIBLE : View.GONE);
            defaultStatusIcon.setVisibility(item.isDefaultStatus() ? View.VISIBLE : View.GONE);

            int managementButtonsVisibility = canManage ? View.VISIBLE : View.GONE;
            editButton.setVisibility(managementButtonsVisibility);

            boolean isUsed = item.getUseNumber() > 0;

            if (canManage && !isUsed && !item.isDefaultStatus()) {
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                deleteButton.setVisibility(View.GONE);
            }

            editButton.setOnClickListener(v -> listener.onEdit(item));
            deleteButton.setOnClickListener(v -> listener.onDelete(item));
        }
    }

    private static final DiffUtil.ItemCallback<StatusResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull StatusResponse oldItem, @NonNull StatusResponse newItem) {
            return oldItem.getStatusID() == newItem.getStatusID();
        }

        @Override
        public boolean areContentsTheSame(@NonNull StatusResponse oldItem, @NonNull StatusResponse newItem) {
            return oldItem.equals(newItem);
        }
    };
}
