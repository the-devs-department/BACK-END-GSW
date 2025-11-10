package com.gsw.service_anexo.controller;

import java.util.List;

import com.gsw.service_anexo.entity.Anexo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gsw.service_anexo.dto.AnexoDto;
import com.gsw.service_anexo.service.AnexoService;
import com.gsw.service_anexo.service.FileService;

@RestController
@RequestMapping("/tarefas")
public class AnexoController {

    @Autowired
    private AnexoService anexoService;

    @Autowired
    private FileService fileService;

    @GetMapping("/{tarefaId}/anexos")
    public ResponseEntity<List<Anexo>> listarAnexos(@PathVariable String tarefaId) {
        List<Anexo> anexos = anexoService.listarAnexosDaTarefa(tarefaId);
        return ResponseEntity.ok(anexos);
    }

    @GetMapping("/{tarefaId}/anexos/{anexoId}")
    public ResponseEntity<Anexo> buscarAnexo(
            @PathVariable String tarefaId,
            @PathVariable String anexoId) {
        
        Anexo anexo = anexoService.buscarAnexoPorId(tarefaId, anexoId);
        return ResponseEntity.ok(anexo);
    }

    @PostMapping("/{tarefaId}/anexos/upload")
    public ResponseEntity<Anexo> adicionarAnexoComUpload(
            @PathVariable String tarefaId,
            @RequestParam("arquivo") MultipartFile arquivo) {
        
        Usuario usuarioAutenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usuarioId = usuarioAutenticado.getId();
        
        Anexo novoAnexo = anexoService.adicionarAnexo(tarefaId, usuarioId, arquivo);
        return ResponseEntity.status(201).body(novoAnexo);
    }

    @PutMapping("/{tarefaId}/anexos/{anexoId}")
    public ResponseEntity<Anexo> atualizarAnexo(
            @PathVariable String tarefaId,
            @PathVariable String anexoId,
            @Valid @RequestBody AnexoDto anexoRequest) {
        
        Anexo anexoAtualizado = anexoService.atualizarAnexo(tarefaId, anexoId, anexoRequest);
        return ResponseEntity.ok(anexoAtualizado);
    }

    @DeleteMapping("/{tarefaId}/anexos/{anexoId}")
    public ResponseEntity<Void> removerAnexo(
            @PathVariable String tarefaId,
            @PathVariable String anexoId) {
        
        anexoService.removerAnexo(tarefaId, anexoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tarefaId}/anexos/{anexoId}/download")
    public ResponseEntity<String> obterUrlDownload(
            @PathVariable String tarefaId,
            @PathVariable String anexoId) {
        
        String downloadUrl = fileService.gerarUrlDownload(tarefaId, anexoId);
        return ResponseEntity.ok(downloadUrl);
    }

    @GetMapping("/{tarefaId}/anexos/{anexoId}/arquivo/download")
    public ResponseEntity<Resource> baixarArquivoAnexo(
            @PathVariable String tarefaId, 
            @PathVariable String anexoId, 
            HttpServletRequest request) {

        return fileService.criarResponseParaDownload(tarefaId, anexoId, request);
    }
}