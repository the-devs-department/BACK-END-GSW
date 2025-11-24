package com.gsw.service_equipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponsavelTarefaDto {
    @NotNull
    public String id;

    @NotBlank
    public String nome;

    @NotBlank
    public String descricao;
}
