package com.gsw.service_usuario.dto.auth;

import jakarta.validation.constraints.NotNull;

public class PasswordResetRequestDto {
  @NotNull
  private String novaSenha;
}