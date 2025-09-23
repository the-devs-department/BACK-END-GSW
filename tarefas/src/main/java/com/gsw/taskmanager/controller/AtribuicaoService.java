package com.gsw.taskmanager.controller;

import com.gsw.taskmanager.dto.UsuarioResponseDto;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.service.TarefaService;
import com.gsw.taskmanager.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AtribuicaoService {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private UsuarioService usuarioService;

    public void atribuirUsuarioATarefa(String tarefaId, String usuarioId) {

        Tarefa tarefa = tarefaService.buscarPorId(tarefaId);

        UsuarioResponseDto usuario = usuarioService.buscarUsuarioPorId(usuarioId);
        System.out.println(usuario.nome());
        System.out.println(tarefa.getId());

        tarefa.setResponsavel(usuario.nome());

        tarefaService.salvarTarefa(tarefa);
    }
}