package com.gsw.service_tarefa.dto.log;

import com.gsw.service_tarefa.enums.CategoriaModificacao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModificacaoLogDTO {
   private CategoriaModificacao categoria;
   private String modificacao;
}