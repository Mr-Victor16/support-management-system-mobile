package com.example.support_management_system_mobile.ui.management.user;

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
import com.example.support_management_system_mobile.data.payload.response.UserDetailsResponse;

public class UserAdapter extends ListAdapter<UserDetailsResponse, UserAdapter.UserViewHolder> {
    public interface OnUserInteractionListener {
        void onEdit(UserDetailsResponse item);
        void onDelete(UserDetailsResponse item);
    }

    private final UserAdapter.OnUserInteractionListener listener;
    private boolean canManage = false;
    private final AuthContext authContext;

    public UserAdapter(UserAdapter.OnUserInteractionListener listener, AuthContext authContext) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.authContext = authContext;
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
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management_user, parent, false);
        return new UserAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        Long userId = authContext.getCurrentUser().getId();

        holder.bind(getItem(position), listener, userId);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, roleTextView;
        ImageButton editButton, deleteButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final UserDetailsResponse item, final UserAdapter.OnUserInteractionListener listener, Long userId) {
            usernameTextView.setText(item.username());
            roleTextView.setText(item.role().toString());

            boolean canManage = !item.id().equals(userId);
            int managementButtonsVisibility = canManage ? View.VISIBLE : View.GONE;

            editButton.setVisibility(managementButtonsVisibility);
            deleteButton.setVisibility(managementButtonsVisibility);

            editButton.setOnClickListener(v -> listener.onEdit(item));
            deleteButton.setOnClickListener(v -> listener.onDelete(item));
        }
    }

    private static final DiffUtil.ItemCallback<UserDetailsResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull UserDetailsResponse oldItem, @NonNull UserDetailsResponse newItem) {
            return oldItem.id().equals(newItem.id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull UserDetailsResponse oldItem, @NonNull UserDetailsResponse newItem) {
            return oldItem.equals(newItem);
        }
    };
}
