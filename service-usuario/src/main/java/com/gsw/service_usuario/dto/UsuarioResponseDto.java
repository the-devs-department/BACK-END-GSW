package com.gsw.service_usuario.dto;
// import com.gsw.service_usuario.dto.tarefa.TarefaDTO;

import java.time.LocalDateTime;
import java.util.List;

import com.gsw.service_usuario.dto.tarefa.TarefaDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDto {
        private String id;
        private String nome;
        private String email;
        private LocalDateTime dataCadastro;
        private boolean ativo;
        private List<TarefaDto> tarefas;
}