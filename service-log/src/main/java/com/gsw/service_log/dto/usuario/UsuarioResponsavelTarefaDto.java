package com.gsw.service_log.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponsavelTarefaDto {
    private String id;
    private String nome;
    private String email;
}
