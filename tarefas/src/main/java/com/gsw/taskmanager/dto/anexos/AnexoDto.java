package com.gsw.taskmanager.dto.anexos;

import com.gsw.taskmanager.enums.TipoAnexo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnexoDto {
    
    @Size(min =  1, max = 255, message = "O nome do anexo deve ter no máximo 255 caracteres")
    private String nome;

    @NotNull(message = "O campo do tipo do anexo não pode estar vazio")
    private TipoAnexo tipo;
}