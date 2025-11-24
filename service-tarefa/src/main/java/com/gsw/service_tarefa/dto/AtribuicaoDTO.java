package com.gsw.service_tarefa.dto;

import com.gsw.service_tarefa.entity.Tarefa;
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
