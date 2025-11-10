package com.gsw.service_tarefa.dto.usuario;

/**
 * DTO utilizado para atribuir um usuário a uma tarefa.
 * Recebe o ID do usuário que será atribuído.
 */
public record AtribuicaoRequestDto(String usuarioId) {
}
