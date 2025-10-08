package com.gsw.taskmanager.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(@NotNull String email,@NotNull String senha) {
}
