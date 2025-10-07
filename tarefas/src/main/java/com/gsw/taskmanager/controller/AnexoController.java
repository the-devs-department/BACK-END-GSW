package com.gsw.taskmanager.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gsw.taskmanager.dto.AnexoDto;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.service.AnexoService;
import com.gsw.taskmanager.service.FileService;

@RestController
@RequestMapping("/tarefas")
public class AnexoController {

    @Autowired
    private AnexoService anexoService;

    @Autowired
    private FileService fileService;

    // listar anexos de uma tarefa
    @GetMapping("/{tarefaId}/anexos")
    public ResponseEntity<List<Tarefa.Anexo>> listarAnexos(@PathVariable String tarefaId) {
        List<Tarefa.Anexo> anexos = anexoService.listarAnexosDaTarefa(tarefaId);
        return ResponseEntity.ok(anexos);
    }

    // buscar por id
    @GetMapping("/{tarefaId}/anexos/{anexoId}")
    public ResponseEntity<Tarefa.Anexo> buscarAnexo(
            @PathVariable String tarefaId,
            @PathVariable String anexoId) {
        
        Tarefa.Anexo anexo = anexoService.buscarAnexoPorId(tarefaId, anexoId);
        return ResponseEntity.ok(anexo);
    }

    // adicionar anexo
    @PostMapping("/{tarefaId}/anexos")
    public ResponseEntity<Tarefa.Anexo> adicionarAnexo(
            @PathVariable String tarefaId,
            @RequestBody AnexoDto anexoRequest) {
        
        Tarefa.Anexo novoAnexo = anexoService.adicionarAnexoNaTarefa(tarefaId, anexoRequest);
        return ResponseEntity.status(201).body(novoAnexo);
    }

    // adicionar anexo com upload de arquivo
    @PostMapping("/{tarefaId}/anexos/upload")
    public ResponseEntity<Tarefa.Anexo> adicionarAnexoComUpload(
            @PathVariable String tarefaId,
            @RequestParam("arquivo") MultipartFile arquivo) {
        
        String usuarioId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Tarefa.Anexo novoAnexo = anexoService.adicionarAnexoComArquivo(tarefaId, usuarioId, arquivo);
        return ResponseEntity.status(201).body(novoAnexo);
    }

    // atualizar anexo
    @PutMapping("/{tarefaId}/anexos/{anexoId}")
    public ResponseEntity<Tarefa.Anexo> atualizarAnexo(
            @PathVariable String tarefaId,
            @PathVariable String anexoId,
            @RequestBody AnexoDto anexoRequest) {
        
        Tarefa.Anexo anexoAtualizado = anexoService.atualizarAnexo(tarefaId, anexoId, anexoRequest);
        return ResponseEntity.ok(anexoAtualizado);
    }

    // remover anexo
    @DeleteMapping("/{tarefaId}/anexos/{anexoId}")
    public ResponseEntity<Void> removerAnexo(
            @PathVariable String tarefaId,
            @PathVariable String anexoId) {
        
        anexoService.removerAnexo(tarefaId, anexoId);
        return ResponseEntity.noContent().build();
    }

    // obter URL de download do anexo
    @GetMapping("/{tarefaId}/anexos/{anexoId}/download")
    public ResponseEntity<String> obterUrlDownload(
            @PathVariable String tarefaId,
            @PathVariable String anexoId) {
        
        String downloadUrl = fileService.gerarUrlDownload(tarefaId, anexoId);
        return ResponseEntity.ok(downloadUrl);
    }

    // baixar arquivo do anexo
    @GetMapping("/{tarefaId}/anexos/{anexoId}/arquivo/download")
    public ResponseEntity<Resource> baixarArquivoAnexo(
            @PathVariable String tarefaId, 
            @PathVariable String anexoId, 
            HttpServletRequest request) {

        return fileService.criarResponseParaDownload(tarefaId, anexoId, request);
    }
}