package com.gsw.service_usuario.dto.auth;

import jakarta.validation.constraints.NotNull;

public class PasswordRequestEmailDto {
  @NotNull
  private String email;
}