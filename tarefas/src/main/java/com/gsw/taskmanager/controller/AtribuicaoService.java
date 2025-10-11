package com.gsw.taskmanager.controller;

import com.gsw.taskmanager.dto.UsuarioResponseDto;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.exception.anexo.TarefaNaoEncontradaException;
import com.gsw.taskmanager.service.AuditoriaLogService;
import com.gsw.taskmanager.service.TarefaService;
import com.gsw.taskmanager.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AtribuicaoService {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuditoriaLogService auditoriaLogService;

    public void atribuirUsuarioATarefa(String tarefaId, String usuarioId) {

        CompletableFuture<Tarefa> tarefaFuture = CompletableFuture.supplyAsync(() -> tarefaService.buscarPorId(tarefaId));
        CompletableFuture<UsuarioResponseDto> usuarioFuture = CompletableFuture.supplyAsync(() -> usuarioService.buscarUsuarioPorId(usuarioId));

        CompletableFuture.allOf(tarefaFuture, usuarioFuture).join();

        Tarefa tarefa = tarefaFuture.join();
        UsuarioResponseDto novoUsuario = usuarioFuture.join();

        if (tarefa == null || novoUsuario == null) {
            throw new TarefaNaoEncontradaException("Tarefa ou usuário não encontrado");
        }

        tarefa.setResponsavel(novoUsuario.nome());

        // Log de auditoria
        auditoriaLogService.registrarAtribuicao(tarefa, tarefa.getResponsavel(), novoUsuario.nome());

        // Salva a tarefa atualizada
        tarefaService.salvarTarefa(tarefa);
    }
}