package com.example.support_management_system_mobile.validators;

public class KnowledgeValidator {
    public static boolean isTitleValid(String content) {
        return content != null && content.length() >= 2 && content.length() <= 50;
    }

    public static boolean isContentValid(String content) {
        return content != null && content.length() >= 20 && content.length() <= 360;
    }
}
