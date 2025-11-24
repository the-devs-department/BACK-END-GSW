package com.gsw.api_gateway.dto;

import java.util.List;

public record UsuarioDTO(
        String id,
        String nome,
        String email,
        String senha,
        boolean ativo,
        List<String> roles
) {}