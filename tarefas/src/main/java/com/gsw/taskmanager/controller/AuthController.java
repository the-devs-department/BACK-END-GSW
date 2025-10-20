package com.gsw.taskmanager.controller;

import com.gsw.taskmanager.dto.LoginRequest;
import com.gsw.taskmanager.dto.TokenResponse;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.exception.BusinessException;
import com.gsw.taskmanager.service.JwtService;
import com.gsw.taskmanager.service.TokenService;
import com.gsw.taskmanager.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse token = usuarioService.autenticar(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String header) {
        String token = header.replace("Bearer ", "");
        LocalDateTime expiry = jwtService.getExpiry(token);
        tokenService.revoke(token, expiry);
        return ResponseEntity.noContent().build();
    }
}
