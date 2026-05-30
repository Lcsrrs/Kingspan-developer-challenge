package com.kingspan.challenge.history.dto;

import com.kingspan.challenge.requests.RequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoryResponseDTO(
        UUID id,
        String actorName,
        String actorRole,
        RequestStatus fromStatus,
        RequestStatus toStatus,
        String comment,
        LocalDateTime createdAt
) {
}
