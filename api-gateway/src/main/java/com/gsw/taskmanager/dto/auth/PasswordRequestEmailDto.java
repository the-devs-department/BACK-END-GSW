package com.gsw.taskmanager.dto.auth;

import jakarta.validation.constraints.NotNull;

public record PasswordRequestEmailDto(@NotNull String email) {
}
