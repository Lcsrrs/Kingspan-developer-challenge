package com.kingspan.challenge.auth.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;

    private String password;
}
