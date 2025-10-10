package com.gsw.taskmanager.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DataHoraDto {

    @NotNull (message = "O campo do dia não pode estar vazio")
    private Integer dia;

    @NotNull (message = "O campo do mês não pode estar vazio")
    private Integer mes;

    @NotNull (message = "O campo do ano não pode estar vazio")
    private Integer ano;

    @NotNull (message = "O campo da hora não pode estar vazio")
    private Integer hora;

    @NotNull (message = "O campo do minuto não pode estar vazio")
    private Integer minuto;
    
    @NotNull (message = "O campo do segundo não pode estar vazio")
    private Integer segundo;

}