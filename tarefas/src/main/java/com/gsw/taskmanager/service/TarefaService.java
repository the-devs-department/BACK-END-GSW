package com.gsw.taskmanager.service;

import com.gsw.taskmanager.entity.AuditoriaLog;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.exception.BusinessException;
import com.gsw.taskmanager.repository.TarefaRepository;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private AuditoriaLogService logService;
    @Autowired
    private AuditoriaLogService auditoriaLogService;

    @Autowired
    private NotificationService notificationService;

    // LISTAR TODAS (apenas ativas)
    public List<Tarefa> listarTodas() {
        return tarefaRepository.findAll()
                .stream()
                .filter(Tarefa::isAtivo)
                .toList();
    }

    // Listar tarefas por membro da equipe (responsável)
    public List<Tarefa> listarPorResponsavel(String usuarioId) {
        return tarefaRepository.findByResponsavel(usuarioId)
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
        tarefa.setDataCriacao(LocalDateTime.now());

        try {
            Tarefa tarefaSalva = tarefaRepository.save(tarefa);
            AuditoriaLog log = logService.registrarCriacao(tarefaSalva);
            System.out.println(log.getModificacoes());
            
           
            tarefaRepository.save(tarefaSalva); 

            // LÓGICA DE NOTIFICAÇÃO (CRIAÇÃO) 
            if (tarefaSalva.getResponsavel() != null && !tarefaSalva.getResponsavel().isEmpty()) {
                String userId = tarefaSalva.getResponsavel();
                String message = "Nova tarefa atribuída a você: " + tarefaSalva.getTitulo();
                String link = "/tasks/" + tarefaSalva.getId(); // Link do front-end
                
                notificationService.sendNotification(userId, message, link);
            }

            return tarefaSalva;

        } catch (Exception e) {
            throw new BusinessException("Erro ao criar tarefa");
        }
    }

    // ATUALIZAR
    public Tarefa atualizar(String id, Tarefa tarefaAtualizada) {
        Tarefa tarefaBanco = tarefaRepository.findById(id).orElseThrow();
        Tarefa tarefaAntiga = SerializationUtils.clone(tarefaBanco);

        String oldResponsavel = tarefaAntiga.getResponsavel();

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

        String newResponsavel = tarefaBanco.getResponsavel();

       try {
            Tarefa tarefaSalva = tarefaRepository.save(tarefaBanco);
            auditoriaLogService.registrarAtualizacao(tarefaAntiga, tarefaBanco);

            // LÓGICA DE NOTIFICAÇÃO (ATRIBUIÇÃO/EDIÇÃO) 
            
            //  Notifica se o responsável MUDOU 
            if (newResponsavel != null && !newResponsavel.equals(oldResponsavel)) {
                notificationService.sendNotification(
                    newResponsavel,
                    "Você foi atribuído(a) à tarefa: " + tarefaSalva.getTitulo(),
                    "/tasks/" + tarefaSalva.getId()
                );
            }
            
            // Notifica o responsável sobre outras mudanças (ex: prazo)
            if (tarefaAtualizada.getDataEntrega() != null && 
                !tarefaAtualizada.getDataEntrega().equals(tarefaAntiga.getDataEntrega()) &&
                newResponsavel != null) 
            {
                notificationService.sendNotification(
                    newResponsavel,
                    "O prazo da tarefa '" + tarefaSalva.getTitulo() + "' foi alterado.",
                    "/tasks/" + tarefaSalva.getId()
                );
            }

            return tarefaSalva;

        } catch (Exception e) {
            throw new BusinessException("Erro ao atualizar tarefa");
        }
    }

    // "DELETAR" (soft delete)
    public void deletarById(String id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow();
        if (tarefa.isAtivo()) {
            tarefa.setAtivo(false);
            tarefaRepository.save(tarefa);
            auditoriaLogService.registrarExclusao(tarefa);

            // LÓGICA DE NOTIFICAÇÃO (EXCLUSÃO) 
            if (tarefa.getResponsavel() != null && !tarefa.getResponsavel().isEmpty()) {
                notificationService.sendNotification(
                    tarefa.getResponsavel(),
                    "A tarefa '" + tarefa.getTitulo() + "' foi excluída.",
                    "/tasks/" + tarefa.getId()
                );
            }

        } else {
            throw new BusinessException("Tarefa já está deletada.");
        }
    }

    public void salvarTarefa(Tarefa tarefa) {
        tarefaRepository.save(tarefa);
    }

}