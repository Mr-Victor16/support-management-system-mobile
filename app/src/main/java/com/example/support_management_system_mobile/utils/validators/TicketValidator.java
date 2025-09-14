package com.example.support_management_system_mobile.utils.validators;

public class TicketValidator {
    public static boolean isTicketReplyValid(String reply) {
        return reply != null && reply.length() >= 5 && reply.length() <= 500;
    }

    public static boolean isTicketTitleValid(String title) {
        return title != null && title.length() >= 5 && title.length() <= 100;
    }

    public static boolean isTicketDescriptionValid(String description) {
        return description != null && description.length() >= 5 && description.length() <= 500;
    }

    public static boolean isTicketVersionValid(String version) {
        return version != null && !version.isEmpty() && version.length() <= 10;
    }
}
