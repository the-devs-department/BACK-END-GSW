package com.gsw.taskmanager.dto;

import com.gsw.taskmanager.enums.CategoriaModificacao;

public record ModificacaoLogDto(CategoriaModificacao categoria, String modificacao) {
}