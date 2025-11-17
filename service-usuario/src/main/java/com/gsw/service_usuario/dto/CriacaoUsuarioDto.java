package com.gsw.service_usuario.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import com.gsw.service_usuario.dto.tarefa.TarefaDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriacaoUsuarioDto {
        @NotNull
        private String nome;
        @NotNull
        private String email;
        @NotNull
        @Length(min = 6, max = 20)
        private String senha;
        private List<TarefaDto> tarefas;
}