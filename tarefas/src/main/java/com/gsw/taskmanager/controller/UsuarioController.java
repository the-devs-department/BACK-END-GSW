package com.gsw.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gsw.taskmanager.dto.UsuarioAlteracaoDto;
import com.gsw.taskmanager.dto.UsuarioResponseDto;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.service.UsuarioService;

import jakarta.validation.Valid;

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
// eu to segurando
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