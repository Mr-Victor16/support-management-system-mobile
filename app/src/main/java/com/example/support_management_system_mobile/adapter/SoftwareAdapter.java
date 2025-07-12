package com.example.support_management_system_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.models.Software;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SoftwareAdapter extends RecyclerView.Adapter<SoftwareAdapter.ViewHolder> {
    private final List<Software> softwareList;
    private final Set<Integer> expandedPositions = new HashSet<>();

    public SoftwareAdapter(List<Software> softwareList) {
        this.softwareList = softwareList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.softwareName);
            description = itemView.findViewById(R.id.softwareDescription);
        }
    }

    @NonNull
    @Override
    public SoftwareAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supported_software, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoftwareAdapter.ViewHolder holder, int position) {
        Software software = softwareList.get(position);
        holder.name.setText(software.getName());
        holder.description.setText(software.getDescription());

        boolean isExpanded = expandedPositions.contains(position);
        holder.description.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (isExpanded) {
                expandedPositions.remove(position);
            } else {
                expandedPositions.add(position);
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return softwareList.size();
    }
}
