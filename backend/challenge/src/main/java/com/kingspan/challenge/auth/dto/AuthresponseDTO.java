package com.kingspan.challenge.auth.dto;

import com.kingspan.challenge.users.UserRole;

public record AuthresponseDTO(String token, String name, String email, UserRole role) {
}
