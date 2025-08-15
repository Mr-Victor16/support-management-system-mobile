package com.example.support_management_system_mobile.ui.ticket;

import androidx.annotation.StringRes;

import com.example.support_management_system_mobile.models.Ticket;

import java.util.List;

public abstract class TicketListUIState {
    private TicketListUIState() { }

    public static final class Loading extends TicketListUIState { }

    public static final class Success extends TicketListUIState {
        public final List<Ticket> tickets;
        public final boolean canAddTicket;

        @StringRes
        public final int headerTextResId;

        public Success(List<Ticket> tickets, boolean canAddTicket, @StringRes int headerTextResId) {
            this.tickets = tickets;
            this.canAddTicket = canAddTicket;
            this.headerTextResId = headerTextResId;
        }
    }

    public static final class Error extends TicketListUIState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
