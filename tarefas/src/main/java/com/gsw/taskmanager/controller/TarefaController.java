package com.gsw.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.service.TarefaService;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    // Criar tarefa
    @PostMapping("/criar")
    public ResponseEntity<Tarefa> criar(@RequestBody Tarefa tarefa) {
        Tarefa novaTarefa = tarefaService.criar(tarefa);
        return ResponseEntity.status(201).body(novaTarefa); // 201 Created
    }

    // Listar todas
    @GetMapping
    public ResponseEntity<List<Tarefa>> listarTodas() {
        return ResponseEntity.ok(tarefaService.listarTodas()); // 200 OK
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> buscarPorId(@PathVariable String id) {
        Tarefa tarefa = tarefaService.buscarPorId(id);
        return ResponseEntity.ok(tarefa); // 200 OK
    }

    // Atualizar
    @PutMapping("/editar/{id}")
    public ResponseEntity<Tarefa> atualizar(@PathVariable String id, @RequestBody Tarefa tarefaAtualizada) {
        Tarefa tarefa = tarefaService.atualizar(id, tarefaAtualizada);
        return ResponseEntity.ok(tarefa); // 200 OK
    }

    // Deletar (soft delete)
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        tarefaService.deletarById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}