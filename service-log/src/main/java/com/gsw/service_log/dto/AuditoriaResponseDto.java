package com.gsw.taskmanager.dto.logs;

import com.gsw.service_tarefa.entity.Tarefa;

public record AuditoriaResponseDto(Tarefa tarefa, ResponsavelAlteracaoDto responsavel, ModificacaoLogDto modificacao, String dataAlteracao, String horaAlteracao) {
}
