package com.gsw.service_calendario.service;

import com.gsw.service_calendario.client.TarefaClient;
import com.gsw.service_calendario.dto.TarefaDTO;
import com.gsw.service_calendario.entity.Calendario;
import com.gsw.service_calendario.exception.CalendarioLoadException;
import com.gsw.service_calendario.repository.CalendarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarioService {

    @Autowired
    CalendarioRepository calendarioRepository;

    @Autowired
    TarefaClient tarefaClient;

    public List<Calendario> listarPorUsuario(String usuarioId) {
        ResponseEntity<List<TarefaDTO>> resp;
        try {
            resp = tarefaClient.listarPorResponsavel(usuarioId);
        } catch (Exception ex) {
            // Wrap any client or other unexpected exceptions into a user-friendly domain exception
            throw new CalendarioLoadException("Não foi possível carregar o calendário.", ex);
        }

        List<TarefaDTO> tarefas = resp.getBody() != null ? resp.getBody() : new ArrayList<>();

        List<Calendario> calendarios = new ArrayList<>();

        for (TarefaDTO t : tarefas) {
            Calendario c = new Calendario();

            c.setUsuarioId(usuarioId);

            List<TarefaDTO> lista = new ArrayList<>();
            lista.add(t);
            c.setTarefas(lista);

            Calendario saved = calendarioRepository.save(c);
            calendarios.add(saved);
        }

        return calendarios;
    }

    public List<Calendario> tarefasDoDia(String usuarioId, String dia) {
        if (dia == null || dia.isBlank()) {
            throw new IllegalArgumentException("Data obrigatória");
        }

        LocalDate data;
        try {
            data = LocalDate.parse(dia);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato de data inválido. Use yyyy-MM-dd");
        }

        ResponseEntity<List<TarefaDTO>> resp;
        try {
            resp = tarefaClient.listarPorResponsavel(usuarioId);
        } catch (Exception ex) {
            throw new CalendarioLoadException("Não foi possível carregar o calendário.", ex);
        }

        List<TarefaDTO> tarefas = resp.getBody() != null ? resp.getBody() : new ArrayList<>();

        List<Calendario> result = new ArrayList<>();
        for (TarefaDTO t : tarefas) {
            if (t.getDataEntrega() == null) continue;

            LocalDate tarefaDate = t.getDataEntrega();

            if (tarefaDate.equals(data)) {
                Calendario c = new Calendario();
                c.setUsuarioId(usuarioId);
                List<TarefaDTO> lista = new ArrayList<>();
                lista.add(t);
                c.setTarefas(lista);

                Calendario saved = calendarioRepository.save(c);
                result.add(saved);
            }
        }

        return result;
    }
}
