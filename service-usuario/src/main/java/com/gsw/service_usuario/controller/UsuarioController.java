package com.gsw.service_usuario.controller;

import java.util.List;

import com.gsw.service_usuario.config.UpdatePasswordDTO;
import com.gsw.service_usuario.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/buscar-usuario-email")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail(@RequestParam String email) {
        Usuario userFound = usuarioService.buscarUsuarioPorEmail(email);
        return  ResponseEntity.ok(userFound);
    }

    // CRIAR USUÁRIO
    @PostMapping("/criar")
    public ResponseEntity<UsuarioResponseDto> criarUsuario(@RequestBody @Valid CriacaoUsuarioDto usuario) {
        UsuarioResponseDto novoUsuario = usuarioService.criarUsuario(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    @PutMapping("/atualizar-senha")
    public ResponseEntity<String> atualizarSenhaUsuario(@RequestBody UpdatePasswordDTO atualizarSenhaDto) {
        usuarioService.atualizarSenha(atualizarSenhaDto.getEmail(), atualizarSenhaDto.getSenha());
        return ResponseEntity.ok("Senha atualizada com sucesso.");
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