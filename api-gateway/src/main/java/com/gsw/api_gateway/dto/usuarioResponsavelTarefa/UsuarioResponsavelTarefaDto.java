package com.gsw.api_gateway.dto.usuarioResponsavelTarefa;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponsavelTarefaDto implements Serializable {
    @NotBlank String id;
    @NotBlank String nome;
    @NotBlank @Email String email;
}