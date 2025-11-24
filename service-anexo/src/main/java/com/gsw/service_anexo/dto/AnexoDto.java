package com.gsw.service_anexo.dto;

import java.time.LocalDateTime;

import com.gsw.service_anexo.enums.TipoAnexo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnexoDto {
    private String id;

    private String tarefaId;

    private String usuarioId;

    @Size(min =  1, max = 255, message = "O nome do anexo deve ter no máximo 255 caracteres")
    private String nome;

    @NotNull(message = "O campo do tipo do anexo não pode estar vazio")
    private TipoAnexo tipo;

    private String url;

    private LocalDateTime dataUpload;

    private Long tamanho;
}