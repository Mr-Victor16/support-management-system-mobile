package com.example.support_management_system_mobile.validators;

public class TicketValidator {
    public static boolean isTicketReplyValid(String content) {
        return content != null && content.length() >= 5 && content.length() <= 500;
    }

    public static boolean isTicketTitleValid(String content) {
        return content != null && content.length() >= 5 && content.length() <= 100;
    }

    public static boolean isTicketDescriptionValid(String content) {
        return content != null && content.length() >= 5 && content.length() <= 500;
    }

    public static boolean isTicketVersionValid(String content) {
        return content != null && content.length() >= 1 && content.length() <= 10;
    }
}
