package com.gsw.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank String novaSenha) {
}
