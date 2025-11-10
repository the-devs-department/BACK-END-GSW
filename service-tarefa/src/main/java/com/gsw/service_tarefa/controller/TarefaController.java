package com.gsw.service_tarefa.controller;

import com.gsw.service_tarefa.entity.Tarefa;
import com.gsw.service_tarefa.service.TarefaService;
import com.gsw.service_tarefa.dto.usuario.AtribuicaoRequestDto;
import com.gsw.service_tarefa.service.AtribuicaoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private AtribuicaoService atribuicaoService;

    @PostMapping("/criar")
    public ResponseEntity<Tarefa> criar(@RequestBody Tarefa tarefa) {
        Tarefa novaTarefa = tarefaService.criar(tarefa);
        return ResponseEntity.status(201).body(novaTarefa);
    }

    @PostMapping("/{tarefaId}/atribuir")
    public ResponseEntity<Void> atribuirTarefa(
            @PathVariable String tarefaId,
            @RequestBody AtribuicaoRequestDto request) {

        atribuicaoService.atribuirUsuarioATarefa(tarefaId, request.usuarioId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Tarefa>> listar(
            @RequestParam(name = "responsavel", required = false) String responsavel) {

        List<Tarefa> tarefas = (responsavel != null && !responsavel.isEmpty())
                ? tarefaService.listarPorResponsavel(responsavel)
                : tarefaService.listarTodas();

        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> buscarPorId(@PathVariable String id) {
        Tarefa tarefa = tarefaService.buscarPorId(id);
        return ResponseEntity.ok(tarefa);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Tarefa> atualizar(
            @PathVariable String id,
            @RequestBody Tarefa tarefaAtualizada) {

        Tarefa tarefa = tarefaService.atualizar(id, tarefaAtualizada);
        return ResponseEntity.ok(tarefa);
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        tarefaService.deletarById(id);
        return ResponseEntity.noContent().build();
    }
}
