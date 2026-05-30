package com.kingspan.challenge.requests.dto;

import com.kingspan.challenge.requests.RequestStatus;
import com.kingspan.challenge.users.ApproverLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RequestResponseDTO(
        UUID id,
        String title,
        String description,
        BigDecimal amount,
        String category,
        RequestStatus status,
        ApproverLevel approverLevel,
        String requestorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
