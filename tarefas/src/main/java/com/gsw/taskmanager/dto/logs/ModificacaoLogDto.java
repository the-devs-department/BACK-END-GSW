package com.gsw.taskmanager.dto.logs;

import com.gsw.taskmanager.enums.CategoriaModificacao;

public record ModificacaoLogDto(CategoriaModificacao categoria, String modificacao) {
}