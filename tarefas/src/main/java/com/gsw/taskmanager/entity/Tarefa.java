package com.gsw.taskmanager.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "tasks")
@Getter
@Setter
public class Tarefa {

    @Id
    private String id;

    @NotNull
    private String titulo;

    @NotNull
    private String descricao;

    private String responsavel;

    @NotNull
    private String dataEntrega;

    @NotNull
    private String tema;

    private LocalDateTime dataCriacao; // quando foi criada

    private boolean ativo; // controle de soft delete
}
