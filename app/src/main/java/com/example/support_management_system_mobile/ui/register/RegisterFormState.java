package com.example.support_management_system_mobile.ui.register;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.validators.UserValidator;

public record RegisterFormState(
        @Nullable @StringRes Integer usernameError,
        @Nullable @StringRes Integer nameError,
        @Nullable @StringRes Integer surnameError,
        @Nullable @StringRes Integer emailError,
        @Nullable @StringRes Integer passwordError,
        boolean isDataValid
) {
    public static RegisterFormState create(String username, String name, String surname, String email, String password) {
        Integer usernameError = (!username.isEmpty() && !UserValidator.isUsernameValid(username))
                ? R.string.username_register_error : null;

        Integer nameError = (!name.isEmpty() && !UserValidator.isNameValid(name))
                ? R.string.name_register_error : null;

        Integer surnameError = (!surname.isEmpty() && !UserValidator.isSurnameValid(surname))
                ? R.string.surname_register_error : null;

        Integer emailError = (!email.isEmpty() && !UserValidator.isEmailValid(email))
                ? R.string.email_register_error : null;

        Integer passwordError = (!password.isEmpty() && !UserValidator.isPasswordValid(password))
                ? R.string.password_register_error : null;

        boolean isDataValidForSubmission = UserValidator.isUsernameValid(username) &&
                UserValidator.isNameValid(name) &&
                UserValidator.isSurnameValid(surname) &&
                UserValidator.isEmailValid(email) &&
                UserValidator.isPasswordValid(password);

        return new RegisterFormState(usernameError, nameError, surnameError, emailError, passwordError, isDataValidForSubmission);
    }
}
