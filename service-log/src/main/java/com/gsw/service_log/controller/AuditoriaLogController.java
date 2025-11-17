package com.gsw.service_log.controller;

import com.gsw.service_log.dto.logs.AuditoriaResponseDto;
import com.gsw.service_log.dto.tarefa.TarefaDto;
import com.gsw.service_log.entity.AuditoriaLog;
import com.gsw.service_log.service.AuditoriaLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class AuditoriaLogController {

    @Autowired
    private AuditoriaLogService auditoriaLogService;

    @GetMapping("/{tarefaId}")
    public ResponseEntity<List<AuditoriaResponseDto>> listarPorTarefaId(@PathVariable String tarefaId) {
        return ResponseEntity.ok(auditoriaLogService.listarPorTarefaId(tarefaId));
    }

    @GetMapping
    public ResponseEntity<List<AuditoriaResponseDto>> listarTodos() {
        return ResponseEntity.ok(auditoriaLogService.listarTodos());
    }

    @PostMapping("/registar-criacao")
    public AuditoriaLog registrarCriacaoTarefa(@RequestBody TarefaDto tarefaDto) {
        AuditoriaLog logCriado = auditoriaLogService.registrarCriacao(tarefaDto);
        return logCriado;
    }

    @PostMapping("/registrar-atualizacao")
    public void registrarAtualizacaoTarefa(@RequestBody TarefaDto tarefaAntiga, TarefaDto tarefaNova) {
        auditoriaLogService.registrarAtualizacao(tarefaAntiga, tarefaNova);
    }

    @PostMapping("/registrar-exclusao")
    public void registrarExclusaoTarefa(@RequestBody TarefaDto tarefa) {
        auditoriaLogService.registrarExclusao(tarefa);
    }

    @PostMapping("/registrar-atribuicao")
    public void registrarAtribuicaoTarefa(@RequestBody TarefaDto tarefaAntiga, String usuarioAnterior, String usuarioNovo) {
        auditoriaLogService.registrarAtribuicao(tarefaAntiga, usuarioNovo, usuarioNovo);
    }

}
