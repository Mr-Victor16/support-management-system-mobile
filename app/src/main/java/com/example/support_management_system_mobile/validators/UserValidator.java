package com.example.support_management_system_mobile.validators;

import android.util.Patterns;

public class UserValidator {
    public static boolean isUsernameValid(String username) {
        return username != null && username.length() >= 2 && username.length() <= 36;
    }

    public static boolean isNameValid(String name) {
        return name != null && name.length() >= 2 && name.length() <= 30 && Character.isUpperCase(name.charAt(0));
    }

    public static boolean isSurnameValid(String surname) {
        return surname != null && surname.length() >= 2 && surname.length() <= 60 && Character.isUpperCase(surname.charAt(0));
    }

    public static boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password != null && !password.isEmpty();
    }
}
