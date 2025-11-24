package com.gsw.api_gateway.controller;

import com.gsw.api_gateway.dto.LoginRequest;
import com.gsw.api_gateway.dto.PasswordRequestEmailDto;
import com.gsw.api_gateway.dto.PasswordResetRequest;
import com.gsw.api_gateway.dto.TokenResponse;
import com.gsw.api_gateway.service.JwtService;
import com.gsw.api_gateway.service.PasswordResetService;
import com.gsw.api_gateway.service.TokenService;
import com.gsw.api_gateway.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        System.out.println(">>> LOGIN: O Controller foi chamado para: " + request.email());

        try {
            TokenResponse token = jwtService.autenticar(request);
            System.out.println(">>> SUCESSO: Token gerado!");
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            System.out.println(">>> ERRO NO LOGIN: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
        passwordResetService.recuperarSenha(email.email());
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