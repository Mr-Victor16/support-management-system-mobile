package com.example.support_management_system_mobile.payload.request.update;

public record UpdatePriorityRequest (
        Long priorityID,
        String name
) { }
