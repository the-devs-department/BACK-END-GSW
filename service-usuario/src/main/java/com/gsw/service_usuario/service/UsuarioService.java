package com.gsw.service_usuario.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.gsw.service_usuario.dto.UsuarioResponsavelTarefaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gsw.service_usuario.dto.CriacaoUsuarioDto;
import com.gsw.service_usuario.dto.auth.LoginResponseDto;
import com.gsw.service_usuario.dto.UsuarioAlteracaoDto;
import com.gsw.service_usuario.dto.UsuarioResponseDto;
import com.gsw.service_usuario.entity.Usuario;
import com.gsw.service_usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDto> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .filter(Usuario::isAtivo)
                .map(usuario -> new UsuarioResponseDto(
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getDataCadastro(),
                        true,
                        usuario.getTarefas()
                ))
                .toList();
    }

    public UsuarioResponseDto buscarUsuarioPorId(String uuid) {
        Optional<Usuario> usuarioBuscado = usuarioRepository.findById(uuid);

        return usuarioBuscado.map(usuario -> new UsuarioResponseDto(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCadastro(),
                usuario.isAtivo(),
                usuario.getTarefas()
        )).orElseThrow();
    }

    public LoginResponseDto buscarUsuarioAoLogar(String uuid) {
        Optional<Usuario> usuarioBuscado = usuarioRepository.findById(uuid);

        return usuarioBuscado.map(usuario -> new LoginResponseDto(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRoles()
        )).orElseThrow();
    }

    public UsuarioResponsavelTarefaDto buscarUsuarioResponsavelTarefa(String email) {
        Optional<Usuario> usuarioBuscado = usuarioRepository.findByEmail(email);

        return  usuarioBuscado.map(usuario -> new UsuarioResponsavelTarefaDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()

        )).orElseThrow();
    }

    public UsuarioResponseDto criarUsuario(CriacaoUsuarioDto user) {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail(user.email());

        if (usuarioEncontrado.isPresent()) {
            throw new DataIntegrityViolationException("Usuário já existe");
        }
        Usuario usuario = new Usuario();
        usuario.setEmail(user.email());
        usuario.setNome(user.nome());
        usuario.setSenha(passwordEncoder.encode(user.senha()));
        usuario.setAtivo(true);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setTarefas(new ArrayList<>());
        usuario.setRoles(List.of("ROLE_USER"));

        usuarioRepository.save(usuario);

        return new UsuarioResponseDto(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCadastro(),
                usuario.isAtivo(),
                usuario.getTarefas()
        );
    }

    public UsuarioResponseDto atualizarUsuario(UsuarioAlteracaoDto usuario) {
        Usuario usuarioBanco = usuarioRepository.findById(usuario.id()).orElseThrow();

        if (usuario.email() != null) {
            usuarioBanco.setEmail(usuario.email());
        }
        if (usuario.nome() != null) {
            usuarioBanco.setNome(usuario.nome());
        }
        usuarioRepository.save(usuarioBanco);
        return new UsuarioResponseDto(
                usuarioBanco.getNome(),
                usuarioBanco.getEmail(),
                usuarioBanco.getDataCadastro(),
                usuarioBanco.isAtivo(),
                usuarioBanco.getTarefas()
        );
    }

    public void deletarById(String id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        if (usuario.isAtivo()) {
            usuario.setAtivo(false);
            usuarioRepository.save(usuario);
        } else {
            throw new BusinessException("Usuário já está deletado.");
        }
    }

    public void atualizarSenha(String email, String novaSenha) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }
}