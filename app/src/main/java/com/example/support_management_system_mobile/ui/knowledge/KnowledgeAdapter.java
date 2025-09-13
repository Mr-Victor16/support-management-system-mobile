package com.example.support_management_system_mobile.ui.knowledge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.models.Knowledge;
import com.example.support_management_system_mobile.models.Software;

public class KnowledgeAdapter extends ListAdapter<KnowledgeUIModel, KnowledgeAdapter.ViewHolder> {
    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClicked(KnowledgeUIModel item);
    }

    private OnItemClickListener clickListener;

    public KnowledgeAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
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

        public void bind(KnowledgeUIModel model, OnItemClickListener listener) {
            Knowledge knowledge = model.knowledge();

            title.setText(knowledge.getTitle());
            content.setText(knowledge.getContent());
            date.setText(knowledge.getCreatedDate().toString());

            Software software = knowledge.getSoftware();
            if (software != null) {
                softwareName.setText(software.getName());
                softwareName.setVisibility(View.VISIBLE);
            } else {
                softwareName.setVisibility(View.GONE);
            }

            content.setVisibility(model.isExpanded() ? View.VISIBLE : View.GONE);

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
                .inflate(R.layout.item_knowledge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KnowledgeUIModel currentItem = getItem(position);
        if (currentItem != null) {
            holder.bind(currentItem, clickListener);
        }
    }

    private static final DiffUtil.ItemCallback<KnowledgeUIModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull KnowledgeUIModel oldItem, @NonNull KnowledgeUIModel newItem) {
            return oldItem.knowledge().getId().equals(newItem.knowledge().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull KnowledgeUIModel oldItem, @NonNull KnowledgeUIModel newItem) {
            return oldItem.knowledge().equals(newItem.knowledge()) &&
                    oldItem.isExpanded() == newItem.isExpanded();
        }
    };
}
