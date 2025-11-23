package com.gsw.service_equipe.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.gsw.service_equipe.dto.TarefaDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDto {
    @NotNull
    private String id;

    @NotBlank
    private String nome;

    @NotBlank
    private String email;

    @NotBlank
    private LocalDateTime dataCadastro;

    @NotBlank
    private boolean ativo;

    @NotBlank
    private List<TarefaDto> tarefas;
}