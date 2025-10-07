package com.gsw.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsw.taskmanager.dto.AnexoDto;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.exception.anexo.AcessoNegadoAnexoException;
import com.gsw.taskmanager.exception.anexo.AnexoNaoEncontradoException;
import com.gsw.taskmanager.exception.anexo.LimiteAnexosExcedidoException;
import com.gsw.taskmanager.exception.anexo.TarefaNaoEncontradaException;
import com.gsw.taskmanager.exception.anexo.TipoAnexoInvalidoException;
import com.gsw.taskmanager.repository.TarefaRepository;

@Service
public class AnexoService {

    private static final long LIMITE_TOTAL_ANEXOS_MB = 20;
    private static final long LIMITE_TOTAL_ANEXOS_BYTES = LIMITE_TOTAL_ANEXOS_MB * 1024 * 1024;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public Tarefa.Anexo adicionarAnexoNaTarefa(String tarefaId, AnexoDto anexoDto) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        validarLimiteAnexos(tarefa, anexoDto.getTamanho());

        if (anexoDto.getUrl() != null && !anexoDto.getUrl().isEmpty()) {
            validarTipoArquivo(anexoDto.getUrl(), anexoDto.getTipo());
        }

        Tarefa.Anexo novoAnexo = Tarefa.Anexo.builder()
                .id(new ObjectId().toString())
                .tarefaId(tarefaId)
                .usuarioId(anexoDto.getUsuarioId())
                .nome(anexoDto.getNome())
                .tipo(anexoDto.getTipo())
                .url(anexoDto.getUrl())
                .tamanho(anexoDto.getTamanho())
                .dataUpload(LocalDateTime.now())
                .build();

        tarefa.getAnexos().add(novoAnexo);
        tarefaRepository.save(tarefa);

