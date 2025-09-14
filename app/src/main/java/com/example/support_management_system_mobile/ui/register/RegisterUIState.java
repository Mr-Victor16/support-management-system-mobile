package com.example.support_management_system_mobile.ui.register;

public abstract class RegisterUIState {
    private RegisterUIState() {}

    public static final class Success extends RegisterUIState {}

    public static final class Error extends RegisterUIState {
        private final int messageRes;

        public Error(int messageRes) {
            this.messageRes = messageRes;
        }

        public int getMessageRes() {
            return messageRes;
        }
    }
}
