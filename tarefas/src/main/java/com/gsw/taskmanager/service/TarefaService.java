package com.gsw.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.exception.BusinessException;
import com.gsw.taskmanager.repository.TarefaRepository;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    // LISTAR TODAS (apenas ativas)
    public List<Tarefa> listarTodas() {
        return tarefaRepository.findAll()
                .stream()
                .filter(Tarefa::isAtivo)
                .toList();
    }

    // BUSCAR POR ID
    public Tarefa buscarPorId(String id) {
        Optional<Tarefa> tarefaBuscada = tarefaRepository.findById(id);
        return tarefaBuscada
                .filter(Tarefa::isAtivo)
                .orElseThrow(() -> new BusinessException("Tarefa não encontrada ou já excluída"));
    }

    // CRIAR
    public Tarefa criar(Tarefa tarefa) {
        tarefa.setAtivo(true);
        tarefa.setDataCriacao(LocalDateTime.now()); // se tiver esse campo
        return tarefaRepository.save(tarefa);
    }

    // ATUALIZAR
    public Tarefa atualizar(String id, Tarefa tarefaAtualizada) {
        Tarefa tarefaBanco = tarefaRepository.findById(id).orElseThrow();

        if (tarefaAtualizada.getTitulo() != null) {
            tarefaBanco.setTitulo(tarefaAtualizada.getTitulo());
        }
        if (tarefaAtualizada.getDescricao() != null) {
            tarefaBanco.setDescricao(tarefaAtualizada.getDescricao());
        }
        if (tarefaAtualizada.getResponsavel() != null) {
            tarefaBanco.setResponsavel(tarefaAtualizada.getResponsavel());
        }
        if (tarefaAtualizada.getDataEntrega() != null) {
            tarefaBanco.setDataEntrega(tarefaAtualizada.getDataEntrega());
        }
        if (tarefaAtualizada.getTema() != null) {
            tarefaBanco.setTema(tarefaAtualizada.getTema());
        }
        if(tarefaAtualizada.getStatus()!=null){
            tarefaBanco.setStatus(tarefaAtualizada.getStatus());
        }
       
        return tarefaRepository.save(tarefaBanco);
    }

    // "DELETAR" (soft delete)
    public void deletarById(String id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow();
        if (tarefa.isAtivo()) {
            tarefa.setAtivo(false);
            tarefaRepository.save(tarefa);
        } else {
            throw new BusinessException("Tarefa já está deletada.");
        }
    }

    public Tarefa salvarTarefa(Tarefa tarefa) {
    return tarefaRepository.save(tarefa);
}
}