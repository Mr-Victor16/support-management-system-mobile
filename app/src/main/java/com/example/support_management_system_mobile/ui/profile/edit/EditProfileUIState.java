package com.example.support_management_system_mobile.ui.profile.edit;

public abstract class EditProfileUIState {
    private EditProfileUIState() {}

    public static class Loading extends EditProfileUIState {}

    public static class Success extends EditProfileUIState {
        private final int messageRes;

        public Success(int messageRes) {
            this.messageRes = messageRes;
        }

        public int getMessageRes() {
            return messageRes;
        }
    }

    public static class Error extends EditProfileUIState {
        private final int messageRes;

        public Error(int messageRes) {
            this.messageRes = messageRes;
        }

        public int getMessageRes() {
            return messageRes;
        }
    }
}
