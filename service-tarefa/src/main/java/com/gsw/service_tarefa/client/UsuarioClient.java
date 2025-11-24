package com.gsw.service_tarefa.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.gsw.service_tarefa.dto.UsuarioResponseDTO;

@FeignClient(name = "service-usuario", url = "http://localhost:8080/usuarios")
public interface UsuarioClient {

  @GetMapping("/{id}")
  ResponseEntity<UsuarioResponseDTO> findUserById(@PathVariable String id);
  
}