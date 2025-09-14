package com.example.support_management_system_mobile.ui.software;

import com.example.support_management_system_mobile.models.Software;

public record SoftwareUIModel(
        Software software,
        boolean isExpanded
) { }
