package com.gsw.service_usuario.dto;

import java.io.Serializable;

public record UsuarioResponsavelTarefaDto(String id, String nome, String email) implements Serializable {
}