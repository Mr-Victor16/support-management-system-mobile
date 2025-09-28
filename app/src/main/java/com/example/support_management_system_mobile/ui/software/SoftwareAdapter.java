package com.example.support_management_system_mobile.ui.software;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.models.Software;

public class SoftwareAdapter extends ListAdapter<SoftwareUIModel, SoftwareAdapter.ViewHolder> {
    private OnItemClickListener clickListener;

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClicked(SoftwareUIModel item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public SoftwareAdapter() {
        super(DIFF_CALLBACK);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name, description;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.softwareName);
            description = itemView.findViewById(R.id.softwareDescription);
        }

        public void bind(SoftwareUIModel model, OnItemClickListener listener) {
            Software software = model.software();
            name.setText(software.name());
            description.setText(software.description());

            description.setVisibility(model.isExpanded() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClicked(model);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supported_software, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SoftwareUIModel currentItem = getItem(position);
        if (currentItem != null) {
            holder.bind(currentItem, clickListener);
        }
    }

    private static final DiffUtil.ItemCallback<SoftwareUIModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull SoftwareUIModel oldItem, @NonNull SoftwareUIModel newItem) {
            return oldItem.software().id().equals(newItem.software().id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SoftwareUIModel oldItem, @NonNull SoftwareUIModel newItem) {
            return oldItem.equals(newItem);
        }
    };
}
