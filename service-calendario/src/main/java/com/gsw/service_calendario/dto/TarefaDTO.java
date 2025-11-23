package com.gsw.service_calendario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"tarefaId", "tarefaTitulo", "dataEntrega"})
public class TarefaDTO {

    @NotBlank
    @JsonProperty("tarefaId")
    @JsonAlias("id")
    private String tarefaId;

    @NotBlank
    @JsonProperty("tarefaTitulo")
    @JsonAlias("titulo")
    private String tarefaTitulo;

   @NotNull
   @JsonProperty("dataEntrega")
   private LocalDate dataEntrega;

}
