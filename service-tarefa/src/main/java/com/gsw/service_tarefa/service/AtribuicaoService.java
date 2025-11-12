package com.gsw.service_tarefa.service;

import com.gsw.service_tarefa.entity.Tarefa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AtribuicaoService {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaLogService auditoriaLogService;

    public void atribuirUsuarioATarefa(String tarefaId, String usuarioId) {

        CompletableFuture<Tarefa> tarefaFuture = CompletableFuture.supplyAsync(() -> tarefaService.buscarPorId(tarefaId));
        CompletableFuture<Usuario> usuarioFuture = CompletableFuture.supplyAsync(() -> usuarioRepository.findById(usuarioId).orElse(null));

        CompletableFuture.allOf(tarefaFuture, usuarioFuture).join();

        Tarefa tarefa = tarefaFuture.join();
        Usuario novoUsuario = usuarioFuture.join();

        if (tarefa == null || novoUsuario == null) {
            throw new TarefaNaoEncontradaException("Tarefa ou usuário não encontrado");
        }

        tarefa.setResponsavel(new UsuarioResponsavelTarefaDto(
                novoUsuario.getId(),
                novoUsuario.getNome(),
                novoUsuario.getEmail()));

        // Log de auditoria
        auditoriaLogService.registrarAtribuicao(tarefa, tarefa.getResponsavel().nome(), novoUsuario.getNome());

        // Salva a tarefa atualizada
        tarefaService.salvarTarefa(tarefa);
    }
}