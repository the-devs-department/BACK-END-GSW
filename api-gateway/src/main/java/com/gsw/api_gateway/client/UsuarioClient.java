package com.gsw.api_gateway.client;

import com.gsw.api_gateway.dto.UsuarioDTO;
import com.gsw.api_gateway.entity.Usuario;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import com.gsw.api_gateway.dto.UpdatePasswordDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-usuario", url = "http://localhost:8080/usuarios")
public interface UsuarioClient {

    @GetMapping("/assignedUser/{email}")
    ResponseEntity<UsuarioResponsavelTarefaDto> getUsuarioResponsavelTarefaByEmail(@RequestBody UsuarioResponsavelTarefaDto usuarioResponsavelTarefaDto);

    @GetMapping("/buscar-usuario-email")
    ResponseEntity<UsuarioDTO> buscarUsuarioPorEmail(@RequestParam @Valid String email);

    @PutMapping("/atualizar-senha")
    ResponseEntity<String> atualizarSenhaUsuario(@RequestBody UpdatePasswordDTO atualizarSenhaDto);

}
