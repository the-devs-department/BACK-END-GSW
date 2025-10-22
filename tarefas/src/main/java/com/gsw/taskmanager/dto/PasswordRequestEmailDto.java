package com.gsw.taskmanager.dto;

import jakarta.validation.constraints.NotNull;

public record PasswordRequestEmailDto(@NotNull String email) {
}
