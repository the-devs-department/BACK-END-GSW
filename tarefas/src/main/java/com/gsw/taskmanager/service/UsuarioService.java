package com.gsw.taskmanager.service;

import com.gsw.taskmanager.dto.UsuarioResponseDto;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    public List<UsuarioResponseDto> listarTodos() {

        // buscar no Mongo todos os usuários
        List<Usuario> usuarios = usuarioRepository.findAll();

        // transformar usuario no record UsuarioResponseDto
        List<UsuarioResponseDto> usuariosDto = usuarios.stream()
                .map(usuario -> new UsuarioResponseDto(
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getDataCadastro(),
                        usuario.isAtivo(),
                        usuario.getTarefas()
                )).toList();

        return usuariosDto;
    }
}