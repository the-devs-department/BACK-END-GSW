package com.gsw.taskmanager.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank String novaSenha) {
}
