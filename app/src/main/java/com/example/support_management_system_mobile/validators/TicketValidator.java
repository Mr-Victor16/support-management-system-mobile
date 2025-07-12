package com.example.support_management_system_mobile.validators;

public class TicketValidator {
    public static boolean isTicketReplyValid(String content) {
        return content != null && content.length() >= 5 && content.length() <= 500;
    }
}
