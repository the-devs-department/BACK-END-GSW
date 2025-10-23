package com.gsw.taskmanager.service;

import com.gsw.taskmanager.dto.usuario.UsuarioResponsavelTarefaDto;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.exception.anexo.TarefaNaoEncontradaException;
import com.gsw.taskmanager.repository.UsuarioRepository;

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