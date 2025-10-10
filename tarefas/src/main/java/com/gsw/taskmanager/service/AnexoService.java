package com.gsw.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import com.gsw.taskmanager.entity.Anexo;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.enums.TipoAnexo;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsw.taskmanager.dto.AnexoDto;
import com.gsw.taskmanager.exception.anexo.AcessoNegadoAnexoException;
import com.gsw.taskmanager.exception.anexo.AnexoNaoEncontradoException;
import com.gsw.taskmanager.exception.anexo.LimiteAnexosExcedidoException;
import com.gsw.taskmanager.exception.anexo.TarefaNaoEncontradaException;
import com.gsw.taskmanager.exception.anexo.UploadNaoPermitidoException;
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

    // TODO REGISTRAR ADIÇÃO DE ANEXO NA TAREFA
    public Anexo adicionarAnexo(String tarefaId, String usuarioId, MultipartFile arquivo) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        if (tarefa.getStatus() == null) {
            throw new UploadNaoPermitidoException(tarefaId, "null");
        }
        if (tarefa.getStatus().name().equalsIgnoreCase("NAO_INICIADA") ||
            tarefa.getStatus().getStatus().equalsIgnoreCase("nao_iniciada")) {
            throw new UploadNaoPermitidoException(tarefaId, tarefa.getStatus().getStatus());
        }

        validarLimiteAnexos(tarefa, arquivo.getSize());

        TipoAnexo tipoAnexo = determinarTipoAnexo(arquivo.getContentType());
        if (tipoAnexo == null) {
            throw new TipoAnexoInvalidoException(arquivo.getContentType(), arquivo.getOriginalFilename());
        }

        String nomeArquivoArmazenado = fileStorageService.storeFile(arquivo);

        // Criar o anexo
        Anexo novoAnexo = new Anexo();
        novoAnexo.setId(new ObjectId().toString());
        novoAnexo.setUsuarioId(usuarioId);
        novoAnexo.setTarefaId(tarefaId);
        novoAnexo.setDataUpload(LocalDateTime.now());
        novoAnexo.setTipo(tipoAnexo);
        novoAnexo.setUrl(nomeArquivoArmazenado);
        novoAnexo.setTamanho(arquivo.getSize());
        novoAnexo.setNome(arquivo.getOriginalFilename());

        tarefa.getAnexos().add(novoAnexo);
        tarefaRepository.save(tarefa);

        return novoAnexo;
    }

    public List<Anexo> listarAnexosDaTarefa(String tarefaId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        return tarefa.getAnexos();
    }

    public Anexo buscarAnexoPorId(String tarefaId, String anexoId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        return tarefa.getAnexos().stream()
                .filter(anexo -> anexo.getId().equals(anexoId))
                .findFirst()
                .orElseThrow(() -> new AnexoNaoEncontradoException("Anexo não encontrado"));
    }

    // TODO REGISTRAR ALTERAÇÃO DO ANEXO
    public Anexo atualizarAnexo(String tarefaId, String anexoId, AnexoDto anexoDto) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        Anexo anexoEncontrado = tarefa.getAnexos().stream()
                .filter(anexo -> anexo.getId().equals(anexoId))
                .findFirst()
                .orElseThrow(() -> new AnexoNaoEncontradoException("Anexo não encontrado"));

        if (anexoDto.getNome() != null) {
            anexoEncontrado.setNome(anexoDto.getNome());
        }
        if (anexoDto.getTipo() != null) {
            anexoEncontrado.setTipo(anexoDto.getTipo());
        }

        tarefaRepository.save(tarefa);
        return anexoEncontrado;
    }

    // TODO REGISTRAR REMOÇÃO DO ANEXO
    public void removerAnexo(String tarefaId, String anexoId) {

        String usuarioAtual = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));


        Anexo anexo = tarefa.getAnexos().stream()
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

    public void validarTipoArquivo(String caminhoArquivo, TipoAnexo tipoEsperado) {
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

    private void validarPermissaoExclusao(Anexo anexo, Tarefa tarefa, String usuarioAtual) {
        boolean podeExcluir = anexo.getUsuarioId().equals(usuarioAtual) || 
                             tarefa.getResponsavel().equals(usuarioAtual);
        
        if (!podeExcluir) {
            throw new AcessoNegadoAnexoException("Você não tem permissão para excluir este anexo. " +
                "Apenas o usuário que enviou o anexo ou o criador da tarefa podem excluí-lo.");
        }
    }

    public long calcularTamanhoTotalAnexos(Tarefa tarefa) {
        if (tarefa.getAnexos() == null || tarefa.getAnexos().isEmpty()) {
            return 0L;
        }

        return tarefa.getAnexos().stream()
                .mapToLong(anexo -> anexo.getTamanho() != null ? anexo.getTamanho() : 0L)
                .sum();
    }

    private TipoAnexo determinarTipoAnexo(String contentType) {
        if (contentType == null) {
            return null;
        }

        for (TipoAnexo tipo : TipoAnexo.values()) {
            if (tipo.getMimeType().equalsIgnoreCase(contentType)) {
                return tipo;
            }
        }
        return null;
    }
}
