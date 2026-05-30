package com.kingspan.challenge.auth.dto;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        String role,
        String approverLevel
) {
}
