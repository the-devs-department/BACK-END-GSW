package com.gsw.taskmanager.controller;

import com.gsw.taskmanager.dto.UsuarioResponseDto;
import com.gsw.taskmanager.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listarTodos() {
        try {
            return ResponseEntity.ok(usuarioService.listarTodos());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //TODO CRIAR ENDPOINT /GET BY ID

    //TODO CRIAR ENDPOINT /UPDATE

    //TODO CRIAR ENDPOINT /DELETE_BY_ID

}