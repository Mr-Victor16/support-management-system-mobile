package com.example.support_management_system_mobile.data.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record Ticket(Long id, String title, String description, List<Image> images,
                     LocalDate createdDate, Category category, Priority priority, Status status,
                     String version, Software software, List<TicketReply> replies, User user
) implements Serializable { }
