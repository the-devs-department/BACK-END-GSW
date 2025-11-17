package com.gsw.service_usuario.controller;

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

import com.gsw.service_usuario.dto.CriacaoUsuarioDto;
import com.gsw.service_usuario.dto.auth.LoginResponseDto;
import com.gsw.service_usuario.dto.UsuarioResponseDto;
import com.gsw.service_usuario.service.UsuarioService;
import com.gsw.service_usuario.dto.UsuarioAlteracaoEReseponsavel;

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

    @GetMapping("/getOnLogin/{id}")
    public ResponseEntity<LoginResponseDto> buscarUsuarioAoLogar(@PathVariable String id){
        LoginResponseDto usuario = usuarioService.buscarUsuarioAoLogar(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/assignedUser/{email}")
    public ResponseEntity<UsuarioAlteracaoEReseponsavel> buscarUsuarioResponsavelTarefa(@PathVariable String email) {
        UsuarioAlteracaoEReseponsavel usuario = usuarioService.buscarUsuarioResponsavelTarefa(email);
        return ResponseEntity.ok(usuario);
    }

    // CRIAR USUÁRIO
    @PostMapping("/criar")
    public ResponseEntity<UsuarioResponseDto> criarUsuario(@RequestBody @Valid CriacaoUsuarioDto usuario) {
        UsuarioResponseDto novoUsuario = usuarioService.criarUsuario(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<UsuarioResponseDto> atualizarUsuario(@RequestBody @Valid UsuarioAlteracaoEReseponsavel usuario) {
        return ResponseEntity.ok(usuarioService.atualizarUsuario(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable String id) {
        usuarioService.deletarById(id);
        return ResponseEntity.noContent().build();
    }
}