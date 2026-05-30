package com.kingspan.challenge.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Authresponse {
    private String token;
    private String name;
    private String email;
    private String role;
}
