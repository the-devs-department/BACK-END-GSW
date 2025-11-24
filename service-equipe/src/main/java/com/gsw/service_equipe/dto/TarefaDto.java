package com.gsw.service_equipe.dto;

import com.gsw.service_equipe.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TarefaDto {
    public String id;
    public Status status;
    public String titulo;
    public String descricao;
    public UsuarioResponsavelTarefaDto responsavel;
    public String dataEntrega;
    public String tema;
    public LocalDateTime dataCriacao;
    public boolean ativo;

}
