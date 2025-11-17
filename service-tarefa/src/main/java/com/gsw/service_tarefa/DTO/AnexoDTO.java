package com.gsw.service_tarefa.dto;

import com.gsw.service_tarefa.enums.TipoAnexo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnexoDTO {
    private String nome;
    private TipoAnexo tipo;
}
