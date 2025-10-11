package com.gsw.taskmanager.dto;

public record AuditoriaResponseDto(ResponsavelAlteracaoDto responsavel, ModificacaoLogDto modificacao, String dataAlteracao, String horaAlteracao) {
}
