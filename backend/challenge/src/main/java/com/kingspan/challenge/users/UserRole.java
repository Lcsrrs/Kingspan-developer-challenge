package com.kingspan.challenge.users;

public enum UserRole {
    SOLICITANTE ("solicitante"),
    APROVADOR ("aprovador"),
    ADMIN ("admin");

    private String role;


    UserRole(String role){
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
