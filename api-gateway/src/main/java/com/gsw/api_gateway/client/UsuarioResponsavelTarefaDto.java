package com.gsw.api_gateway.client;

/**
 * DTO para usuário responsável por tarefa
 */
public record UsuarioResponsavelTarefaDto(
    String email,
    String nome,
    String id
) {}

