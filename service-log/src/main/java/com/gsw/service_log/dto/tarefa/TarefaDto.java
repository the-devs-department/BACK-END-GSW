package com.gsw.service_log.dto.tarefa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.gsw.service_log.dto.anexo.AnexoDto;
import com.gsw.service_log.dto.usuario.UsuarioResponsavelTarefaDto;
import com.gsw.service_log.enums.Status;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TarefaDto {

    private String id;

    private Status status;

    private String titulo;

    private String descricao;

    private UsuarioResponsavelTarefaDto responsavel;

    private String dataEntrega;

    private String tema;

    private LocalDateTime dataCriacao; 

    private boolean ativo;

    private List<AnexoDto> anexos = new ArrayList<>();
    
}
