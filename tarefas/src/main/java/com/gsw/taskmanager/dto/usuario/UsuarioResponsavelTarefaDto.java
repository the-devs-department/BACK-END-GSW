package com.gsw.taskmanager.dto.usuario;

import java.io.Serializable;

public record UsuarioResponsavelTarefaDto(String id, String nome, String email) implements Serializable {
}