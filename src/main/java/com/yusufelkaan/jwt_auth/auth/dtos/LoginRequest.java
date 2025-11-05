package com.yusufelkaan.jwt_auth.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide valid email!")
        String email,
        @NotBlank(message = "Password is required") String password) {
}
