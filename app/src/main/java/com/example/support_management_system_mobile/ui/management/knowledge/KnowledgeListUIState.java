package com.example.support_management_system_mobile.ui.management.knowledge;

import com.example.support_management_system_mobile.models.Knowledge;

import java.util.List;

public abstract class KnowledgeListUIState {
    private KnowledgeListUIState() {    }

    public static class Loading extends KnowledgeListUIState {  }

    public static class Success extends KnowledgeListUIState {
        public final List<Knowledge> knowledgeItems;
        public final boolean canManage;

        public Success(List<Knowledge> items, boolean canManage) {
            this.knowledgeItems = items;
            this.canManage = canManage;
        }
    }

    public static class Error extends KnowledgeListUIState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
