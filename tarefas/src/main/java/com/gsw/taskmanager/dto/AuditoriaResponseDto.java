package com.gsw.taskmanager.dto;

import java.time.LocalDateTime;

public record AuditoriaResponseDto(String nomeResponsavel, String modificacao, LocalDateTime dataAlteracao) {
}
