package com.gsw.service_equipe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListaUsuariosDto {
    @NotBlank
    private String email;

    @NotBlank
    private String nome;

    @NotBlank
    private List<String> role;
}
