package com.gsw.service_usuario.dto.auth;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(@NotNull String email,@NotNull String senha) {
}
