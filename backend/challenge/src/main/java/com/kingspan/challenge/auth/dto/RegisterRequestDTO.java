package com.kingspan.challenge.auth.dto;

import com.kingspan.challenge.users.ApproverLevel;
import com.kingspan.challenge.users.UserRole;

public record RegisterRequestDTO(String name, String email, String password, UserRole role, ApproverLevel approverLevel) {

}
