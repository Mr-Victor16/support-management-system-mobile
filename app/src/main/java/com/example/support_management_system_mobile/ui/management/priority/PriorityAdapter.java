package com.example.support_management_system_mobile.ui.management.priority;

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
import com.example.support_management_system_mobile.auth.AuthContext;
import com.example.support_management_system_mobile.payload.response.PriorityResponse;

public class PriorityAdapter extends ListAdapter<PriorityResponse, PriorityAdapter.PriorityViewHolder> {
    public interface OnPriorityInteractionListener {
        void onEdit(PriorityResponse priority);
        void onDelete(PriorityResponse priority);
    }

    private final OnPriorityInteractionListener listener;
    private final AuthContext authContext;

    public PriorityAdapter(OnPriorityInteractionListener listener, AuthContext authContext) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.authContext = authContext;
    }

    @NonNull
    @Override
    public PriorityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management_priority, parent, false);
        return new PriorityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriorityViewHolder holder, int position) {
        boolean isAdmin = authContext.isAdmin();
        holder.bind(getItem(position), listener, isAdmin);
    }

    static class PriorityViewHolder extends RecyclerView.ViewHolder {
        TextView priorityName, usageCount;
        ImageButton editButton, deleteButton;

        public PriorityViewHolder(@NonNull View itemView) {
            super(itemView);
            priorityName = itemView.findViewById(R.id.priorityNameTextView);
            usageCount = itemView.findViewById(R.id.usageCountTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final PriorityResponse priority, final OnPriorityInteractionListener listener, boolean isAdmin) {
            priorityName.setText(priority.getName());
            usageCount.setText(itemView.getContext().getString(R.string.usage_count_format, priority.getUseNumber()));

            boolean canBeModified = priority.getUseNumber() == 0  && isAdmin;
            editButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            deleteButton.setVisibility(canBeModified ? View.VISIBLE : View.GONE);
            editButton.setOnClickListener(v -> listener.onEdit(priority));
            deleteButton.setOnClickListener(v -> listener.onDelete(priority));
        }
    }

    private static final DiffUtil.ItemCallback<PriorityResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull PriorityResponse oldItem, @NonNull PriorityResponse newItem) {
            return oldItem.getPriorityID() == newItem.getPriorityID();
        }

        @Override
        public boolean areContentsTheSame(@NonNull PriorityResponse oldItem, @NonNull PriorityResponse newItem) {
            return oldItem.equals(newItem);
        }
    };
}
