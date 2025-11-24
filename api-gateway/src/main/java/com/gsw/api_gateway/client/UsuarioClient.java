package com.gsw.api_gateway.client;

import com.gsw.api_gateway.dto.UsuarioDTO;
import com.gsw.api_gateway.dto.UpdatePasswordDTO;
import com.gsw.api_gateway.dto.usuarioResponsavelTarefa.UsuarioResponsavelTarefaDto;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "service-usuario",
        url = "http://localhost:8080/usuarios"
)
public interface UsuarioClient {

    // Buscar usuário responsável por tarefa (corrigido - GET com @PathVariable)
    @GetMapping("/assignedUser/{email}")
    ResponseEntity<UsuarioResponsavelTarefaDto> getUsuarioResponsavelTarefaByEmail(
            @PathVariable String email
    );

    // Buscar usuário por email (permanece igual - depende de como está no MS Usuário)
    @GetMapping("/buscar-usuario-email")
    ResponseEntity<UsuarioDTO> buscarUsuarioPorEmail(
            @RequestParam @Valid String email
    );

    // Atualizar senha
    @PutMapping("/atualizar-senha")
    ResponseEntity<String> atualizarSenhaUsuario(
            @RequestBody UpdatePasswordDTO atualizarSenhaDto
    );
}
