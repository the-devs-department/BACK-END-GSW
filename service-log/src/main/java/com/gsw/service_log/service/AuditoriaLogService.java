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

    // @Autowired
    // private TarefaRepository tarefaRepository;

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

    public AuditoriaLog registrarCriacao(TarefaDto tarefaNova){
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();
        String descricao = "Tarefa criada com título '" + tarefaNova.getTitulo() + "'.";
        CategoriaModificacao categoria = CategoriaModificacao.CRIACAO;

        modificacoes.add(new ModificacaoLogDto(categoria, descricao));
        return auditoriaLogRepository.save(criarLog(tarefaNova.getId(), modificacoes));
    }

    public void registrarAtualizacao(TarefaDto tarefaAntiga, TarefaDto tarefaNova) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();

        modificacoes.addAll(visualizadorDeMudancas(tarefaAntiga, tarefaNova));

        modificacoes.addAll(visualizadorDeAnexos(tarefaAntiga.getAnexos(), tarefaNova.getAnexos()));

        if (modificacoes.isEmpty()) {
            return;
        }

        auditoriaLogRepository.save(criarLog(tarefaNova.getId(), modificacoes));
    }

    // ------------------------- EXCLUSÃO -------------------------

    public void registrarExclusao(TarefaDto tarefa){
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();
        String descricao = "Tarefa removida (soft delete)";
        CategoriaModificacao categoria = CategoriaModificacao.EXCLUSAO;
        modificacoes.add(new ModificacaoLogDto(categoria, descricao));

        auditoriaLogRepository.save(criarLog(tarefa.getId(), modificacoes));
    }

    // ------------------------- ATRIBUIÇÃO -----------------------
    public void registrarAtribuicao(TarefaDto tarefaAntiga, String usuarioAnterior, String usuarioNovo) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();
        String descricao = "Responsável alterado de '" + usuarioAnterior + "' para '" + usuarioNovo + "'.";
        CategoriaModificacao categoria = CategoriaModificacao.EDICAO;

        modificacoes.add(new ModificacaoLogDto(categoria, descricao));
        auditoriaLogRepository.save(criarLog(tarefaAntiga.getId(), modificacoes));
    }

    private List<ModificacaoLogDto> visualizadorDeMudancas(TarefaDto tarefaAntiga, TarefaDto tarefaNova) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();

        if (tarefaAntiga == null || tarefaNova == null) return modificacoes;

        Field[] fields = TarefaDto.class.getDeclaredFields();

        for (Field field : fields) {
            if (ignoredFields.contains(field.getName())) continue;

            field.setAccessible(true);
            try {
                Object valorAntigo = field.get(tarefaAntiga);
                Object valorNovo = field.get(tarefaNova);

                if (!Objects.equals(valorAntigo, valorNovo)) {
                    String nomeCampo = fieldLabels.getOrDefault(field.getName(), field.getName());
                    String descricao = nomeCampo + " alterado de '" + valorAntigo + "' para '" + valorNovo + "'.";
                    CategoriaModificacao categoria = CategoriaModificacao.EDICAO;
                    modificacoes.add(new ModificacaoLogDto(categoria, descricao));
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return modificacoes;
    }

    private List<ModificacaoLogDto> visualizadorDeAnexos(List<AnexoDto> anexosAntigos, List<AnexoDto> anexosNovos) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();

        Map<String, AnexoDto> antigosMap = new HashMap<>();
        for (AnexoDto anexo : anexosAntigos) antigosMap.put(anexo.getId(), anexo);

        Map<String, AnexoDto> novosMap = new HashMap<>();
        for (AnexoDto anexo : anexosNovos) novosMap.put(anexo.getId(), anexo);

        for (AnexoDto novo : anexosNovos) {
            if (!antigosMap.containsKey(novo.getId())) {
                String descricao = "Anexo '" + novo.getNome() + "' adicionado.";
                CategoriaModificacao categoria = CategoriaModificacao.EDICAO;
                modificacoes.add(new ModificacaoLogDto(categoria, descricao));
            }
        }

        for (AnexoDto antigo : anexosAntigos) {
            if (!novosMap.containsKey(antigo.getId())) {
                String descricao = "Anexo '" + antigo.getNome() + "' removido.";
                CategoriaModificacao categoria = CategoriaModificacao.EXCLUSAO;
                modificacoes.add(new ModificacaoLogDto(categoria, descricao));
            }
        }

        for (AnexoDto novo : anexosNovos) {
            AnexoDto antigo = antigosMap.get(novo.getId());
            if (antigo != null) {
                if (!Objects.equals(antigo.getNome(), novo.getNome())) {
                    String descricao = "Anexo '" + antigo.getNome() + "' alterado: nome de '" + antigo.getNome() + "' para '" + novo.getNome() + "'.";
                    CategoriaModificacao categoria = CategoriaModificacao.EDICAO;
                    modificacoes.add(new ModificacaoLogDto(categoria, descricao));
                }
                if (!Objects.equals(antigo.getTipo(), novo.getTipo())) {
                    String descricao = "Anexo '" + antigo.getNome() + "' alterado: tipo de '" + antigo.getTipo() + "' para '" + novo.getTipo() + "'.";
                    CategoriaModificacao categoria = CategoriaModificacao.EDICAO;
                    modificacoes.add(new ModificacaoLogDto(categoria, descricao));
                }
            }
        }
        return modificacoes;
    }

    private AuditoriaLog criarLog(String tarefaId, List<ModificacaoLogDto> modificacoes) {
        UsuarioDto usuario = obterUsuarioAutenticado();

        AuditoriaLog log = new AuditoriaLog();

        log.setTarefaId(tarefaId);
        log.setResponsavel(new ResponsavelAlteracaoDto(usuario.getId(), usuario.getEmail()));
        log.setCriadoEm(LocalDateTime.now());
        log.setModificacoes(modificacoes);

        return log;
    }

    private UsuarioDto obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UsuarioDto) {
            return (UsuarioDto) principal;
        } else {
            throw new BusinessException("Usuário autenticado inválido");
        }
    }

    public List<AuditoriaResponseDto> listarPorTarefaId(String tarefaId) {
        List<AuditoriaLog> logs = auditoriaLogRepository.findAllByTarefaId(tarefaId);
        ResponseEntity<TarefaDto> response = tarefaClient.buscarPorId(tarefaId);
        if (response.getStatusCode() != HttpStatus.OK && response.getBody() == null) {
            throw new BusinessException("Tarefa com ID " + tarefaId + " não encontrada ou serviço indisponível. Status: " + response.getStatusCode());
        } 
        TarefaDto tarefa = response.getBody();
        Optional.of(tarefa).filter(TarefaDto::isAtivo)
        .orElseThrow(() -> new BusinessException("Tarefa não encontrada ou inativa."));
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        List<AuditoriaResponseDto> modificacoes = logs.stream()
                .flatMap(log -> log.getModificacoes().stream()
                        .map(modificacao -> new AuditoriaResponseDto(
                                tarefa,
                                log.getResponsavel(),
                                modificacao,
                                log.getCriadoEm().format(formatoData),
                                log.getCriadoEm().format(formatoHora)
                        ))
                ).toList();
        return modificacoes;
    }

    public List<AuditoriaResponseDto> listarTodos() {
        List<AuditoriaLog> logs = auditoriaLogRepository.findAll();
        ResponseEntity<List<TarefaDto>> response = tarefaClient.listarTarefas();
        List<TarefaDto> tarefas;
        if (response.getStatusCode() != HttpStatus.OK && response.getBody() == null) {
           tarefas = response.getBody();
        } else {
            tarefas = Collections.emptyList();
        }
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        return logs.stream()
                .flatMap(log -> log.getModificacoes().stream()
                        .map(modificacao -> {
                            TarefaDto tarefa = tarefas.stream()
                                    .filter(t -> t.getId().equals(log.getTarefaId()))
                                    .findFirst()
                                    .orElse(null);

                            return new AuditoriaResponseDto(
                                    tarefa,
                                    log.getResponsavel(),
                                    modificacao,
                                    log.getCriadoEm().format(formatoData),
                                    log.getCriadoEm().format(formatoHora)
                            );
                        })
                )
                .toList();
    }
}
