package com.gsw.taskmanager.controller;

import com.gsw.taskmanager.dto.UsuarioAlteracaoDto;
import com.gsw.taskmanager.dto.UsuarioResponseDto;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listarTodos() {
        List<UsuarioResponseDto> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorId(@PathVariable String id) {
        UsuarioResponseDto usuario = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    // CRIAR USUÁRIO
    @PostMapping("/criar")
    public ResponseEntity<UsuarioResponseDto> criarUsuario(@RequestBody @Valid Usuario usuario) {
        UsuarioResponseDto novoUsuario = usuarioService.criarUsuario(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    //TODO ENDPOINT /UPDATE
    @PutMapping("/atualizar")
    public ResponseEntity<UsuarioResponseDto> atualizarUsuario(@RequestBody @Valid UsuarioAlteracaoDto usuario) {
        return ResponseEntity.ok(usuarioService.atualizarUsuario(usuario));
    }

    //TODO ENDPOINT /DELETE_BY_ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable String id) {
        usuarioService.deletarById(id);
        return ResponseEntity.noContent().build();
    }
}