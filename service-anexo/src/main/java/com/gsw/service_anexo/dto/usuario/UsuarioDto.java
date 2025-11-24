package com.gsw.service_anexo.dto.usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gsw.service_anexo.dto.tarefa.TarefaDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDto {
    private String id;

    private String nome;

    private String email;

    private String senha;

    private LocalDateTime dataCadastro;
    private boolean ativo;

    private List<TarefaDto> tarefas = new ArrayList<>();

    private List<String> roles = new ArrayList<>();
    
}
