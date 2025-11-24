package com.gsw.service_tarefa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponsavelDTO {
    private String id;
    private String nome;
    private String email;
}