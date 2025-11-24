package com.gsw.service_tarefa.dto.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogRequest {
    private String usuarioNome;
    private String usuarioEmail;
    private String tarefaId;
    private String categoria;
    private String acao;
}
