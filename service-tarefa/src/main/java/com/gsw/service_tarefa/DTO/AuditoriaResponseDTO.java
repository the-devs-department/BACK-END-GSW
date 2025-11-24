package com.gsw.service_tarefa.dto;

import com.gsw.service_tarefa.entity.Tarefa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaResponseDTO {
    private Tarefa tarefa;
    private ResponsavelAlteracaoDTO responsavel;
    private ModificacaoLogDTO modificacao;
    private String dataAlteracao;
    private String horaAlteracao;
}
