package com.kingspan.challenge.auth.dto;

import com.kingspan.challenge.users.ApproverLevel;
import com.kingspan.challenge.users.UserRole;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class RegisterRequest {

    private String name;

    @Email(message = "Email inválido")
    private String email;

    private String password;

    private UserRole role;

    private ApproverLevel approverLevel;
}
