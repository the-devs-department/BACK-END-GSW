package com.gsw.taskmanager.controller;

import com.gsw.taskmanager.dto.auth.LoginRequest;
import com.gsw.taskmanager.dto.auth.PasswordRequestEmailDto;
import com.gsw.taskmanager.dto.auth.PasswordResetRequest;
import com.gsw.taskmanager.dto.auth.TokenResponse;
import com.gsw.taskmanager.service.PasswordResetService;
import com.gsw.taskmanager.service.JwtService;
import com.gsw.taskmanager.service.TokenService;
import com.gsw.taskmanager.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse token = jwtService.autenticar(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String header) {
        String token = header.replace("Bearer ", "");
        LocalDateTime expiry = jwtService.getExpiry(token);
        tokenService.revoke(token, expiry);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/recuperar-senha")
    public ResponseEntity<Void> recuperarSenha(
            @RequestBody @Valid PasswordRequestEmailDto email) {
        String codigo = passwordResetService.recuperarSenha(email.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetar-senha/{token}")
    public ResponseEntity<String> resetarSenha(
            @RequestBody @Valid PasswordResetRequest novaSenha,
            @PathVariable String token) {
        passwordResetService.resetarSenha(novaSenha.novaSenha(), token);
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }

    @GetMapping("/resetar-senha/validar/{token}")
    public ResponseEntity<String> validarSenha(@PathVariable String token) {
        String email = passwordResetService.validarToken(token);
        return ResponseEntity.ok(email.replaceAll("(?<=.{2}).(?=.*@)", "*"));
    }
}