        return novoAnexo;
    }

    public Tarefa.Anexo adicionarAnexoComArquivo(String tarefaId, String usuarioId, MultipartFile arquivo) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        validarLimiteAnexos(tarefa, arquivo.getSize());

        Tarefa.TipoAnexo tipoAnexo = determinarTipoAnexo(arquivo.getContentType());
        if (tipoAnexo == null) {
            throw new TipoAnexoInvalidoException(arquivo.getContentType(), arquivo.getOriginalFilename());
        }

        String nomeArquivoArmazenado = fileStorageService.storeFile(arquivo);

        // Criar o anexo
        Tarefa.Anexo novoAnexo = Tarefa.Anexo.builder()
                .id(new ObjectId().toString())
                .tarefaId(tarefaId)
                .usuarioId(usuarioId)
                .nome(arquivo.getOriginalFilename())
                .tipo(tipoAnexo)
                .url(nomeArquivoArmazenado)
                .tamanho(arquivo.getSize())
                .dataUpload(LocalDateTime.now())
                .build();

        tarefa.getAnexos().add(novoAnexo);
        tarefaRepository.save(tarefa);

        return novoAnexo;
    }

    public List<Tarefa.Anexo> listarAnexosDaTarefa(String tarefaId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        return tarefa.getAnexos();
    }

    public Tarefa.Anexo buscarAnexoPorId(String tarefaId, String anexoId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        return tarefa.getAnexos().stream()
                .filter(anexo -> anexo.getId().equals(anexoId))
                .findFirst()
                .orElseThrow(() -> new AnexoNaoEncontradoException("Anexo não encontrado"));
    }

    public Tarefa.Anexo atualizarAnexo(String tarefaId, String anexoId, AnexoDto anexoDto) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        Tarefa.Anexo anexoEncontrado = tarefa.getAnexos().stream()
                .filter(anexo -> anexo.getId().equals(anexoId))
                .findFirst()
                .orElseThrow(() -> new AnexoNaoEncontradoException("Anexo não encontrado"));

        if (anexoDto.getNome() != null) {
            anexoEncontrado.setNome(anexoDto.getNome());
        }
        if (anexoDto.getTipo() != null) {
            anexoEncontrado.setTipo(anexoDto.getTipo());
        }
        if (anexoDto.getUsuarioId() != null) {
            anexoEncontrado.setUsuarioId(anexoDto.getUsuarioId());
        }

        tarefaRepository.save(tarefa);
        return anexoEncontrado;
    }

    public void removerAnexo(String tarefaId, String anexoId) {

        String usuarioAtual = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));


        Tarefa.Anexo anexo = tarefa.getAnexos().stream()
                .filter(a -> a.getId().equals(anexoId))
                .findFirst()
                .orElseThrow(() -> new AnexoNaoEncontradoException("Attachment not found"));


        validarPermissaoExclusao(anexo, tarefa, usuarioAtual);

        // Remover arquivo físico
        if (anexo.getUrl() != null && !anexo.getUrl().isEmpty()) {
            fileStorageService.deleteFile(anexo.getUrl());
        }

        boolean removido = tarefa.getAnexos().removeIf(a -> a.getId().equals(anexoId));

        if (!removido) {
            throw new AnexoNaoEncontradoException("Attachment not found");
        }

        tarefaRepository.save(tarefa);
    }

    public void validarLimiteAnexos(Tarefa tarefa, Long novoTamanho) {
        long tamanhoTotalAtual = calcularTamanhoTotalAnexos(tarefa);

        if (tamanhoTotalAtual + novoTamanho > LIMITE_TOTAL_ANEXOS_BYTES) {
            long limiteMB = LIMITE_TOTAL_ANEXOS_MB;
            long tamanhoAtualMB = tamanhoTotalAtual / (1024 * 1024);
            long novoTamanhoMB = novoTamanho / (1024 * 1024);

            throw new LimiteAnexosExcedidoException(tamanhoAtualMB, novoTamanhoMB, limiteMB);
        }
    }

    public void validarTipoArquivo(String caminhoArquivo, Tarefa.TipoAnexo tipoEsperado) {
        try {
            String mimeTypeDetectado = Files.probeContentType(Paths.get(caminhoArquivo));
            
            if (mimeTypeDetectado == null) {
                return; 
            }
            
            if (!mimeTypeDetectado.equalsIgnoreCase(tipoEsperado.getMimeType())) {
                throw new TipoAnexoInvalidoException(mimeTypeDetectado, caminhoArquivo);
            }
            
        } catch (IOException e) {
            return;
        }
    }

    private void validarPermissaoExclusao(Tarefa.Anexo anexo, Tarefa tarefa, String usuarioAtual) {
        boolean podeExcluir = anexo.getUsuarioId().equals(usuarioAtual) || 
                             tarefa.getResponsavel().equals(usuarioAtual);
        
        if (!podeExcluir) {
            throw new AcessoNegadoAnexoException("Você não tem permissão para excluir este anexo. " +
                "Apenas o usuário que enviou o anexo ou o criador da tarefa podem excluí-lo.");
        }
    }

    public boolean isTipoSuportado(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        
        for (Tarefa.TipoAnexo tipo : Tarefa.TipoAnexo.values()) {
            if (tipo.getMimeType().equalsIgnoreCase(mimeType)) {
                return true;
            }
        }
        return false;
    }

    public long calcularTamanhoTotalAnexos(Tarefa tarefa) {
        if (tarefa.getAnexos() == null || tarefa.getAnexos().isEmpty()) {
            return 0L;
        }

        return tarefa.getAnexos().stream()
                .mapToLong(anexo -> anexo.getTamanho() != null ? anexo.getTamanho() : 0L)
                .sum();
    }

    public long calcularEspacoDisponivelMB(Tarefa tarefa) {
        long tamanhoTotalAtual = calcularTamanhoTotalAnexos(tarefa);
        long espacoDisponivel = LIMITE_TOTAL_ANEXOS_BYTES - tamanhoTotalAtual;
        return espacoDisponivel / (1024 * 1024); // Retornar em MB
    }

    public long calcularEspacoDisponivelMBPorId(String tarefaId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));
        return calcularEspacoDisponivelMB(tarefa);
    }

    private Tarefa.TipoAnexo determinarTipoAnexo(String contentType) {
        if (contentType == null) {
            return null;
        }

        for (Tarefa.TipoAnexo tipo : Tarefa.TipoAnexo.values()) {
            if (tipo.getMimeType().equalsIgnoreCase(contentType)) {
                return tipo;
            }
        }
        return null;
    }
}
