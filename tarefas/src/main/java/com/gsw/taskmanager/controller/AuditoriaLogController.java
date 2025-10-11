package com.gsw.taskmanager.controller;

import com.gsw.taskmanager.dto.AuditoriaResponseDto;
import com.gsw.taskmanager.service.AuditoriaLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("logs")
public class AuditoriaLogController {

    @Autowired
    private AuditoriaLogService auditoriaLogService;

    @GetMapping("/{tarefaId}")
    public ResponseEntity<List<AuditoriaResponseDto>> listarPorTarefaId(@PathVariable String tarefaId) {
        return ResponseEntity.ok(auditoriaLogService.listarPorTarefaId(tarefaId));
    }

}
