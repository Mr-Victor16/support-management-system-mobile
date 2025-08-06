package com.example.support_management_system_mobile.ui.welcome;

public interface WelcomeUIState {
    class Loading implements WelcomeUIState {}

    class Success implements WelcomeUIState {
        private final String welcomeMessage;
        private final boolean isLoginButtonVisible;

        public Success(String welcomeMessage, boolean isLoginButtonVisible) {
            this.welcomeMessage = welcomeMessage;
            this.isLoginButtonVisible = isLoginButtonVisible;
        }

        public String getWelcomeMessage() {
            return welcomeMessage;
        }

        public boolean isLoginButtonVisible() {
            return isLoginButtonVisible;
        }
    }
}
