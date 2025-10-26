package com.gsw.taskmanager.dto.auth;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(@NotNull String email,@NotNull String senha) {
}
