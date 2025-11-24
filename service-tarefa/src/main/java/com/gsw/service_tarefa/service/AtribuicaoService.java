package com.gsw.service_tarefa.service;

import com.gsw.service_tarefa.client.AuditoriaClient;
import com.gsw.service_tarefa.client.UsuarioClient;
import com.gsw.service_tarefa.dto.AtribuicaoDTO;
import com.gsw.service_tarefa.dto.UsuarioResponsavelDTO;
import com.gsw.service_tarefa.dto.UsuarioResponseDTO;
import com.gsw.service_tarefa.entity.Tarefa;
import com.gsw.service_tarefa.exceptions.Tarefa.TarefaNaoEncontradaException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AtribuicaoService {

    @Autowired
    private TarefaService tarefaService;
    @Autowired
    private AuditoriaClient logAuditoriaClient;
    @Autowired
    private UsuarioClient usuarioClient;

    public void atribuirUsuarioATarefa(String tarefaId, String usuarioId) {

        CompletableFuture<Tarefa> tarefaFuture = CompletableFuture
                .supplyAsync(() -> tarefaService.buscarPorId(tarefaId));

        CompletableFuture<UsuarioResponseDTO> usuarioFuture = CompletableFuture.supplyAsync(() -> {
            var response = usuarioClient.findUserById(usuarioId);
            if (response == null || response.getBody() == null) {
                throw new TarefaNaoEncontradaException("Usuário não encontrado");
            }
            return response.getBody();
        });

        CompletableFuture<Void> combinado = CompletableFuture.allOf(tarefaFuture, usuarioFuture);

        combinado.join();

        Tarefa tarefa = tarefaFuture.join();
        UsuarioResponseDTO usuario = usuarioFuture.join();

        tarefa.setResponsavel(new UsuarioResponsavelDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()));

        AtribuicaoDTO atribDTO = new AtribuicaoDTO(tarefa.getId(), usuario.getNome(), usuario.getNome());
        logAuditoriaClient.registrarAtribuicao(atribDTO);
        tarefaService.salvarTarefa(tarefa);
    }

}