package com.gsw.service_anexo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gsw.service_anexo.dto.tarefa.TarefaDto;

@FeignClient(name = "service-tarefa", url = "http://localhost:8081/tarefas")
public interface TarefaClient {
  
  @GetMapping("/{id}")
  ResponseEntity<TarefaDto> fetchTaskById(@PathVariable String id);

  @PutMapping("/editar/{id}")
  ResponseEntity<TarefaDto> atualizarTarefa(@PathVariable String id, @RequestBody TarefaDto tarefaAtualizada);
}
