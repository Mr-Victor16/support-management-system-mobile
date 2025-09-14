package com.example.support_management_system_mobile.ui.ticket.form;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class TicketFormValidationState {
    @Nullable
    @StringRes
    public final Integer titleError;

    @Nullable
    @StringRes
    public final Integer descriptionError;

    @Nullable
    @StringRes
    public final Integer versionError;

    public final boolean isSaveButtonEnabled;

    public TicketFormValidationState(@Nullable @StringRes Integer titleError, @Nullable @StringRes Integer descriptionError,
                                     @Nullable @StringRes Integer versionError, boolean isSaveButtonEnabled) {
        this.titleError = titleError;
        this.descriptionError = descriptionError;
        this.versionError = versionError;
        this.isSaveButtonEnabled = isSaveButtonEnabled;
    }
}
