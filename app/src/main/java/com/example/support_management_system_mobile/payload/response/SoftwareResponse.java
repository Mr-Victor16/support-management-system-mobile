package com.example.support_management_system_mobile.payload.response;

public record SoftwareResponse(
        Long softwareID,
        String name,
        String description,
        Long useNumberTicket,
        Long useNumberKnowledge
) { }
