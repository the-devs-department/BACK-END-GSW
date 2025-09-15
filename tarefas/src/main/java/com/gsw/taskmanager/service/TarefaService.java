package com.gsw.taskmanager.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.repository.TarefaRepository;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    // CREATE
    public Tarefa criar(Tarefa tarefa) {
        return tarefaRepository.save(tarefa);
    }

    // READ - listar todas
    public List<Tarefa> ListarTarefa() {
        return tarefaRepository.findAll();
    }

    // READ - buscar por ID
    public Tarefa BuscaPorId(String id) {
        return tarefaRepository.findById(id).orElse(null);
    }
}
