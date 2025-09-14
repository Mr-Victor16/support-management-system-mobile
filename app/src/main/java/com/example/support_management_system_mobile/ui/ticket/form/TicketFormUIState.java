package com.example.support_management_system_mobile.ui.ticket.form;

import androidx.annotation.StringRes;

public abstract class TicketFormUIState {
    public static class Loading extends TicketFormUIState { }
    public static class Submitting extends TicketFormUIState { }
    public static class Success extends TicketFormUIState { }

    public static class Error extends TicketFormUIState {
        @StringRes
        public final int messageTextResId;

        public Error(@StringRes int messageTextResId) {
            this.messageTextResId = messageTextResId;
        }
    }

    public static class Editing extends TicketFormUIState {
        @StringRes
        public final int headerTextResId;

        @StringRes
        public final int saveButtonTextResId;

        public Editing(@StringRes int headerTextResId, @StringRes int saveButtonTextResId) {
            this.headerTextResId = headerTextResId;
            this.saveButtonTextResId = saveButtonTextResId;
        }
    }
}
