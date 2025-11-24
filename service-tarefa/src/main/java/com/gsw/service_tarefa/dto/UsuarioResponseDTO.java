package com.gsw.service_tarefa.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.gsw.service_tarefa.entity.Tarefa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {
        private String id;
        private String nome;
        private String email;
        private LocalDateTime dataCadastro;
        private boolean ativo;
        private List<Tarefa> tarefas;
}