package com.example.support_management_system_mobile.ui.ticket.details;

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
import com.example.support_management_system_mobile.data.models.TicketReply;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TicketReplyAdapter extends ListAdapter<TicketReply, TicketReplyAdapter.ReplyViewHolder> {
    @FunctionalInterface
    public interface OnDeleteClickListener {
        void onDeleteClick(TicketReply reply);
    }

    private final OnDeleteClickListener onDeleteClickListener;
    private boolean canDeleteReplies = false;

    public TicketReplyAdapter(OnDeleteClickListener onDeleteClickListener) {
        super(DIFF_CALLBACK);
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDeleteReplies = canDelete;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        TicketReply reply = getItem(position);
        holder.bind(reply, canDeleteReplies, onDeleteClickListener);
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        final TextView contentText, authorText, dateText;
        final ImageButton deleteButton;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            contentText = itemView.findViewById(R.id.replyContentTextView);
            authorText = itemView.findViewById(R.id.replyAuthorTextView);
            dateText = itemView.findViewById(R.id.replyDateTextView);
            deleteButton = itemView.findViewById(R.id.deleteReplyButton);
        }

        public void bind(final TicketReply reply, boolean canDelete, final OnDeleteClickListener listener) {
            contentText.setText(reply.content());
            authorText.setText(reply.user().username());

            if (reply.createdDate() != null) {
                dateText.setText(reply.createdDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }

            if (canDelete) {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(v -> listener.onDeleteClick(reply));
            } else {
                deleteButton.setVisibility(View.GONE);
            }
        }
    }

    private static final DiffUtil.ItemCallback<TicketReply> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull TicketReply oldItem, @NonNull TicketReply newItem) {
            return Objects.equals(oldItem.id(), newItem.id());

        }

        @Override
        public boolean areContentsTheSame(@NonNull TicketReply oldItem, @NonNull TicketReply newItem) {
            return oldItem.equals(newItem);
        }
    };
}
