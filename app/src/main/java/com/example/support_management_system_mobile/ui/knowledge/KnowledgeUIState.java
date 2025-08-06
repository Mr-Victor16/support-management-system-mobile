package com.example.support_management_system_mobile.ui.knowledge;

import java.util.List;

public interface KnowledgeUIState {

    class Loading implements KnowledgeUIState {
    }

    class Success implements KnowledgeUIState {
        public final List<KnowledgeUIModel> items;

        public Success(List<KnowledgeUIModel> items) {
            this.items = items;
        }
    }

    class Empty implements KnowledgeUIState {
        public final String message;

        public Empty(String message) {
            this.message = message;
        }
    }
}
