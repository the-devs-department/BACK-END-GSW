package com.gsw.service_usuario.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank String novaSenha) {
}
