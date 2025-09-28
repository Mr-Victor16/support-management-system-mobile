package com.example.support_management_system_mobile.data.models;

import java.io.Serializable;

public record Status(
        Long id,
        String name,
        boolean closeTicket
) implements Serializable { }
