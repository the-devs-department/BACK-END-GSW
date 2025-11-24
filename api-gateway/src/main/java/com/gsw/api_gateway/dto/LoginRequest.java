package com.gsw.api_gateway.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(String email, @NotNull String senha) {}
