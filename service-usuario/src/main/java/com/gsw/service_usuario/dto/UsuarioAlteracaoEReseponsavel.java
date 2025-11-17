package com.gsw.service_usuario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAlteracaoEReseponsavel {
        @NotNull
        private String id;
        private String nome;
        private String email;
        
}