package com.example.support_management_system_mobile.data.models;

import java.io.Serializable;
import java.time.LocalDate;

public record TicketReply(
        Long id,
        User user,
        String content,
        LocalDate createdDate
) implements Serializable { }
