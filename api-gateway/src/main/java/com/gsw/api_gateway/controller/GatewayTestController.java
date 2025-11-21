package com.gsw.api_gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller de exemplo para testar o Spring Cloud Gateway
 * 
 * Este controller simula os microservices da aplicação.
 * Em produção, estes endpoints estarão em aplicações separadas.
 * 
 * ROTAS DISPONÍVEIS ATRAVÉS DO GATEWAY (porta 8086):
 * - GET  http://localhost:8086/usuarios/status
 * - GET  http://localhost:8086/tarefas/status
 * - GET  http://localhost:8086/anexos/status
 * - GET  http://localhost:8086/logs/status
 * - GET  http://localhost:8086/notificacoes/status
 * - GET  http://localhost:8086/equipes/status
 */
@RestController
@RequestMapping("/gateway-status")
public class GatewayTestController {

    // ============================================
    // USUARIOS - Porta 8080
    // ============================================
    
    @GetMapping("/usuarios/status")
    public ResponseEntity<Map<String, Object>> usuariosStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Usuários");
        response.put("status", "ONLINE");
        response.put("port", 8080);
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Microservice de Usuários funcionando via Gateway!");
        return ResponseEntity.ok(response);
    }

    // ============================================
    // TAREFAS - Porta 8081
    // ============================================
    
    @GetMapping("/tarefas/status")
    public ResponseEntity<Map<String, Object>> tarefasStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Tarefas");
        response.put("status", "ONLINE");
        response.put("port", 8081);
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Microservice de Tarefas funcionando via Gateway!");
        return ResponseEntity.ok(response);
    }

    // ============================================
    // ANEXOS - Porta 8082
    // ============================================
    
    @GetMapping("/anexos/status")
    public ResponseEntity<Map<String, Object>> anexosStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Anexos");
        response.put("status", "ONLINE");
        response.put("port", 8082);
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Microservice de Anexos funcionando via Gateway!");
        return ResponseEntity.ok(response);
    }

    // ============================================
    // LOGS - Porta 8083
    // ============================================
    
    @GetMapping("/logs/status")
    public ResponseEntity<Map<String, Object>> logsStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Logs");
        response.put("status", "ONLINE");
        response.put("port", 8083);
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Microservice de Logs funcionando via Gateway!");
        return ResponseEntity.ok(response);
    }

    // ============================================
    // NOTIFICAÇÕES - Porta 8084
    // ============================================
    
    @GetMapping("/notificacoes/status")
    public ResponseEntity<Map<String, Object>> notificacoesStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Notificações");
        response.put("status", "ONLINE");
        response.put("port", 8084);
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Microservice de Notificações funcionando via Gateway!");
        return ResponseEntity.ok(response);
    }

    // ============================================
    // EQUIPES - Porta 8085
    // ============================================
    
    @GetMapping("/equipes/status")
    public ResponseEntity<Map<String, Object>> equipesStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Equipes");
        response.put("status", "ONLINE");
        response.put("port", 8085);
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Microservice de Equipes funcionando via Gateway!");
        return ResponseEntity.ok(response);
    }
}
