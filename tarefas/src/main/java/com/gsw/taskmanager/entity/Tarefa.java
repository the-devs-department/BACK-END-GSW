package com.gsw.taskmanager.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "tasks")
@Getter
@Setter
public class Tarefa {

    @Id
    private String id;
    private String titulo;
    private String descricao;
    private String responsavel;
    private String dataEntrega;
    private String tema;
    private List<Tarefa> tarefas = new ArrayList<>();

}

