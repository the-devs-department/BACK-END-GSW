package com.gsw.service_usuario.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.List;

public record CriacaoUsuarioDto (
        @NotNull
        String nome,

        @NotNull
        String email,

        @NotNull
        @Length(min = 6, max = 20)
        String senha,

        List<Tarefa> tarefas
        ) {}
