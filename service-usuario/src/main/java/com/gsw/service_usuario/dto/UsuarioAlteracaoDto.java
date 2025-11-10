package com.gsw.taskmanager.dto.usuario;

import jakarta.validation.constraints.NotNull;

public record UsuarioAlteracaoDto(@NotNull String id, String nome, String email) {
}
