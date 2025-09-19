package com.gsw.taskmanager.service;

import com.gsw.taskmanager.dto.DataHoraDto;
import java.time.LocalDateTime;

public class DataHoraService {
    
    public LocalDateTime converterParaLocalDateTime(DataHoraDto dataHoraDto) {
        int dia = dataHoraDto.getDia();
        int mes = dataHoraDto.getMes();
        int ano = dataHoraDto.getAno();
        int hora = dataHoraDto.getHora();
        int minuto = dataHoraDto.getMinuto();
        int segundo = dataHoraDto.getSegundo();
        
        return LocalDateTime.of(ano, mes, dia, hora, minuto, segundo);
    }
}
