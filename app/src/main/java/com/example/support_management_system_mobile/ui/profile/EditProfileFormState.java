package com.example.support_management_system_mobile.ui.profile;

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

    public EditProfileFormState(@Nullable @StringRes Integer firstNameError,
                         @Nullable @StringRes Integer surnameError,
                         @Nullable @StringRes Integer passwordError) {
        this.firstNameError = firstNameError;
        this.surnameError = surnameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    public EditProfileFormState(boolean isDataValid) {
        this.firstNameError = null;
        this.surnameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
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
}
