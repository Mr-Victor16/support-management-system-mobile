package com.example.support_management_system_mobile.ui.management.knowledge;

import androidx.annotation.StringRes;

public abstract class KnowledgeFormUIState {
    private KnowledgeFormUIState() {    }

    public static class Loading extends KnowledgeFormUIState {  }
    public static class Submitting extends KnowledgeFormUIState {   }
    public static class Success extends KnowledgeFormUIState {  }

    public static class Error extends KnowledgeFormUIState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }

    public static class Editing extends KnowledgeFormUIState {
        @StringRes
        public final int saveButtonTextResId;

        public Editing(@StringRes int saveButtonTextResId) {
            this.saveButtonTextResId = saveButtonTextResId;
        }
    }
}
