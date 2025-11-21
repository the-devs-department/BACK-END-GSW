package com.gsw.service_anexo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import com.gsw.service_anexo.entity.Anexo;
import com.gsw.service_anexo.enums.TipoAnexo;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gsw.service_anexo.client.TarefaClient;
import com.gsw.service_anexo.dto.AnexoDto;
import com.gsw.service_anexo.dto.tarefa.TarefaDto;
import com.gsw.service_anexo.dto.usuario.UsuarioDto;
import com.gsw.service_anexo.exceptions.AcessoNegadoAnexoException;
import com.gsw.service_anexo.exceptions.AnexoNaoEncontradoException;
import com.gsw.service_anexo.exceptions.BusinessException;
import com.gsw.service_anexo.exceptions.LimiteAnexosExcedidoException;
import com.gsw.service_anexo.exceptions.TarefaNaoEncontradaException;
import com.gsw.service_anexo.exceptions.UploadNaoPermitidoException;
import com.gsw.service_anexo.exceptions.TipoAnexoInvalidoException;

@Service
public class AnexoService {

    private static final long LIMITE_TOTAL_ANEXOS_MB = 20;
    private static final long LIMITE_TOTAL_ANEXOS_BYTES = LIMITE_TOTAL_ANEXOS_MB * 1024 * 1024;

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired TarefaClient tarefaClient;

    public AnexoDto adicionarAnexo(String tarefaId, String usuarioId, MultipartFile arquivo) {
        ResponseEntity<TarefaDto> response = tarefaClient.fetchTaskById(tarefaId);
        if (response.getStatusCode() != HttpStatus.OK && response.getBody() == null) {
            throw new BusinessException("Tarefa com ID " + tarefaId + " não encontrada ou serviço indisponível. Status: " + response.getStatusCode());
        }
        TarefaDto tarefa = response.getBody();
         Optional.of(tarefa).orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

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

        AnexoDto novoAnexo = new AnexoDto();
        novoAnexo.setId(new ObjectId().toString());
        novoAnexo.setUsuarioId(usuarioId);
        novoAnexo.setTarefaId(tarefaId);
        novoAnexo.setDataUpload(LocalDateTime.now());
        novoAnexo.setTipo(tipoAnexo);
        novoAnexo.setUrl(nomeArquivoArmazenado);
        novoAnexo.setTamanho(arquivo.getSize());
        novoAnexo.setNome(arquivo.getOriginalFilename());

        tarefa.getAnexos().add(novoAnexo);
        tarefaClient.atualizarTarefa(tarefaId, tarefa);

        return novoAnexo;
    }

    public List<AnexoDto> listarAnexosDaTarefa(String tarefaId) {
        ResponseEntity<TarefaDto> response = tarefaClient.fetchTaskById(tarefaId);
        if (response.getStatusCode() != HttpStatus.OK && response.getBody() == null) {
            throw new BusinessException("Tarefa com ID " + tarefaId + " não encontrada ou serviço indisponível. Status: " + response.getStatusCode());
        }
        TarefaDto tarefa = response.getBody();
         Optional.of(tarefa).orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        return tarefa.getAnexos();
    }

    public AnexoDto buscarAnexoPorId(String tarefaId, String anexoId) {
        ResponseEntity<TarefaDto> response = tarefaClient.fetchTaskById(tarefaId);
        if (response.getStatusCode() != HttpStatus.OK && response.getBody() == null) {
            throw new BusinessException("Tarefa com ID " + tarefaId + " não encontrada ou serviço indisponível. Status: " + response.getStatusCode());
        }
        TarefaDto tarefa = response.getBody();
        Optional.of(tarefa).orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        return tarefa.getAnexos().stream()
                .filter(anexo -> anexo.getId().equals(anexoId))
                .findFirst()
                .orElseThrow(() -> new AnexoNaoEncontradoException("Anexo não encontrado"));
    }

    public AnexoDto atualizarAnexo(String tarefaId, String anexoId, AnexoDto anexoDto) {
        ResponseEntity<TarefaDto> response = tarefaClient.fetchTaskById(tarefaId);
        if (response.getStatusCode() != HttpStatus.OK && response.getBody() == null) {
            throw new BusinessException("Tarefa com ID " + tarefaId + " não encontrada ou serviço indisponível. Status: " + response.getStatusCode());
        }
        TarefaDto tarefa = response.getBody();
        Optional.of(tarefa).orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));

        AnexoDto anexoEncontrado = tarefa.getAnexos().stream()
                .filter(anexo -> anexo.getId().equals(anexoId))
                .findFirst()
                .orElseThrow(() -> new AnexoNaoEncontradoException("Anexo não encontrado"));

        if (anexoDto.getNome() != null) {
            anexoEncontrado.setNome(anexoDto.getNome());
        }
        if (anexoDto.getTipo() != null) {
            anexoEncontrado.setTipo(anexoDto.getTipo());
        }

        tarefaClient.atualizarTarefa(tarefaId, tarefa);
        return anexoEncontrado;
    }

    public void removerAnexo(String tarefaId, String anexoId) {

        UsuarioDto usuarioAutenticado = (UsuarioDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usuarioAtual = usuarioAutenticado.getId();
        
         ResponseEntity<TarefaDto> response = tarefaClient.fetchTaskById(tarefaId);
        if (response.getStatusCode() != HttpStatus.OK && response.getBody() == null) {
            throw new BusinessException("Tarefa com ID " + tarefaId + " não encontrada ou serviço indisponível. Status: " + response.getStatusCode());
        }
        TarefaDto tarefa = response.getBody();
        Optional.of(tarefa).orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada"));


        AnexoDto anexo = tarefa.getAnexos().stream()
                .filter(a -> a.getId().equals(anexoId))
                .findFirst()
                .orElseThrow(() -> new AnexoNaoEncontradoException("Attachment not found"));


        validarPermissaoExclusao(anexo, tarefa, usuarioAtual);

        if (anexo.getUrl() != null && !anexo.getUrl().isEmpty()) {
            fileStorageService.deleteFile(anexo.getUrl());
        }

        boolean removido = tarefa.getAnexos().removeIf(a -> a.getId().equals(anexoId));

        if (!removido) {
            throw new AnexoNaoEncontradoException("Attachment not found");
        }

        tarefaClient.atualizarTarefa(tarefaId, tarefa);
    }

    public void validarLimiteAnexos(TarefaDto tarefa, Long novoTamanho) {
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

    private void validarPermissaoExclusao(AnexoDto anexo, TarefaDto tarefa, String usuarioAtual) {
        boolean podeExcluir = anexo.getUsuarioId().equals(usuarioAtual) || 
                             tarefa.getResponsavel().getId().equals(usuarioAtual);
        if (!podeExcluir) {
            throw new AcessoNegadoAnexoException("Você não tem permissão para excluir este anexo. " +
                "Apenas o usuário que enviou o anexo ou o criador da tarefa podem excluí-lo.");
        }
    }

    public long calcularTamanhoTotalAnexos(TarefaDto tarefa) {
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
