package com.example.support_management_system_mobile.ui.profile;

public abstract class EditProfileResult {
    private EditProfileResult() {}

    public static class Loading extends EditProfileResult {}

    public static class Success extends EditProfileResult {
        private final int messageRes;

        public Success(int messageRes) {
            this.messageRes = messageRes;
        }

        public int getMessageRes() {
            return messageRes;
        }
    }

    public static class Error extends EditProfileResult {
        private final int messageRes;

        public Error(int messageRes) {
            this.messageRes = messageRes;
        }

        public int getMessageRes() {
            return messageRes;
        }
    }
}
