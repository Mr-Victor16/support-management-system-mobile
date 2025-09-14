package com.example.support_management_system_mobile.ui.profile.edit;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class EditProfileFormState {
    @Nullable
    @StringRes
    private final Integer firstNameError;

    @Nullable
    @StringRes
    private final Integer surnameError;

    @Nullable
    @StringRes
    private final Integer passwordError;

    private final boolean isDataValid;
    private final boolean hasChanges;

    public EditProfileFormState(@Nullable @StringRes Integer firstNameError,
                                @Nullable @StringRes Integer surnameError,
                                @Nullable @StringRes Integer passwordError) {
        this.firstNameError = firstNameError;
        this.surnameError = surnameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
        this.hasChanges = false;
    }

    public EditProfileFormState(boolean isDataValid, boolean hasChanges) {
        this.firstNameError = null;
        this.surnameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
        this.hasChanges = hasChanges;
    }

    @Nullable
    @StringRes
    public Integer getFirstNameError() {
        return firstNameError;
    }

    @Nullable
    @StringRes
    public Integer getSurnameError() {
        return surnameError;
    }

    @Nullable
    @StringRes
    public Integer getPasswordError() {
        return passwordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }

    public boolean hasChanges() {
        return hasChanges;
    }
}
