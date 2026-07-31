package com.mariosilva.task_manager.enums;

public enum UserRole {
    ADMIN("admin"),
    USER("user");

    private String role; 

    UserRole(String role) {
        this.role = role;
    }

    String getRole() {
        return this.role;
    }

}
