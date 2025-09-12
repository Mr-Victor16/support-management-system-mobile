package com.example.support_management_system_mobile.ui.management.user;

import androidx.annotation.StringRes;

public abstract class UserFormUIState {
    private UserFormUIState() {    }

    public static class Loading extends UserFormUIState {  }
    public static class Submitting extends UserFormUIState {   }
    public static class Success extends UserFormUIState {  }

    public static class Error extends UserFormUIState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }

    public static class Editing extends UserFormUIState {
        @StringRes
        public final int headerTextResId;

        @StringRes
        public final int saveButtonTextResId;

        public Editing(@StringRes int headerTextResId, @StringRes int saveButtonTextResId) {
            this.headerTextResId = headerTextResId;
            this.saveButtonTextResId = saveButtonTextResId;
        }
    }
}
