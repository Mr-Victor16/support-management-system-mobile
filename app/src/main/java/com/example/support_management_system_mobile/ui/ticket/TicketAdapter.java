package com.example.support_management_system_mobile.ui.ticket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.models.Ticket;

import java.time.format.DateTimeFormatter;

public class TicketAdapter extends ListAdapter<Ticket, TicketAdapter.TicketViewHolder> {
    @FunctionalInterface
    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }

    private final OnTicketClickListener listener;

    public TicketAdapter(OnTicketClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = getItem(position);
        holder.bind(ticket, listener);
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, status;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ticketTitle);
            date = itemView.findViewById(R.id.ticketDate);
            status = itemView.findViewById(R.id.ticketStatus);
        }

        public void bind(final Ticket ticket, final OnTicketClickListener listener) {
            title.setText(ticket.getTitle());
            date.setText(ticket.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            status.setText(ticket.getStatus().getName());
            itemView.setOnClickListener(v -> listener.onTicketClick(ticket));
        }
    }

    private static final DiffUtil.ItemCallback<Ticket> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Ticket oldItem, @NonNull Ticket newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Ticket oldItem, @NonNull Ticket newItem) {
            return oldItem.equals(newItem);
        }
    };
}
