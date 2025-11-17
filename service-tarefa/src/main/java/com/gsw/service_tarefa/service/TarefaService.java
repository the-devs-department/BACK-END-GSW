package com.gsw.service_tarefa.service;

import com.gsw.service_tarefa.client.AuditoriaClient;
import com.gsw.service_tarefa.dto.UsuarioResponsavelDTO;
import com.gsw.service_tarefa.dto.log.AuditoriaLogDTO;
import com.gsw.service_tarefa.entity.Tarefa;
import com.gsw.service_tarefa.enums.Status;
import com.gsw.service_tarefa.exceptions.BusinessException;
import com.gsw.service_tarefa.repository.TarefaRepository;
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
    private AuditoriaClient logAuditoriaClient;

    public List<Tarefa> listarTodas() {
        return tarefaRepository.findAll()
                .stream()
                .filter(Tarefa::isAtivo)
                .toList();
    }

    public List<Tarefa> listarPorResponsavel(String usuarioId) {
        return tarefaRepository.findByResponsavelId(usuarioId)
                .stream()
                .filter(Tarefa::isAtivo)
                .toList();
    }

    public Tarefa buscarPorId(String id) {
        Optional<Tarefa> tarefaBuscada = tarefaRepository.findById(id);
        return tarefaBuscada
                .filter(Tarefa::isAtivo)
                .orElseThrow(() -> new BusinessException("Tarefa não encontrada ou já excluída"));
    }

   public Tarefa criar(Tarefa tarefa) {
        tarefa.setAtivo(true);
        tarefa.setDataCriacao(LocalDateTime.now());

        try {
            Tarefa tarefaSalva = tarefaRepository.save(tarefa);
            AuditoriaLogDTO log = logAuditoriaClient.registrarCriacao(tarefaSalva)
            System.out.println(log.getModificacoes());
            tarefaRepository.save(tarefaSalva); 

            if (tarefaSalva.getResponsavel() != null) {
                UsuarioResponsavelDTO responsavelDto = tarefaSalva.getResponsavel();
                String userIdParaNotificar = responsavelDto.getId();
                String message = "Nova tarefa atribuída a você: " + tarefaSalva.getTitulo();
                String link = "/tarefas/" + tarefaSalva.getId(); 
                
                notificationService.sendNotification(userIdParaNotificar, message, link);
            }
            return tarefaSalva;
        } catch (Exception e) {
            throw new BusinessException("Erro ao criar tarefa");
        }
    }

    public Tarefa atualizar(String id, Tarefa tarefaAtualizada) {
        Tarefa tarefaBanco = tarefaRepository.findById(id).orElseThrow();
        Tarefa tarefaAntiga = SerializationUtils.clone(tarefaBanco);

        String oldResponsavelId = (tarefaAntiga.getResponsavel() != null)
                ? tarefaAntiga.getResponsavel().getId()
                : null;

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
        if (tarefaAtualizada.getStatus() != null) {
            tarefaBanco.setStatus(tarefaAtualizada.getStatus());
        }

        String newResponsavelId = (tarefaBanco.getResponsavel() != null)
                ? tarefaBanco.getResponsavel().getId()
                : null;

        try {
            Tarefa tarefaSalva = tarefaRepository.save(tarefaBanco);
            logAuditoriaClient.registrarAtualizacao(tarefaAntiga, tarefaBanco);

            if (newResponsavelId != null && !newResponsavelId.equals(oldResponsavelId)) {
                notificationService.sendNotification(
                        newResponsavelId,
                        "Você foi atribuído(a) à tarefa: " + tarefaSalva.getTitulo(),
                        "/tarefas/" + tarefaSalva.getId());
            }
            if (tarefaAtualizada.getDataEntrega() != null &&
                    !tarefaAtualizada.getDataEntrega().equals(tarefaAntiga.getDataEntrega()) &&
                    newResponsavelId != null) {
                notificationService.sendNotification(
                        newResponsavelId,
                        "O prazo da tarefa '" + tarefaSalva.getTitulo() + "' foi alterado.",
                        "/tarefas/" + tarefaSalva.getId());
            }

            return tarefaSalva;

        } catch (Exception e) {
            throw new BusinessException("Erro ao atualizar tarefa");
        }
    }

    public void deletarById(String id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow();
        if (tarefa.isAtivo()) {
            tarefa.setAtivo(false);
            tarefaRepository.save(tarefa);
            logAuditoriaClient.registrarExclusao(tarefa);

            if (tarefa.getResponsavel() != null) {
                String responsavelId = tarefa.getResponsavel().getId();
                notificationService.sendNotification(
                        responsavelId,
                        "A tarefa '" + tarefa.getTitulo() + "' foi excluída.",
                        "/tarefas/" + tarefa.getId());
            }

        } else {
            throw new BusinessException("Tarefa já está deletada.");
        }
    }

    public void salvarTarefa(Tarefa tarefa) {
        tarefaRepository.save(tarefa);
    }

}