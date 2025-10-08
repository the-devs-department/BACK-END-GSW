package com.gsw.taskmanager.entity;

import com.gsw.taskmanager.enums.TipoAnexo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class Anexo {

    @Id
    private String id;

    @NotNull
    private String tarefaId;

    @NotNull
    private String usuarioId;

    @Length(min =  1, max = 255, message = "O nome do anexo deve ter no máximo 255 caracteres")
    private String nome;

    @NotNull
    private TipoAnexo tipo;

    @NotNull
    private String url;

    @NotNull
    private LocalDateTime dataUpload;

    @NotNull
    private Long tamanho;
}
