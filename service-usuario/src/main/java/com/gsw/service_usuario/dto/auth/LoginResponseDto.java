package com.gsw.service_usuario.dto.auth;

import java.util.List;

public record LoginResponseDto(
     String nome,
     String email,
     List<String>roles
) {}