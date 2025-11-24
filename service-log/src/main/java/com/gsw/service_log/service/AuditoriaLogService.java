package com.gsw.service_log.service;

import com.gsw.service_log.client.TarefaClient;
import com.gsw.service_log.dto.anexo.AnexoDto;
import com.gsw.service_log.dto.logs.AuditoriaResponseDto;
import com.gsw.service_log.dto.logs.ModificacaoLogDto;
import com.gsw.service_log.dto.logs.ResponsavelAlteracaoDto;
import com.gsw.service_log.entity.AuditoriaLog;
import com.gsw.service_log.repository.AuditoriaLogRepository;
import com.gsw.service_log.dto.tarefa.TarefaDto;
import com.gsw.service_log.enums.CategoriaModificacao;
import com.gsw.service_log.dto.usuario.UsuarioDto;
import com.gsw.service_log.exception.BusinessException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AuditoriaLogService {

    @Autowired
    private AuditoriaLogRepository auditoriaLogRepository;

    @Autowired
    private TarefaClient tarefaClient;

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

    public AuditoriaLog registrarCriacao(TarefaDto tarefaNova) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();

        modificacoes.add(new ModificacaoLogDto(
                CategoriaModificacao.CRIACAO,
                "Tarefa criada com título '" + tarefaNova.getTitulo() + "'."
        ));

        return auditoriaLogRepository.save(criarLog(tarefaNova.getId(), modificacoes));
    }

    // ------------------------- ATUALIZAÇÃO -------------------------

    public void registrarAtualizacao(TarefaDto tarefaAntiga, TarefaDto tarefaNova) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();

        modificacoes.addAll(visualizadorDeMudancas(tarefaAntiga, tarefaNova));
        modificacoes.addAll(visualizadorDeAnexos(tarefaAntiga.getAnexos(), tarefaNova.getAnexos()));

        if (!modificacoes.isEmpty()) {
            auditoriaLogRepository.save(criarLog(tarefaNova.getId(), modificacoes));
        }
    }

    // ------------------------- EXCLUSÃO -------------------------

    public void registrarExclusao(TarefaDto tarefa) {
        List<ModificacaoLogDto> modificacoes = List.of(
                new ModificacaoLogDto(CategoriaModificacao.EXCLUSAO,
                        "Tarefa removida (soft delete)")
        );

        auditoriaLogRepository.save(criarLog(tarefa.getId(), modificacoes));
    }

    // ------------------------- ATRIBUIÇÃO -------------------------

    public void registrarAtribuicao(String tarefaId, String usuarioAnterior, String usuarioNovo) {
        List<ModificacaoLogDto> modificacoes = List.of(
                new ModificacaoLogDto(CategoriaModificacao.EDICAO,
                        "Responsável alterado de '" + usuarioAnterior +
                                "' para '" + usuarioNovo + "'.")
        );

        auditoriaLogRepository.save(criarLog(tarefaId, modificacoes));
    }

    // ------------------------- DETECTAR MUDANÇAS -------------------------

    private List<ModificacaoLogDto> visualizadorDeMudancas(TarefaDto antiga, TarefaDto nova) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();
        if (antiga == null || nova == null) return modificacoes;

        for (Field field : TarefaDto.class.getDeclaredFields()) {
            if (ignoredFields.contains(field.getName())) continue;

            field.setAccessible(true);
            try {
                Object valorAntigo = field.get(antiga);
                Object valorNovo = field.get(nova);

                if (!Objects.equals(valorAntigo, valorNovo)) {
                    modificacoes.add(
                            new ModificacaoLogDto(
                                    CategoriaModificacao.EDICAO,
                                    fieldLabels.getOrDefault(field.getName(), field.getName())
                                            + " alterado de '" + valorAntigo + "' para '" + valorNovo + "'."
                            )
                    );
                }

            } catch (IllegalAccessException ignored) {}
        }
        return modificacoes;
    }

    private List<ModificacaoLogDto> visualizadorDeAnexos(List<AnexoDto> antigos, List<AnexoDto> novos) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();

        Map<String, AnexoDto> antigosMap = new HashMap<>();
        antigos.forEach(a -> antigosMap.put(a.getId(), a));

        Map<String, AnexoDto> novosMap = new HashMap<>();
        novos.forEach(a -> novosMap.put(a.getId(), a));

        // adicionados
        for (AnexoDto novo : novos) {
            if (!antigosMap.containsKey(novo.getId())) {
                modificacoes.add(new ModificacaoLogDto(
                        CategoriaModificacao.EDICAO,
                        "Anexo '" + novo.getNome() + "' adicionado."
                ));
            }
        }

        // removidos
        for (AnexoDto antigo : antigos) {
            if (!novosMap.containsKey(antigo.getId())) {
                modificacoes.add(new ModificacaoLogDto(
                        CategoriaModificacao.EXCLUSAO,
                        "Anexo '" + antigo.getNome() + "' removido."
                ));
            }
        }

        // modificados
        for (AnexoDto novo : novos) {
            AnexoDto antigo = antigosMap.get(novo.getId());
            if (antigo != null) {

                if (!Objects.equals(antigo.getNome(), novo.getNome())) {
                    modificacoes.add(new ModificacaoLogDto(
                            CategoriaModificacao.EDICAO,
                            "Anexo '" + antigo.getNome() + "' alterado: nome de '" +
                                    antigo.getNome() + "' para '" + novo.getNome() + "'."
                    ));
                }

                if (!Objects.equals(antigo.getTipo(), novo.getTipo())) {
                    modificacoes.add(new ModificacaoLogDto(
                            CategoriaModificacao.EDICAO,
                            "Anexo '" + antigo.getNome() + "' alterado: tipo de '" +
                                    antigo.getTipo() + "' para '" + novo.getTipo() + "'."
                    ));
                }
            }
        }

        return modificacoes;
    }

    // ------------------------- CRIAR LOG -------------------------

    private AuditoriaLog criarLog(String tarefaId, List<ModificacaoLogDto> modificacoes) {
        UsuarioDto usuario = obterUsuarioAutenticado();

        AuditoriaLog log = new AuditoriaLog();
        log.setTarefaId(tarefaId);
        log.setResponsavel(new ResponsavelAlteracaoDto(usuario.getId(), usuario.getEmail()));
        log.setCriadoEm(LocalDateTime.now());
        log.setModificacoes(modificacoes);
        return log;
    }

    // ------------------------- USUÁRIO AUTENTICADO -------------------------

    private UsuarioDto obterUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UsuarioDto usuario) {
            return usuario;
        }

        throw new BusinessException("Usuário autenticado inválido");
    }

    // ------------------------- CONSULTAS -------------------------

    public List<AuditoriaResponseDto> listarPorTarefaId(String tarefaId) {
        List<AuditoriaLog> logs = auditoriaLogRepository.findAllByTarefaId(tarefaId);

        ResponseEntity<TarefaDto> response = tarefaClient.buscarPorId(tarefaId);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new BusinessException("Tarefa não encontrada. Status: " + response.getStatusCode());
        }

        TarefaDto tarefa = response.getBody();
        if (!tarefa.isAtivo()) {
            throw new BusinessException("Tarefa não encontrada ou inativa.");
        }

        DateTimeFormatter d = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter h = DateTimeFormatter.ofPattern("HH:mm");

        return logs.stream()
                .flatMap(log ->
                        log.getModificacoes().stream()
                                .map(m -> new AuditoriaResponseDto(
                                        tarefa,
                                        log.getResponsavel(),
                                        m,
                                        log.getCriadoEm().format(d),
                                        log.getCriadoEm().format(h)
                                ))
                ).toList();
    }

    public List<AuditoriaResponseDto> listarTodos() {
        List<AuditoriaLog> logs = auditoriaLogRepository.findAll();

        ResponseEntity<List<TarefaDto>> response = tarefaClient.listarTarefas();
        List<TarefaDto> tarefas = response.getBody() != null ? response.getBody() : List.of();

        DateTimeFormatter d = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter h = DateTimeFormatter.ofPattern("HH:mm");

        return logs.stream()
                .flatMap(log ->
                        log.getModificacoes().stream()
                                .map(m -> new AuditoriaResponseDto(
                                        tarefas.stream()
                                                .filter(t -> t.getId().equals(log.getTarefaId()))
                                                .findFirst()
                                                .orElse(null),
                                        log.getResponsavel(),
                                        m,
                                        log.getCriadoEm().format(d),
                                        log.getCriadoEm().format(h)
                                ))
                ).toList();
    }
}
