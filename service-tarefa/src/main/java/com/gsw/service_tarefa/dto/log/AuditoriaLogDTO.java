package com.gsw.service_tarefa.dto.log;

import java.time.LocalDateTime;
import java.util.List;

import com.gsw.service_tarefa.dto.ResponsavelAlteracaoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaLogDTO {
  private String id;
  private LocalDateTime criadoEm;
  private String tarefaId;
  private ResponsavelAlteracaoDTO responsavel;
  private List<ModificacaoLogDTO> modificacoes;
}
