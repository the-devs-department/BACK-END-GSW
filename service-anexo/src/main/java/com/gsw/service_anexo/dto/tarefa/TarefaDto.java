package com.gsw.service_anexo.dto.tarefa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gsw.service_anexo.dto.AnexoDto;
import com.gsw.service_anexo.dto.usuario.UsuarioResponsavelTarefaDto;
import com.gsw.service_anexo.enums.Status;

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
