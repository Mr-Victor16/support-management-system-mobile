package com.example.support_management_system_mobile.ui.software;

import java.util.List;

public interface SoftwareUIState {
    class Loading implements SoftwareUIState {}

    class Success implements SoftwareUIState {
        public final List<SoftwareUIModel> items;

        public Success(List<SoftwareUIModel> items) {
            this.items = items;
        }
    }

    class Empty implements SoftwareUIState {
        public final String message;

        public Empty(String message) {
            this.message = message;
        }
    }
}
