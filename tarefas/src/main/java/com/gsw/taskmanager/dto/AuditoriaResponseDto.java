package com.gsw.taskmanager.dto;

import com.gsw.taskmanager.entity.Tarefa;

public record AuditoriaResponseDto(Tarefa tarefa, ResponsavelAlteracaoDto responsavel, ModificacaoLogDto modificacao, String dataAlteracao, String horaAlteracao) {
}
