package com.gsw.service_log.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.gsw.service_log.dto.tarefa.TarefaDto;


@FeignClient(name = "service-tarefa", url = "http://localhost:8081/tarefas")
public interface TarefaClient {

  @GetMapping
  ResponseEntity<List<TarefaDto>> listarTarefas();
  
  @GetMapping("/{id}")
  ResponseEntity<TarefaDto> buscarPorId(@PathVariable String id);

}
