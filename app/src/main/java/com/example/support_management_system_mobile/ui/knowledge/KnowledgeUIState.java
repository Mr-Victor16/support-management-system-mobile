package com.example.support_management_system_mobile.ui.knowledge;

import java.util.List;

public abstract class KnowledgeUIState {
    public static class Loading extends KnowledgeUIState { }

    public static class Success extends KnowledgeUIState {
        public final List<KnowledgeUIModel> items;

        public Success(List<KnowledgeUIModel> items) {
            this.items = items;
        }
    }

    public static class Empty extends KnowledgeUIState {
        public final String message;

        public Empty(String message) {
            this.message = message;
        }
    }
}
