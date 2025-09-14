package com.example.support_management_system_mobile.ui.software;

import java.util.List;

public abstract class SoftwareUIState {
    public static class Loading extends SoftwareUIState {}

    public static class Success extends SoftwareUIState {
        public final List<SoftwareUIModel> items;

        public Success(List<SoftwareUIModel> items) {
            this.items = items;
        }
    }

    public static class Error extends SoftwareUIState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
