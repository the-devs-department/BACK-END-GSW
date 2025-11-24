package com.gsw.service_log.dto.logs;

import com.gsw.service_log.dto.tarefa.TarefaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaResponseDto {

    private TarefaDto tarefa;
    private ResponsavelAlteracaoDto responsavel;
    private ModificacaoLogDto modificacao;
    private String dataAlteracao;
    private String horaAlteracao;
}
