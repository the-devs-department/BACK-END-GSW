package com.gsw.service_log.dto.anexo;

import java.time.LocalDateTime;

import com.gsw.service_log.enums.TipoAnexo;
import com.gsw.service_log.dto.tarefa.TarefaDto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AnexoDto {
    
    private String id;

    @Size(min =  1, max = 255, message = "O nome do anexo deve ter no máximo 255 caracteres")
    private String nome;

    @NotNull(message = "O campo do tipo do anexo não pode estar vazio")
    private TipoAnexo tipo;
    
}
