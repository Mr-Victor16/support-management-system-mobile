package com.example.support_management_system_mobile.ui.login;

import com.example.support_management_system_mobile.data.payload.response.LoginResponse;

public abstract class LoginUIState {
    private LoginUIState() {}

    public static final class Success extends LoginUIState {
        private final LoginResponse data;

        public Success(LoginResponse data) {
            this.data = data;
        }

        public LoginResponse getData() {
            return data;
        }
    }

    public static final class Error extends LoginUIState {
        private final int messageRes;

        public Error(int messageRes) {
            this.messageRes = messageRes;
        }

        public int getMessageRes() {
            return messageRes;
        }
    }
}
