package com.gsw.taskmanager.entity;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter 
@Setter 
@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    private String id;

    private String nome;

    private String email;

    private String senha;

    private LocalDateTime dataCadastro;
    private boolean ativo;

    private List<Tarefa> tarefas = new ArrayList<>();

    private List<String> roles = new ArrayList<>();

    public void setRoles(List<String> roles) {
        this.roles.addAll(roles);
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getId() {
        return id;
    }

    public @NotNull String getNome() {
        return nome;
    }

    public void setNome(@NotNull String nome) {
        this.nome = nome;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public @NotNull @Length(min = 6, max = 20) String getSenha() {
        return senha;
    }

    public void setSenha(@NotNull @Length(min = 6, max = 20) String senha) {
        this.senha = senha;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }


}