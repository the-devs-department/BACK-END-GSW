package com.gsw.taskmanager.service;

import com.gsw.taskmanager.dto.logs.AuditoriaResponseDto;
import com.gsw.taskmanager.dto.logs.ModificacaoLogDto;
import com.gsw.taskmanager.dto.logs.ResponsavelAlteracaoDto;
import com.gsw.taskmanager.entity.Anexo;
import com.gsw.taskmanager.entity.AuditoriaLog;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.enums.CategoriaModificacao;
import com.gsw.taskmanager.exception.BusinessException;
import com.gsw.taskmanager.repository.AuditoriaLogRepository;
import com.gsw.taskmanager.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();
        String descricao = "Tarefa criada com título '" + tarefaNova.getTitulo() + "'.";
        CategoriaModificacao categoria = CategoriaModificacao.CRIACAO;

        modificacoes.add(new ModificacaoLogDto(categoria, descricao));
        return auditoriaLogRepository.save(criarLog(tarefaNova.getId(), modificacoes));
    }

    public void registrarAtualizacao(Tarefa tarefaAntiga, Tarefa tarefaNova) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();

        modificacoes.addAll(visualizadorDeMudancas(tarefaAntiga, tarefaNova));

        modificacoes.addAll(visualizadorDeAnexos(tarefaAntiga.getAnexos(), tarefaNova.getAnexos()));

        if (modificacoes.isEmpty()) {
            return;
        }

        auditoriaLogRepository.save(criarLog(tarefaNova.getId(), modificacoes));
    }

    // ------------------------- EXCLUSÃO -------------------------

    public void registrarExclusao(Tarefa tarefa){
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();
        String descricao = "Tarefa removida (soft delete)";
        CategoriaModificacao categoria = CategoriaModificacao.EXCLUSAO;
        modificacoes.add(new ModificacaoLogDto(categoria, descricao));

        auditoriaLogRepository.save(criarLog(tarefa.getId(), modificacoes));
    }

    // ------------------------- ATRIBUIÇÃO -----------------------
    public void registrarAtribuicao(Tarefa tarefaAntiga, String usuarioAnterior, String usuarioNovo) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();
        String descricao = "Responsável alterado de '" + usuarioAnterior + "' para '" + usuarioNovo + "'.";
        CategoriaModificacao categoria = CategoriaModificacao.EDICAO;

        modificacoes.add(new ModificacaoLogDto(categoria, descricao));
        auditoriaLogRepository.save(criarLog(tarefaAntiga.getId(), modificacoes));
    }

    private List<ModificacaoLogDto> visualizadorDeMudancas(Tarefa tarefaAntiga, Tarefa tarefaNova) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();

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

    private List<ModificacaoLogDto> visualizadorDeAnexos(List<Anexo> anexosAntigos, List<Anexo> anexosNovos) {
        List<ModificacaoLogDto> modificacoes = new ArrayList<>();

        Map<String, Anexo> antigosMap = new HashMap<>();
        for (Anexo anexo : anexosAntigos) antigosMap.put(anexo.getId(), anexo);

        Map<String, Anexo> novosMap = new HashMap<>();
        for (Anexo anexo : anexosNovos) novosMap.put(anexo.getId(), anexo);

        for (Anexo novo : anexosNovos) {
            if (!antigosMap.containsKey(novo.getId())) {
                String descricao = "Anexo '" + novo.getNome() + "' adicionado.";
                CategoriaModificacao categoria = CategoriaModificacao.EDICAO;
                modificacoes.add(new ModificacaoLogDto(categoria, descricao));
            }
        }

        for (Anexo antigo : anexosAntigos) {
            if (!novosMap.containsKey(antigo.getId())) {
                String descricao = "Anexo '" + antigo.getNome() + "' removido.";
                CategoriaModificacao categoria = CategoriaModificacao.EXCLUSAO;
                modificacoes.add(new ModificacaoLogDto(categoria, descricao));
            }
        }

        for (Anexo novo : anexosNovos) {
            Anexo antigo = antigosMap.get(novo.getId());
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
        Usuario usuario = obterUsuarioAutenticado();

        AuditoriaLog log = new AuditoriaLog();

        log.setTarefaId(tarefaId);
        log.setResponsavel(new ResponsavelAlteracaoDto(usuario.getId(), usuario.getEmail()));
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

        // Formatadores BRL
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
        List<Tarefa> tarefas = tarefaRepository.findAll();

        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        return logs.stream()
                .flatMap(log -> log.getModificacoes().stream()
                        .map(modificacao -> {
                            Tarefa tarefa = tarefas.stream()
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
