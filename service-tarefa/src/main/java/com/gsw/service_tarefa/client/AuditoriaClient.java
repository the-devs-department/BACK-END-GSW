package com.gsw.service_tarefa.client;

import com.gsw.service_tarefa.dto.AtribuicaoDTO;
import com.gsw.service_tarefa.dto.AtualizacaoDTO;
import com.gsw.service_tarefa.dto.log.AuditoriaLogDTO;
import com.gsw.service_tarefa.entity.Tarefa;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "service-log", url = "http://localhost:8083/logs")
public interface AuditoriaClient {

    @PostMapping("/registrar-criacao")
    AuditoriaLogDTO registrarCriacao(@RequestBody Tarefa tarefa);
    
    @PostMapping("/registrar-atualizacao")
    void registrarAtualizacao(@RequestBody AtualizacaoDTO taskUpdates);

    @PostMapping("/registrar-exclusao")
    void registrarExclusao(@RequestBody Tarefa tarefa);

    @PostMapping("/registrar-atribuicao")
    void registrarAtribuicao(@RequestBody AtribuicaoDTO  atribuicao);
}
