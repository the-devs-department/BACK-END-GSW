package com.gsw.taskmanager.service;

import com.gsw.taskmanager.dto.AuditoriaResponseDto;
import com.gsw.taskmanager.dto.ResponsavelAlteracaoDto;
import com.gsw.taskmanager.entity.Anexo;
import com.gsw.taskmanager.entity.AuditoriaLog;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.exception.BusinessException;
import com.gsw.taskmanager.repository.AuditoriaLogRepository;
import com.gsw.taskmanager.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuditoriaLogService {

    @Autowired
    private AuditoriaLogRepository auditoriaLogRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Transient
    private static final Set<String> ignoredFields = Set.of(
            "id", "dataCriacao", "ativo"
    );

    @Transient
    private static final Map<String, String> fieldLabels = Map.of(
            "titulo", "Título",
            "descricao", "Descrição",
            "status", "Status",
            "responsavel", "Responsável",
            "dataEntrega", "Data de entrega",
            "tema", "Tema"
    );

    // ------------------------- CRIAÇÃO -------------------------

    public AuditoriaLog registrarCriacao(Tarefa tarefaNova){
        List<String> modificacoes = new ArrayList<>();
        modificacoes.add("Tarefa criada com título '" + tarefaNova.getTitulo() + "'.");
        return auditoriaLogRepository.save(criarLog(tarefaNova.getId(), modificacoes));
    }

    public void registrarAtualizacao(Tarefa tarefaAntiga, Tarefa tarefaNova) {
        List<String> modificacoes = new ArrayList<>();

        modificacoes.addAll(visualizadorDeMudancas(tarefaAntiga, tarefaNova));

        modificacoes.addAll(visualizadorDeAnexos(tarefaAntiga.getAnexos(), tarefaNova.getAnexos()));

        if (modificacoes.isEmpty()) {
            modificacoes.add("Nenhuma alteração detectada.");
            return;
        }

        auditoriaLogRepository.save(criarLog(tarefaNova.getId(), modificacoes));
    }

    // ------------------------- EXCLUSÃO -------------------------

    public void registrarExclusao(Tarefa tarefa){
        List<String> modificacoes = new ArrayList<>();
        modificacoes.add("Tarefa removida (soft delete)");

        auditoriaLogRepository.save(criarLog(tarefa.getId(), modificacoes));
    }

    // ------------------------- ATRIBUIÇÃO -----------------------
    public void registrarAtribuicao(Tarefa tarefaAntiga, String usuarioAnterior, String usuarioNovo) {
        List<String> modificacoes = new ArrayList<>();
        modificacoes.add("Responsável alterado de '" + usuarioAnterior + "' para '" + usuarioNovo + "'.");
        tarefaAntiga.setResponsavel(usuarioNovo);
        auditoriaLogRepository.save(criarLog(tarefaAntiga.getId(), modificacoes));
    }

    private List<String> visualizadorDeMudancas(Tarefa tarefaAntiga, Tarefa tarefaNova) {
        List<String> modificacoes = new ArrayList<>();

        if (tarefaAntiga == null || tarefaNova == null) return modificacoes;

        Field[] fields = Tarefa.class.getDeclaredFields();

        for (Field field : fields) {
            if (ignoredFields.contains(field.getName())) continue;

            field.setAccessible(true);
            try {
                Object valorAntigo = field.get(tarefaAntiga);
                Object valorNovo = field.get(tarefaNova);

                if (!Objects.equals(valorAntigo, valorNovo)) {
                    String nomeCampo = fieldLabels.getOrDefault(field.getName(), field.getName());
                    modificacoes.add(nomeCampo + " alterado de '" + valorAntigo + "' para '" + valorNovo + "'.");
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return modificacoes;
    }

    private List<String> visualizadorDeAnexos(List<Anexo> anexosAntigos, List<Anexo> anexosNovos) {
        List<String> modificacoes = new ArrayList<>();

        Map<String, Anexo> antigosMap = new HashMap<>();
        for (Anexo anexo : anexosAntigos) antigosMap.put(anexo.getId(), anexo);

        Map<String, Anexo> novosMap = new HashMap<>();
        for (Anexo anexo : anexosNovos) novosMap.put(anexo.getId(), anexo);

        for (Anexo novo : anexosNovos) {
            if (!antigosMap.containsKey(novo.getId())) {
                modificacoes.add("Anexo '" + novo.getNome() + "' adicionado.");
            }
        }

        for (Anexo antigo : anexosAntigos) {
            if (!novosMap.containsKey(antigo.getId())) {
                modificacoes.add("Anexo '" + antigo.getNome() + "' removido.");
            }
        }

        for (Anexo novo : anexosNovos) {
            Anexo antigo = antigosMap.get(novo.getId());
            if (antigo != null) {
                if (!Objects.equals(antigo.getNome(), novo.getNome())) {
                    modificacoes.add("Anexo '" + antigo.getNome() + "' alterado: nome de '" + antigo.getNome() + "' para '" + novo.getNome() + "'.");
                }
                if (!Objects.equals(antigo.getTipo(), novo.getTipo())) {
                    modificacoes.add("Anexo '" + antigo.getNome() + "' alterado: tipo de '" + antigo.getTipo() + "' para '" + novo.getTipo() + "'.");
                }
                if (!Objects.equals(antigo.getUrl(), novo.getUrl())) {
                    modificacoes.add("Anexo '" + antigo.getNome() + "' alterado: URL de '" + antigo.getUrl() + "' para '" + novo.getUrl() + "'.");
                }
            }
        }
        return modificacoes;
    }

    private AuditoriaLog criarLog(String tarefaId, List<String> modificacoes) {
        Usuario usuario = obterUsuarioAutenticado();

        AuditoriaLog log = new AuditoriaLog();

        log.setTarefaId(tarefaId);
        log.setResponsavel(new ResponsavelAlteracaoDto(usuario.getId(), usuario.getNome()));
        log.setCriadoEm(LocalDateTime.now());
        log.setModificacoes(modificacoes);

        return log;
    }

    private Usuario obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof Usuario) {
            return (Usuario) principal;
        } else {
            throw new BusinessException("Usuário autenticado inválido");
        }
    }

    public List<AuditoriaResponseDto> listarPorTarefaId(String tarefaId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId).filter(Tarefa::isAtivo).orElseThrow(() -> new BusinessException("Tarefa não encontrada."));
        List<AuditoriaLog> logs = auditoriaLogRepository.findAllByTarefaId(tarefaId);

        List<AuditoriaResponseDto> modificacoes = logs.stream()
                .flatMap(log -> log.getModificacoes().stream()
                        .map(mod -> new AuditoriaResponseDto(
                                log.getResponsavel().nomeResponsavel(),
                                mod,
                                log.getCriadoEm()
                        ))
                ).toList();

        return modificacoes;
    }
}
