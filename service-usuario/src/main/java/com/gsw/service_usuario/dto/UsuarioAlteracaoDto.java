package com.gsw.service_usuario.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioAlteracaoDto(@NotNull String id, String nome, String email) {
}
