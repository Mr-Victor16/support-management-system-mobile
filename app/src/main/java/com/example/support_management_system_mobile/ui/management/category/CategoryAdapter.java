package com.example.support_management_system_mobile.ui.management.category;

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
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.data.payload.response.CategoryResponse;

public class CategoryAdapter extends ListAdapter<CategoryResponse, CategoryAdapter.CategoryViewHolder> {
    public interface OnCategoryInteractionListener {
        void onEdit(CategoryResponse category);
        void onDelete(CategoryResponse category);
    }

    private final OnCategoryInteractionListener listener;
    private final AuthContext authContext;

    public CategoryAdapter(OnCategoryInteractionListener listener, AuthContext authContext) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.authContext = authContext;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        boolean isAdmin = authContext.isAdmin();
        holder.bind(getItem(position), listener, isAdmin);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, usageCount;
        ImageButton editButton, deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryNameTextView);
            usageCount = itemView.findViewById(R.id.usageCountTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final CategoryResponse category, final OnCategoryInteractionListener listener, boolean isAdmin) {
            categoryName.setText(category.name());
            usageCount.setText(itemView.getContext().getString(R.string.usage_count_format, category.useNumber()));

            boolean canBeModified = category.useNumber() == 0 && isAdmin;
            editButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            deleteButton.setVisibility(canBeModified ? View.VISIBLE : View.GONE);
            editButton.setOnClickListener(v -> listener.onEdit(category));
            deleteButton.setOnClickListener(v -> listener.onDelete(category));
        }
    }

    private static final DiffUtil.ItemCallback<CategoryResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull CategoryResponse oldItem, @NonNull CategoryResponse newItem) {
            return oldItem.categoryID().equals(newItem.categoryID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoryResponse oldItem, @NonNull CategoryResponse newItem) {
            return oldItem.equals(newItem);
        }
    };
}
