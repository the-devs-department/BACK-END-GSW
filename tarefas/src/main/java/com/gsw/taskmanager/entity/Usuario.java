package com.gsw.taskmanager.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "users")
@Getter
@Setter
public class Usuario {

    @Id
    private String id;
    private String nome;
    private String email;
    private String senha;
    private LocalDateTime dataCadastro;
    private boolean ativo;
    private List<Tarefa> tarefas = new ArrayList<>();
}