package com.gsw.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.service.TarefaService;

@RestController
@RequestMapping("tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    // Nivel 2 seguindo o padrão do gerson 
    // Criação de tarefas
    @PostMapping
    public ResponseEntity<Tarefa> criar(@RequestBody Tarefa tarefa) {
        Tarefa novaTarefa = tarefaService.criar(tarefa);
        return ResponseEntity.status(201).body(novaTarefa); // 201 Created
    }

    // Read de todas as tarefas 
    @GetMapping
    public ResponseEntity<List<Tarefa>> listarTodas() {
        return ResponseEntity.ok(tarefaService.ListarTarefa()); // 200 OK
    }

    // Read por id
    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> buscarPorId(@PathVariable String id) {
        Tarefa tarefa = tarefaService.BuscaPorId(id);
        if (tarefa == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(tarefa); // 200 OK
    }
}
