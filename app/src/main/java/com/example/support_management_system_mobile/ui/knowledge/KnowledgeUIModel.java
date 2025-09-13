package com.example.support_management_system_mobile.ui.knowledge;

import com.example.support_management_system_mobile.models.Knowledge;

public record KnowledgeUIModel(
        Knowledge knowledge,
        boolean isExpanded
) { }
