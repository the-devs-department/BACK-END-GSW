package com.gsw.service_tarefa.dto;

import com.gsw.service_tarefa.entity.Tarefa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private LocalDateTime dataCadastro;
    private boolean ativo;
    private List<Tarefa> tarefas;
    private List<String> user_roles;
}
