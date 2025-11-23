package com.gsw.service_equipe.client;

import com.gsw.service_equipe.dto.UsuarioResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "service-usuario", url = "http://localhost:8081/usuarios")
public interface UsuarioClient {

    @GetMapping("/email/{email}")
    ResponseEntity<UsuarioResponseDto> findByEmail(@PathVariable String email);

    @PutMapping("/{email}/atualizar-role")
    void atualizarRole(
            @PathVariable("email") String email,
            @RequestParam("novaRole") String novaRole
    );
}

