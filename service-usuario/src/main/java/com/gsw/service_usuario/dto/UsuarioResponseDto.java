package com.gsw.taskmanager.dto.usuario;

import com.gsw.taskmanager.entity.Tarefa;

import java.time.LocalDateTime;
import java.util.List;

public record UsuarioResponseDto(
        String nome,
        String email,
        LocalDateTime dateCadastro,
        boolean ativo,
        List<Tarefa> tarefas
) {}
