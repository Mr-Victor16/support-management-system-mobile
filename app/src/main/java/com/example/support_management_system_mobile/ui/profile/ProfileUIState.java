package com.example.support_management_system_mobile.ui.profile;

import androidx.annotation.StringRes;

public abstract class ProfileUIState {
    public static class Loading extends ProfileUIState {}

    public static class Success extends ProfileUIState {
        public final String username;
        public final String fullName;
        public final String email;

        @StringRes
        public final int roleResId;

        public final boolean isManagementPanelVisible;

        public Success(String username, String fullName, String email, @StringRes int roleResId, boolean isManagementPanelVisible) {
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.roleResId = roleResId;
            this.isManagementPanelVisible = isManagementPanelVisible;
        }
    }
}
