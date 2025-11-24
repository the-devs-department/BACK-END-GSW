package com.gsw.service_log.dto;

import com.gsw.service_log.dto.tarefa.TarefaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtribuicaoDTO {
    public String tarefaAntigaId;
    public String usuarioAntigo;
    public String usuarioNovo;
}
