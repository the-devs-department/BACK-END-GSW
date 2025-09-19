package com.gsw.taskmanager.service;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gsw.taskmanager.entity.Tarefa;

@Service
public class FileService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AnexoService anexoService;

    public ResponseEntity<Resource> criarResponseParaDownload(String tarefaId, String anexoId, HttpServletRequest request) {
        Tarefa.Anexo anexo = anexoService.buscarAnexoPorId(tarefaId, anexoId);
        
        String fileName = extrairNomeArquivoDaUrl(anexo.getUrl());
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        
        String contentType = determinarContentType(resource, request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + anexo.getNome() + "\"")
                .body(resource);
    }

    public String gerarUrlDownload(String tarefaId, String anexoId) {
        return "/tarefas/" + tarefaId + "/anexos/" + anexoId + "/arquivo/download";
    }

    private String extrairNomeArquivoDaUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL do anexo não pode ser vazia");
        }
        
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            return url.substring(lastSlashIndex + 1);
        }
        
        return url;
    }

    private String determinarContentType(Resource resource, HttpServletRequest request) {
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return contentType;
    }
}