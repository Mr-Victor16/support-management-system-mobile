package com.example.support_management_system_mobile.ui.knowledge;

import com.example.support_management_system_mobile.models.Knowledge;

import java.util.Objects;

public class KnowledgeUIModel {
    private final Knowledge knowledge;
    private final boolean isExpanded;

    public KnowledgeUIModel(Knowledge knowledge, boolean isExpanded) {
        this.knowledge = knowledge;
        this.isExpanded = isExpanded;
    }

    public Knowledge getKnowledge() {
        return knowledge;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KnowledgeUIModel that = (KnowledgeUIModel) o;

        return isExpanded == that.isExpanded &&
                Objects.equals(knowledge, that.knowledge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(knowledge, isExpanded);
    }
}
