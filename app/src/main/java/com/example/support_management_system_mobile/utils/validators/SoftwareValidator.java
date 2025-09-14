package com.example.support_management_system_mobile.utils.validators;

public class SoftwareValidator {
    public static boolean isNameValid(String name) {
        return name != null && name.length() >= 2 && name.length() <= 30;
    }

    public static boolean isDescriptionValid(String description) {
        return description != null && description.length() >= 10 && description.length() <= 200;
    }
}
