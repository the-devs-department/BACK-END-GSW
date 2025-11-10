package com.gsw.service_usuario.dto;
import java.time.LocalDateTime;
import java.util.List;

public record UsuarioResponseDto(
        String nome,
        String email,
        LocalDateTime dateCadastro,
        boolean ativo,
        List<Tarefa> tarefas
) {}
