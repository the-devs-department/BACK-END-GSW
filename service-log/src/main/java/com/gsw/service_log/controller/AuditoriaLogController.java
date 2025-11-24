package com.gsw.service_log.controller;

import com.gsw.service_log.dto.AtribuicaoDTO;
import com.gsw.service_log.dto.AtualizacaoDTO;
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
    public ResponseEntity<AuditoriaLog> registrarCriacaoTarefa(@RequestBody TarefaDto tarefaDto) {
        AuditoriaLog logCriado = auditoriaLogService.registrarCriacao(tarefaDto);
        return ResponseEntity.ok(logCriado);
    }

    @PostMapping("/registrar-atualizacao")
    public ResponseEntity<Void> registrarAtualizacaoTarefa(@RequestBody AtualizacaoDTO atualizacao) {
        auditoriaLogService.registrarAtualizacao(
                atualizacao.getTarefaAntiga(),
                atualizacao.getTarefaAtualizada()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registrar-exclusao")
    public ResponseEntity<Void> registrarExclusaoTarefa(@RequestBody TarefaDto tarefa) {
        auditoriaLogService.registrarExclusao(tarefa);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registrar-atribuicao")
    public ResponseEntity<Void> registrarAtribuicaoTarefa(@RequestBody AtribuicaoDTO atribuicao) {
        auditoriaLogService.registrarAtribuicao(
                atribuicao.getTarefaAntigaId(),
                atribuicao.getUsuarioAntigo(),
                atribuicao.getUsuarioNovo()
        );
        return ResponseEntity.ok().build();
    }
}
