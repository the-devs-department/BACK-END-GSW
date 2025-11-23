package com.gsw.service_calendario.controller;

import com.gsw.service_calendario.entity.Calendario;
import com.gsw.service_calendario.exception.CalendarioLoadException;
import com.gsw.service_calendario.service.CalendarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendario")
public class CalendarioController {

    @Autowired
    CalendarioService calendarioService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable String usuarioId) {
        try {
            List<Calendario> calendarios = calendarioService.listarPorUsuario(usuarioId);
            return ResponseEntity.ok(calendarios);
        } catch (CalendarioLoadException ex) {
            // Return a generic, user-friendly message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível carregar o calendário.");
        }
    }

    @GetMapping("/usuario/{usuarioId}/data/{dia}")
    public ResponseEntity<?> tarefasDoDia(
            @PathVariable String usuarioId,
            @PathVariable String dia) {

        try {
            List<Calendario> result = calendarioService.tarefasDoDia(usuarioId, dia);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (CalendarioLoadException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível carregar o calendário.");
        }
    }
}