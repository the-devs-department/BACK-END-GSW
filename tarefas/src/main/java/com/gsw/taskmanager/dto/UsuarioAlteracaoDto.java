package com.gsw.taskmanager.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioAlteracaoDto(@NotNull String id, String nome, String email) {
}
