package com.gsw.api_gateway.service;

import com.gsw.api_gateway.utils.DataHoraDto;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
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
