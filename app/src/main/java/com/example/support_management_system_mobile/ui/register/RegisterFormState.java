package com.example.support_management_system_mobile.ui.register;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class RegisterFormState {
    @Nullable @StringRes
    private final Integer usernameError;
    @Nullable @StringRes
    private final Integer nameError;
    @Nullable @StringRes
    private final Integer surnameError;
    @Nullable @StringRes
    private final Integer emailError;
    @Nullable @StringRes
    private final Integer passwordError;

    private final boolean isDataValid;

    public RegisterFormState(@Nullable Integer usernameError, @Nullable Integer nameError, @Nullable Integer surnameError,
                             @Nullable Integer emailError, @Nullable Integer passwordError, boolean isDataValid) {
        this.usernameError = usernameError;
        this.nameError = nameError;
        this.surnameError = surnameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getUsernameError() { return usernameError; }
    @Nullable
    public Integer getNameError() { return nameError; }
    @Nullable
    public Integer getSurnameError() { return surnameError; }
    @Nullable
    public Integer getEmailError() { return emailError; }
    @Nullable
    public Integer getPasswordError() { return passwordError; }

    public boolean isDataValid() { return isDataValid; }
}
