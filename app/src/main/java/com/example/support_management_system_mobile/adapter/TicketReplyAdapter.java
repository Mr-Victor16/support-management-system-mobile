package com.example.support_management_system_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.models.TicketReply;
import com.example.support_management_system_mobile.models.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TicketReplyAdapter extends RecyclerView.Adapter<TicketReplyAdapter.ReplyViewHolder> {
    private final List<TicketReply> replies;
    private final String currentUserRole;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(TicketReply reply);
    }

    public TicketReplyAdapter(List<TicketReply> replies, String currentUserRole, OnDeleteClickListener onDeleteClickListener) {
        this.replies = replies;
        this.currentUserRole = currentUserRole;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public TicketReplyAdapter.ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketReplyAdapter.ReplyViewHolder holder, int position) {
        TicketReply reply = replies.get(position);
        User user = reply.getUser();

        holder.contentText.setText(reply.getContent());
        holder.authorText.setText(user.getUsername());

        holder.dateText.setText(reply.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if ("ROLE_OPERATOR".equals(currentUserRole)) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(reply);
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView contentText, authorText, dateText;
        ImageButton deleteButton;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            contentText = itemView.findViewById(R.id.replyContentTextView);
            authorText = itemView.findViewById(R.id.replyAuthorTextView);
            dateText = itemView.findViewById(R.id.replyDateTextView);
            deleteButton = itemView.findViewById(R.id.deleteReplyButton);
        }
    }
}
