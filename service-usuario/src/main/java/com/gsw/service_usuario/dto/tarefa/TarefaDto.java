package com.gsw.service_usuario.dto.tarefa;

import java.io.ObjectInputFilter.Status;

import com.gsw.service_usuario.dto.UsuarioAlteracaoEReseponsavel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TarefaDto {
    private String id;
    private Status status;
    private String titulo;
    private UsuarioAlteracaoEReseponsavel responsavel;
    private String dataEntrega;
    private String dataCriacao;
    private Boolean ativo;

}