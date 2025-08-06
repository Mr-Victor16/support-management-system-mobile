package com.example.support_management_system_mobile.ui.register;

public abstract class RegisterResult {
    private RegisterResult() {}

    public static final class Success extends RegisterResult {}

    public static final class Error extends RegisterResult {
        private final int messageRes;
        public Error(int messageRes) { this.messageRes = messageRes; }
        public int getMessageRes() { return messageRes; }
    }
}
