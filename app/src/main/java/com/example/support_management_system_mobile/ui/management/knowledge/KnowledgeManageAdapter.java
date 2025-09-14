package com.example.support_management_system_mobile.ui.management.knowledge;

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
import com.example.support_management_system_mobile.models.Knowledge;

import java.util.Objects;

public class KnowledgeManageAdapter extends ListAdapter<Knowledge, KnowledgeManageAdapter.KnowledgeViewHolder> {
    private final OnKnowledgeInteractionListener listener;
    private boolean canManage;

    public interface OnKnowledgeInteractionListener {
        void onEdit(Knowledge item);
        void onDelete(Knowledge item);
        void onItemClick(Knowledge item);
    }

    public KnowledgeManageAdapter(boolean canManage, OnKnowledgeInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.canManage = canManage;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KnowledgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management_knowledge, parent, false);
        return new KnowledgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KnowledgeViewHolder holder, int position) {
        Knowledge currentItem = getItem(position);

        if (currentItem != null) {
            holder.bind(currentItem, listener, canManage);
        }
    }

    static class KnowledgeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, softwareTextView, dateTextView;
        ImageButton editButton, deleteButton;

        public KnowledgeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.knowledgeTitleTextView);
            softwareTextView = itemView.findViewById(R.id.knowledgeSoftwareTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final Knowledge item, final OnKnowledgeInteractionListener listener, boolean canManage) {
            titleTextView.setText(item.getTitle());
            dateTextView.setText(item.getCreatedDate().toString());
            softwareTextView.setText(item.getSoftware().getName());

            int visibility = canManage ? View.VISIBLE : View.GONE;
            editButton.setVisibility(visibility);
            deleteButton.setVisibility(visibility);

            itemView.setOnClickListener(v -> listener.onItemClick(item));

            editButton.setOnClickListener(v -> listener.onEdit(item));
            deleteButton.setOnClickListener(v -> listener.onDelete(item));
        }
    }

    private static final DiffUtil.ItemCallback<Knowledge> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Knowledge oldItem, @NonNull Knowledge newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Knowledge oldItem, @NonNull Knowledge newItem) {
            return oldItem.equals(newItem);
        }
    };

    public void setCanManage(boolean canManage) {
        boolean needsUpdate = this.canManage != canManage;
        this.canManage = canManage;

        if (needsUpdate) {
            notifyDataSetChanged();
        }
    }
}
