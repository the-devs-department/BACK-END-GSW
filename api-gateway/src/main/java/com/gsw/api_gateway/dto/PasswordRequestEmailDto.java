package com.gsw.api_gateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordRequestEmailDto(
    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    String email
) {}
