package com.example.support_management_system_mobile.ui.ticket.list;

import androidx.annotation.StringRes;

import com.example.support_management_system_mobile.models.Ticket;

import java.util.List;

public abstract class TicketListUIState {
    private TicketListUIState() { }

    public static final class Loading extends TicketListUIState {
        @StringRes
        public final int headerTextResId;

        public Loading(@StringRes int headerTextResId) {
            this.headerTextResId = headerTextResId;
        }
    }

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

        @StringRes
        public final int headerTextResId;

        public Error(String message, @StringRes int headerTextResId) {
            this.message = message;
            this.headerTextResId = headerTextResId;
        }
    }

    public static class AccessDenied extends TicketListUIState {
        public final String message;

        @StringRes
        public final int headerTextResId;

        public AccessDenied(String message, @StringRes int headerTextResId) {
            this.message = message;
            this.headerTextResId = headerTextResId;
        }
    }
}
