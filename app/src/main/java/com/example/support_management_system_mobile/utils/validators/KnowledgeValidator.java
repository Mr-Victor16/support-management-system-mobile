package com.example.support_management_system_mobile.utils.validators;

public class KnowledgeValidator {
    public static boolean isTitleValid(String title) {
        return title != null && title.length() >= 2 && title.length() <= 50;
    }

    public static boolean isContentValid(String content) {
        return content != null && content.length() >= 20 && content.length() <= 360;
    }
}
