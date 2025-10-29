package com.gsw.taskmanager.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.gsw.taskmanager.dto.usuario.UsuarioResponsavelTarefaDto;
import com.gsw.taskmanager.enums.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "tasks")
@Getter
@Setter
public class Tarefa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    private Status status;

    @NotNull
    private String titulo;

    @NotNull
    private String descricao;

    private UsuarioResponsavelTarefaDto responsavel;

    @NotNull
    private String dataEntrega;

    @NotNull
    private String tema;

    private LocalDateTime dataCriacao; 

    private boolean ativo;

    // ANEXOS:
    private List<Anexo> anexos = new ArrayList<>();

}
