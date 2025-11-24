package com.gsw.service_log.dto.logs;

import com.gsw.service_log.enums.CategoriaModificacao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// public record ModificacaoLogDto(CategoriaModificacao categoria, String modificacao) {
// }


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModificacaoLogDto {
   private CategoriaModificacao categoria;
   private String modificacao;
}