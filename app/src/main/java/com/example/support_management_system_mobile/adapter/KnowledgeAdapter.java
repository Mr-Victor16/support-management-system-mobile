package com.example.support_management_system_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.payload.response.Knowledge;
import com.example.support_management_system_mobile.payload.response.Software;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KnowledgeAdapter extends RecyclerView.Adapter<KnowledgeAdapter.ViewHolder> {
    private final List<Knowledge> knowledgeList;
    private final Set<Integer> expandedPositions = new HashSet<>();

    public KnowledgeAdapter(List<Knowledge> knowledgeList) {
        this.knowledgeList = knowledgeList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content, date, softwareName;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.knowledgeTitle);
            content = view.findViewById(R.id.knowledgeContent);
            date = view.findViewById(R.id.knowledgeDate);
            softwareName = view.findViewById(R.id.knowledgeSoftware);
        }
    }

    @NonNull
    @Override
    public KnowledgeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_knowledge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KnowledgeAdapter.ViewHolder holder, int position) {
        Knowledge knowledge = knowledgeList.get(position);
        holder.title.setText(knowledge.getTitle());
        holder.content.setText(knowledge.getContent());
        holder.date.setText(knowledge.getCreatedDate().toString());

        Software software = knowledge.getSoftware();
        if (software != null) holder.softwareName.setText(software.getName());

        boolean isExpanded = expandedPositions.contains(position);
        holder.content.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

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
        return knowledgeList.size();
    }
}
