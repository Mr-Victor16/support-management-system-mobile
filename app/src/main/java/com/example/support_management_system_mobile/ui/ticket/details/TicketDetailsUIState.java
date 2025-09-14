package com.example.support_management_system_mobile.ui.ticket.details;

import androidx.annotation.StringRes;

import com.example.support_management_system_mobile.data.models.Role;
import com.example.support_management_system_mobile.data.models.Ticket;
import com.example.support_management_system_mobile.data.models.User;

public abstract class TicketDetailsUIState {
    private TicketDetailsUIState() { }

    public static final class Loading extends TicketDetailsUIState { }

    public static final class Error extends TicketDetailsUIState {
        @StringRes
        public final int messageTextResId;

        public Error(@StringRes int messageTextResId) {
            this.messageTextResId = messageTextResId;
        }
    }

    public static final class Success extends TicketDetailsUIState {
        public final Ticket ticket;
        public final TicketDetailsControlsState controls;
        public final boolean isClosedNoticeVisible;
        public final int imageCount;

        public Success(Ticket ticket, User currentUser) {
            this.ticket = ticket;

            boolean isOwner = currentUser.getId().equals(ticket.getUser().getId());
            boolean isOperator = currentUser.getRole().getType().equals(Role.Types.ROLE_OPERATOR);
            boolean isTicketClosed = ticket.getStatus().isCloseTicket();

            this.imageCount = (ticket.getImages() != null) ? ticket.getImages().size() : 0;
            boolean hasImages = this.imageCount > 0;

            boolean canEditImages = (isOwner && !isTicketClosed) || isOperator;
            boolean canShowManageImagesButton = canEditImages || hasImages;

            this.controls = new TicketDetailsControlsState(
                    /* canEditTicket: */ (isOwner && !isTicketClosed) || isOperator,
                    /* canDeleteTicket: */ (isOwner && !isTicketClosed) || isOperator,
                    /* canChangeStatus: */ isOperator,
                    /* canShowManageImagesButton: */ canShowManageImagesButton,
                    /* canEditImages: */ canEditImages,
                    /* canAddReply: */ !isTicketClosed,
                    /* canDeleteReply: */ isOperator
            );

            this.isClosedNoticeVisible = isTicketClosed;
        }
    }
}
